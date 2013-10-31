package edu.ucdenver.ccp.simple_uima_example;


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
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.jcas.JCas;
import org.apache.uima.tools.components.FileSystemCollectionReader;

import org.apache.uima.examples.SourceDocumentInformation;


import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.CollectionReaderFactory;
import org.uimafit.factory.TypeSystemDescriptionFactory;
import org.uimafit.pipeline.SimplePipeline;
import org.uimafit.pipeline.JCasIterable;
import org.uimafit.component.xwriter.CASDumpWriter;

// http://code.google.com/p/dkpro-core-asl/wiki/MyFirstDKProProject#Create_your_first_experiment



public class ProteinPipeline  {

	private static Logger logger = Logger.getLogger(ProteinPipeline.class);

	protected static final String[] typeSystemStrs = {
		"org.apache.uima.examples.SourceDocumentInformation",
		"descriptors.TutorialTypeSystem"
	};


	protected TypeSystemDescription tsd;
    CollectionReader cr;
	List<AnalysisEngineDescription> aeDescList;


	ProteinPipeline(File inputDir) throws ResourceInitializationException {
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

		aeDescList.add(AnalysisEngineFactory.createPrimitiveDescription(
			ProteinAnnotator.class));

		aeDescList.add(AnalysisEngineFactory.createPrimitiveDescription(
			CASDumpWriter.class,
			CASDumpWriter.PARAM_OUTPUT_FILE, "output.txt"));

		aeDescList.add(AnalysisEngineFactory.createPrimitiveDescription(
			ProteinReporter.class));

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
			System.out.println("error:" + x);
			x.printStackTrace();
			usage();
			System.exit(2);
		}


		// main part
		try {
			ProteinPipeline pipeline = new ProteinPipeline (inputDir);
			pipeline.go(inputDir);
		}
		catch(Exception x) {
			System.err.println(x);
			x.printStackTrace();
			System.exit(3);
		}
	}


}
