/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

//package org.apache.uima.tutorial.ex1;
package edu.ucdenver.ccp.simple_uima_example;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

import org.apache.uima.jcas.JCas;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.UimaContext;

import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.ConfigurationParameterFactory;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.JCasFactory;


public class ParamProteinAnnotator extends JCasAnnotator_ImplBase {

	Logger logger = Logger.getLogger(ProteinAnnotator.class);


	public static final String PARAM_PATTERN_STRING 
		= ConfigurationParameterFactory.createConfigurationParameterName(
			ParamProteinAnnotator.class, "patternString");

	@ConfigurationParameter(mandatory=true, description="a regex string", defaultValue="(.*)")
	private String patternString;
	
	private Pattern proteinPattern;

	@Override
	public void initialize(UimaContext context) 
	throws ResourceInitializationException {
		super.initialize(context);

		logger.info("------getting pattern: \"" + patternString + "\"");
		proteinPattern = Pattern.compile(patternString);
		if (proteinPattern == null) {
			logger.error("error: getting pattern: \"" + patternString + "\"");
			throw new ResourceInitializationException(new Exception("error compile pattern:" + patternString));	
		}
	}


  /**
   * @see JCasAnnotator_ImplBase#process(JCas)
   */
  public void process(JCas aJCas) {
	logger.info("processing");
	logger.info("getting pattern: \"" + patternString + "\"");
    String docText = aJCas.getDocumentText();
    Matcher matcher = proteinPattern.matcher(docText);
	logger.info("running protein annotator");

    while (matcher.find()) {
      // create annotation
      Protein annotation = new Protein(aJCas);
      annotation.setBegin(matcher.start());
      annotation.setEnd(matcher.end());

      String prefix=matcher.group(1);
      annotation.setPrefix(prefix);

      String suffix=matcher.group(2);
      annotation.setSuffix(suffix);
	  logger.info("found: " + prefix + " - " + suffix);

	  // add annotation to indexes
      annotation.addToIndexes();
    }
  }

}
