// Copyright 2012, Chris Roeder 
package com.croeder.uima_sample;;


import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.jcas.JCas;
import org.apache.uima.pear.util.FileUtil;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.resource.metadata.ResourceMetaData;
import org.apache.uima.tools.components.FileSystemCollectionReader;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.util.XMLSerializer;

import org.apache.uima.conceptMapper.ConceptMapper; 
import org.apache.uima.conceptMapper.DictTerm;
import org.apache.uima.conceptMapper.support.tokens.TokenFilter;
import org.apache.uima.conceptMapper.support.tokens.TokenNormalizer;
import org.apache.uima.conceptMapper.support.dictionaryResource.DictionaryResource_impl;

import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.CollectionReaderFactory;
import org.uimafit.factory.TypeSystemDescriptionFactory;
import org.uimafit.factory.JCasFactory;
import org.uimafit.pipeline.SimplePipeline;
import org.uimafit.pipeline.JCasIterable;
import org.uimafit.component.xwriter.CASDumpWriter;
import org.uimafit.component.xwriter.XWriter;

import uima.tt.TokenAnnotation;

import com.croeder.uima_sample.analysis_engines.LingPipeSentenceDetector_AE;

import org.xml.sax.SAXException;



public class ConceptMapperPipeline  {
	private static Logger logger = Logger.getLogger(BaseUimaFitPipeline.class);

	protected static final String[] typeSystemStrs = {
 		"analysis_engine.primitive.DictTerm",

		////uima.tt.SentenceAnnotation
		//"edu.ucdenver.ccp.nlp.ext.uima.types.Sentence",
		//"edu.ucdenver.ccp.nlp.ext.uima.annotation.syntax.TypeSystem",
		//"edu.ucdenver.ccp.nlp.core.uima.TypeSystem",
		//"edu.ucdenver.ccp.nlp.ext.uima.annotators.sentencedetectors.TypeSystem",
		//"edu.ucdenver.ccp.nlp.core.uima.TypeSystem",

		"com.croeder.uima_sample.TypeSystem",

		"org.apache.uima.conceptMapper.support.tokenizer.TokenAnnotation",
		"org.apache.uima.examples.SourceDocumentInformation"
	};

	protected TypeSystemDescription tsd;


	ConceptMapperPipeline() {
        tsd = TypeSystemDescriptionFactory.createTypeSystemDescription(typeSystemStrs);
    }


	protected List<AnalysisEngine> getPipelineEngines(
		File dictionaryFile)
	throws UIMAException, IllegalArgumentException, IOException {

		List<AnalysisEngine> engines = new ArrayList<AnalysisEngine>();

		// OpenNLP Sentence Detector by way of ClearTK
		// not work because these sentences are derived from uima.tcas.Annotation
        // instead of org.apache.uima.jcas.tcas.Annotation...
		//descriptions.add(SentenceAnnotator.getDescription());


        // SENTENCE DETECTOR - CCP
        //AnalysisEngineDescription sentenceDetectorDesc
        //   = LingPipeSentenceDetector_AE.createAnalysisEngineDescription(tsd, ExplicitSentenceCasInserter.class, true);
		//engines.add(UIMAFramework.produceAnalysisEngine(sentenceDetectorDesc));

        // SENTENCE DETECTOR - CROEDER
        AnalysisEngineDescription sentenceDetectorDesc
           = LingPipeSentenceDetector_AE.createAnalysisEngineDescription(tsd);
		engines.add(UIMAFramework.produceAnalysisEngine(sentenceDetectorDesc));


		Object[] config = new Object[0];;
		// TOKENIZER from xml files ** THE PATH MUST BE A FILE SYSTEM PATH **
		AnalysisEngine tokenizerAE = 
			AnalysisEngineFactory.createAnalysisEngineFromPath(
				"target/classes/descriptors/analysis_engine/primitive/OffsetTokenizer.xml", config);
		engines.add(tokenizerAE);

		//CONCEPT MAPPER from xml files ** ...FILE SYSTEM PATH **
		AnalysisEngine conceptMapperAE = 
			AnalysisEngineFactory.createAnalysisEngineFromPath(
				"target/classes/descriptors/analysis_engine/primitive/ConceptMapperOffsetTokenizer.xml", config);
		engines.add(conceptMapperAE);


		// CAS Dumper
        AnalysisEngineDescription aeD =  AnalysisEngineFactory.createPrimitiveDescription(
            CASDumpWriter.class,
            CASDumpWriter.PARAM_OUTPUT_FILE, "cm_output.txt");
		engines.add(UIMAFramework.produceAnalysisEngine(aeD));



		// Dict Term Reporter 
        //descriptions.add(AnalysisEngineFactory.createPrimitiveDescription( DictTermReporter.class));

		return engines;
	}


	public void go(File dictionaryFile, File inputDir)
	throws UIMAException, ResourceInitializationException, FileNotFoundException, IOException {

		List<AnalysisEngine> aeList
			= getPipelineEngines(dictionaryFile);



        CollectionReader reader = CollectionReaderFactory.createCollectionReader(
			FileSystemCollectionReader.class,
			tsd,
			FileSystemCollectionReader.PARAM_INPUTDIR,	inputDir,
			FileSystemCollectionReader.PARAM_ENCODING,	"UTF-8",
			FileSystemCollectionReader.PARAM_LANGUAGE, 	"English",
			FileSystemCollectionReader.PARAM_XCAS, 		"false",
			FileSystemCollectionReader.PARAM_LENIENT,	"true"
        );


        AnalysisEngine xWriter = AnalysisEngineFactory.createPrimitive(XWriter.class, tsd,
                XWriter.PARAM_OUTPUT_DIRECTORY_NAME, "./");
	
		aeList.add(xWriter);

		SimplePipeline.runPipeline(reader, aeList.toArray(new AnalysisEngine[0]));
    }

	
	protected static void usage() {
	 	System.out.println("mvn exec:java -Dinput=<input tree> -Ddictionar=<dictionary file>");
		System.out.println("mvn exec:java -Dinput=/Users/roederc/data/fulltext/pmc/Yeast -Ddictionary=classes/dictionaries/cmDict-APO.xml");
	}


	public static void main(String[] args) {

		if (args.length < 2) {
			usage();
			System.exit(1);
		}

			File dictionaryFile  = null;
			File inputDir = null;
		try {
			inputDir = new File(args[0]);
			dictionaryFile  = new File(args[1]);;
		
		} catch(Exception x) {
			System.out.println("error:" + x);
			x.printStackTrace();
			usage();
			System.exit(2);
		}
		try {
			ConceptMapperPipeline pipeline = new ConceptMapperPipeline ();
	
			BasicConfigurator.configure();
	
			System.out.println("going with "
				+ " dictionaryFile:" + dictionaryFile
				+ " inputDir:" + inputDir);	
			pipeline.go(dictionaryFile,  inputDir);
		}
		catch(Exception x) {
			System.err.println(x);
			x.printStackTrace();
			System.exit(3);
		}
	}
}
