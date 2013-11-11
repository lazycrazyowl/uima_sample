/*
 Copyright (c) 2012, Regents of the University of Colorado
 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification,
 are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
    list of conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 * Neither the name of the University of Colorado nor the names of its
    contributors may be used to endorse or promote products derived from this
    software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.ucdenver.ccp.simple_uima_example;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

import org.apache.uima.jcas.JCas;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceInitializationException;
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
