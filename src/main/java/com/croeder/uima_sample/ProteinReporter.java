package com.croeder.uima_sample;

import static org.uimafit.util.JCasUtil.select;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

import org.apache.uima.jcas.JCas;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.analysis_engine.AnalysisEngine;

//import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.JCasFactory;


/**
 * Example annotator  just reports what Proteins it finds in the CAS.
 */
public class ProteinReporter extends JCasAnnotator_ImplBase {

	Logger logger = Logger.getLogger(GetStartedQuickAE.class);

	public void process(JCas jCas) {
		for (Protein prot : select(jCas, Protein.class)) {
            String word = prot.getCoveredText();
			System.out.println(word + " prefix:" + prot.getPrefix() + ", " + prot.getSuffix());
        }

	
	}

}
