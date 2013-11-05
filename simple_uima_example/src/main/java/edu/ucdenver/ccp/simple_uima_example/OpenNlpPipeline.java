package edu.ucdenver.ccp.simple_uima_example;


import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.jcas.JCas;
import org.apache.uima.tools.components.FileSystemCollectionReader;
import org.apache.uima.examples.SourceDocumentInformation;
import org.apache.uima.util.InvalidXMLException;

import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.CollectionReaderFactory;
import org.uimafit.factory.ExternalResourceFactory;
import org.uimafit.factory.TypeSystemDescriptionFactory;
import org.uimafit.pipeline.SimplePipeline;
import org.uimafit.component.xwriter.CASDumpWriter;

import opennlp.uima.util.UimaUtil;
import opennlp.uima.sentdetect.SentenceDetector;
import opennlp.uima.sentdetect.SentenceModelResourceImpl;
import opennlp.uima.tokenize.Tokenizer;
import opennlp.uima.tokenize.TokenizerModelResourceImpl;

import edu.ucdenver.ccp.simple_uima_example.types.SentenceAnnotation;
import edu.ucdenver.ccp.simple_uima_example.types.TokenAnnotation;


public class OpenNlpPipeline  {

	private static Logger logger = Logger.getLogger(ProteinPipeline.class);

	protected static final String[] typeSystemStrs = {
		"org.apache.uima.examples.SourceDocumentInformation",
		"descriptors.TutorialTypeSystem"
	};


	final String sentenceModelUrl = "http://opennlp.sourceforge.net/models-1.5/en-sent.bin"; 
	final String tokenizerModelUrl = "http://opennlp.sourceforge.net/models-1.5/en-token.bin";


	protected TypeSystemDescription tsd;
    CollectionReader cr;
	List<AnalysisEngineDescription> aeDescList;


	OpenNlpPipeline(File inputDir) throws ResourceInitializationException {
        tsd = TypeSystemDescriptionFactory.createTypeSystemDescription(typeSystemStrs);

        cr = CollectionReaderFactory.createCollectionReader(
			FileSystemCollectionReader.class,
			tsd,
			FileSystemCollectionReader.PARAM_INPUTDIR,	inputDir,
			FileSystemCollectionReader.PARAM_ENCODING,	"UTF-8",
			FileSystemCollectionReader.PARAM_LANGUAGE, 	"English",
			FileSystemCollectionReader.PARAM_XCAS, 		"false",
			FileSystemCollectionReader.PARAM_LENIENT,	"true"
        );

		aeDescList = new ArrayList<AnalysisEngineDescription>();

		// OpenNLP Sentence Detector
		try {
			AnalysisEngineDescription sentenceDesc = AnalysisEngineFactory.createPrimitiveDescription(
				SentenceDetector.class, 
				UimaUtil.SENTENCE_TYPE_PARAMETER, SentenceAnnotation.class.getName());
			ExternalResourceFactory.createDependencyAndBind(sentenceDesc, 
				UimaUtil.MODEL_PARAMETER, SentenceModelResourceImpl.class, 
				sentenceModelUrl);
			aeDescList.add(sentenceDesc);
		}
		catch (InvalidXMLException e) {
			throw new ResourceInitializationException(e);
		}

		// OpenNLP Tokenizer
		try {
			AnalysisEngineDescription tokenizerDesc = AnalysisEngineFactory.createPrimitiveDescription(
				Tokenizer.class, 
				UimaUtil.SENTENCE_TYPE_PARAMETER, SentenceAnnotation.class.getName(),
				UimaUtil.TOKEN_TYPE_PARAMETER, TokenAnnotation.class.getName());
			ExternalResourceFactory.createDependencyAndBind(tokenizerDesc, 
				UimaUtil.MODEL_PARAMETER, TokenizerModelResourceImpl.class, 
				tokenizerModelUrl);
			aeDescList.add(tokenizerDesc);
		}
		catch (InvalidXMLException e) {
			throw new ResourceInitializationException(e);
		}

		aeDescList.add(AnalysisEngineFactory.createPrimitiveDescription(
			ParamProteinAnnotator.class,
			ParamProteinAnnotator.PARAM_PATTERN_STRING, "\\b([ABCDEFGHIJKLMNOPQRSTUVWXYZ]?)-(\\d+)"));

		aeDescList.add(AnalysisEngineFactory.createPrimitiveDescription(
			CASDumpWriter.class,
			CASDumpWriter.PARAM_OUTPUT_FILE, "output.txt"));

		aeDescList.add(AnalysisEngineFactory.createPrimitiveDescription(
			ProteinReporter.class));

		aeDescList.add(AnalysisEngineFactory.createPrimitiveDescription(
			Debug_AE.class));

    }


	public void go(File inputDir)
	throws UIMAException, ResourceInitializationException, FileNotFoundException, IOException {
		SimplePipeline.runPipeline(cr, aeDescList.toArray(new AnalysisEngineDescription[0]));
    }

	
	protected static void usage() {
		System.out.println("mvn exec:java -Dinput=<input tree> ");
		System.out.println("  Set numToProcess to -1 to process all files under input tree.");
	}


	public static void main(String[] args) {

		BasicConfigurator.configure();
		File inputDir = null;

		// get args
		if (args.length < 1) {
			usage();
			System.exit(1);
		}
		try {
			inputDir = new File(args[0]);
		
		} catch(Exception x) {
			System.out.println("error: " + x);
			x.printStackTrace();
			usage();
			System.exit(2);
		}


		// main part
		try {
			OpenNlpPipeline pipeline = new OpenNlpPipeline (inputDir);
			pipeline.go(inputDir);
		}
		catch(Exception x) {
			System.err.println("error: " + x);
			x.printStackTrace();
			System.exit(3);
		}
	}


}
