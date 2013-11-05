package edu.ucdenver.ccp.simple_uima_example;

import static java.lang.System.out;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JFSIndexRepository;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.cas.text.AnnotationIndex;

import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.descriptor.SofaCapability;

import edu.ucdenver.ccp.simple_uima_example.types.SentenceAnnotation;
import edu.ucdenver.ccp.simple_uima_example.types.TokenAnnotation;
import edu.ucdenver.ccp.simple_uima_example.Protein;

import org.apache.log4j.Logger;

@SofaCapability
public class Debug_AE extends JCasAnnotator_ImplBase {

	Logger logger = Logger.getLogger(Debug_AE.class);

    @Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
    }


    @Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {

        FSIterator<Annotation> annotIter = jcas.getJFSIndexRepository().getAnnotationIndex().iterator();
        while (annotIter.hasNext()) {

	        Annotation annot = (Annotation) annotIter.next();
			if (annot instanceof TokenAnnotation) {
				TokenAnnotation ta = (TokenAnnotation) annot;
				out.println("token: " + ta.getBegin() + ", " + ta.getEnd() + " \"" 
					+ ta.getCoveredText() + "\" " + ta.getPos());
			}
			if (annot instanceof SentenceAnnotation) {
				SentenceAnnotation sa = (SentenceAnnotation) annot;
				out.println("sentence: " + sa.getBegin() + ", " + sa.getEnd() + " \"" 
					+ sa.getCoveredText() + "\"");
			}
			if (annot instanceof Protein) {
				Protein pa = (Protein) annot;
				out.println("protein: " + pa.getBegin() + ", " + pa.getEnd() + " \"" 
					+ pa.getCoveredText() + "\" " + pa.getPrefix() + ", " + pa.getSuffix());
			}
		}

    }

    @Override
	public void collectionProcessComplete() throws AnalysisEngineProcessException {
        super.collectionProcessComplete();
    }
    
	public static AnalysisEngine createAnalysisEngine(TypeSystemDescription tsd)
	throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitive(
				Debug_AE.class, tsd
		);
	}
	public static AnalysisEngineDescription createAnalysisEngineDescription(TypeSystemDescription tsd)
	throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitiveDescription(
				Debug_AE.class, tsd
		);
	}
	
	

}
