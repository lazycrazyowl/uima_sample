package com.croeder.uima_sample.analysis_engines;

import java.io.IOException;

import org.junit.Test;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.UIMAException;
import org.apache.uima.resource.ResourceInitializationException;

import org.uimafit.pipeline.SimplePipeline;

import com.croeder.uima_sample.annotation.SentenceAnnotation;
import com.croeder.uima_sample.analysis_engines.LingPipeSentenceDetector_AE;

public class LingPipeSentenceDetectorAeTest  extends AnalysisEngineTest {

	@Test
	public void test()
	throws ResourceInitializationException, UIMAException, IOException {
		AnalysisEngineDescription aed  
			=  LingPipeSentenceDetector_AE.createAnalysisEngineDescription(tsd);
		SimplePipeline.runPipeline(jCas, aed);

		FSIterator iter = jCas.getJFSIndexRepository().getAnnotationIndex(SentenceAnnotation.type).iterator();
		while (iter.hasNext()) {
			SentenceAnnotation sentence = (SentenceAnnotation) iter.next();
		}

	}

	protected void addJcasData() 
	throws UIMAException, IOException {
		String documentText  
			 //12345678901234567890123456789012345678901234567890
			 //00000000011111111112222222222333333333344444444445
		    = "Lorem ipsum dolor sit amet, consectetur adipiscing"
			+ " elit. Vivamus volutpat ut dui ut pretium. Praesent non quam massa. ";
			 //123456789012345678901234567890123456789012345678901234567890123456789
			 //555555555666666666677777777778888888888999999999900000000001111111111

		jCas.setDocumentText(documentText);
		SentenceAnnotation  sa;
		sa = new SentenceAnnotation(jCas, 01,57); sa.addToIndexes();
		sa = new SentenceAnnotation(jCas, 58,118); sa.addToIndexes();
	}
}
