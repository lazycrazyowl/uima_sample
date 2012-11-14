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
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.jcas.JCas;
import org.apache.uima.tools.components.FileSystemCollectionReader;

import org.apache.uima.conceptMapper.ConceptMapper;

import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.CollectionReaderFactory;
import org.uimafit.factory.TypeSystemDescriptionFactory;
import org.uimafit.pipeline.SimplePipeline;
import org.uimafit.pipeline.JCasIterable;






public class BaseUimaFitPipeline  {

	private static Logger logger = Logger.getLogger(BaseUimaFitPipeline.class);

	protected static final String[] typeSystemStrs = {
 		"analysis_engine.primitive.DictTerm",
		"org.apache.uima.conceptMapper.support.tokenizer.TokenAnnotation"
	};

    public static String  STOPWORDS[] = {"i", "a", "is", "if", "be", "mm", "of", "no",
            "so", "mg", "ml", "as", "we", "at", "by", "in", "on", "or", "it", "to", "do", "kg", "km", "an", "did",
            "due", "any", "for", "use", "may", "and", "etc", "are", "but", "can", "our", "how", "nor", "the", "has",
            "was", "had", "its", "all", "this", "done", "just", "also", "make", "upon", "show", "used", "very", "from",
            "most", "were", "must", "some", "what", "have", "than", "here", "that", "such", "when", "been", "with",
            "both", "them", "then", "into", "seem", "thus", "each", "made", "seen", "does", "they", "these", "about",
            "quite", "again", "which", "found", "might", "shown", "using", "among", "those", "shows", "since", "while",
            "being", "their", "often", "would", "there", "could", "during", "nearly", "having", "mostly", "enough",
            "always", "either", "mainly", "should", "showed", "before", "within", "theirs", "itself", "rather",
            "really", "almost", "perhaps", "through", "several", "another", "various", "further", "because", "neither",
            "between", "however", "without", "overall", "obtained", "although", "therefore", "regarding", "especially",
            "significantly"};

	protected TypeSystemDescription tsd;


	BaseUimaFitPipeline() {
        tsd = TypeSystemDescriptionFactory.createTypeSystemDescription(typeSystemStrs);
    }

	protected List<AnalysisEngineDescription> getPipelineAeDescriptions(
		File dictionaryFile,
		File a1OutputDir,
		File rdfOutputDir)
	throws UIMAException, IllegalArgumentException, IOException {

//desc/analysis_engine/aggregate/OffsetTokenizerMatcher.xml

		//AnalysisEngineDescription offsetTokenizerDescription =

		Object[] configData = new Object[] {
			ConceptMapper.PARAM_DICT_FILE,  dictionaryFile.getAbsolutePath()
		};
		AnalysisEngineDescription conceptMapperDescription = 
			AnalysisEngineFactory.createAnalysisEngineDescription(
				"analysis_engine.primitive.ConceptMapperOffsetTokenizer",
				configData);

        //AnalysisEngineDescription offsetTokenizerDescription = OffsetTokenizerFactory.buildOffsetTokenizerDescription(
        //        tsd, 
	//			OffsetTokenizerFactory.buildConfigurationData(CaseMatchParamValue.CASE_IGNORE)
		//);

/****
		// RDF Serialization 
		// AnalysisEngine rdf = RdfSerialization_AE.createAnalysisEngine(
		AnalysisEngineDescription rdfDesc = RdfSerialization_AE.createAnalysisEngineDescription(
                tsd,
                rdfOutputDir,
                "proteins", // output file prefix
                2000, // batch size
                1 // batch number
			);
****/

/*****
		// CLASS MENTION CONVERTER
		// GO is too general, this uses the name of the dictionary to change the id's
		// so they include a clue of the GO sub-ontology. 
		// change GO:0001234 to GO:CC_0001234 if GO-CC is what you ran.
        AnalysisEngineDescription  mentionNameConverterDesc = null;
		if (!goSubOntologyName.equals("")) {
        	mentionNameConverterDesc =
    			 ClassMentionConverter_AE.createAnalysisEngineDescription(tsd,
					"GO:" + goSubOntologyName + "$1", new String[] { "GO:(.*)" });
		}

		// Annotation Filter
		// ???


		// BIONLP Serialization
		// The "true" here says to add a 2nd line for normalized entities.
 		// It requires a slot for the canonicalized name from Concept Mapper (see above).
		AnalysisEngineDescription bionlpDesc = BionlpFormatPrinter_AE.createAnalysisEngineDescription(
                tsd ,a1OutputDir, true);
*****/

		List<AnalysisEngineDescription> descriptions = new ArrayList<AnalysisEngineDescription>();
		descriptions.add(conceptMapperDescription);
		//descriptions.add(rdfDesc);
		//if (mentionNameConverterDesc != null) { descriptions.add(mentionNameConverterDesc); }
		//descriptions.add(bionlpDesc);

		return descriptions;
	}


	public void go(int numToSkip, int numToProcess, File dictionaryFile, File a1OutputDir, File rdfOutputDir, File inputDir, String extension)
	throws UIMAException, ResourceInitializationException, FileNotFoundException, IOException {

		List<AnalysisEngineDescription> aeDescList
			= getPipelineAeDescriptions(dictionaryFile, a1OutputDir, rdfOutputDir);
		AnalysisEngineDescription[] aeDescriptions 
			= aeDescList.toArray(new AnalysisEngineDescription[0]);



        CollectionReader cr = CollectionReaderFactory.createCollectionReader(
			FileSystemCollectionReader.class,
			FileSystemCollectionReader.PARAM_INPUTDIR,	inputDir,
			FileSystemCollectionReader.PARAM_ENCODING,	"UTF-8",
			FileSystemCollectionReader.PARAM_LANGUAGE, 	"English",
			FileSystemCollectionReader.PARAM_XCAS, 		"false",
			FileSystemCollectionReader.PARAM_LENIENT,	"true"
        );


		SimplePipeline.runPipeline(cr, aeDescriptions);
    }

	
	protected static void usage() {
		System.out.println("java -cp . <this class> <dict dir> <input tree> <a1 output dir> <rdf output dir> <numToSkip> <numToProcess> [<input file extension>]");
		System.out.println("  Set numToProcess to -1 to process all files under input tree.");
	}


	public static void main(String[] args) {

		if (args.length < 2) {
			usage();
			System.exit(1);
		}

			File dictionaryFile  = null;
			File inputDir = null;
			File a1OutputDir   = null;
			File rdfOutputDir   = null;
			int numToSkip=0;
			int numToProcess=0;
			String extension="";
		try {
			dictionaryFile  = new File(args[0]);;
			inputDir = new File(args[1]);
			a1OutputDir   = new File(args[2]);
			rdfOutputDir   = new File(args[3]);
			numToSkip=Integer.parseInt(args[4]);
			numToProcess=Integer.parseInt(args[5]);
			if (args.length > 6 ) {
				extension = args[6];
			}
		
		} catch(Exception x) {
			System.out.println("error:" + x);
			x.printStackTrace();
			usage();
			System.exit(2);
		}
		try {
			BaseUimaFitPipeline pipeline = new BaseUimaFitPipeline ();
	
			BasicConfigurator.configure();
	
	        if (a1OutputDir.exists())  {
				System.err.println("a1 output directory exists and will not be over-written.: " + a1OutputDir.getAbsolutePath() );
				System.exit(1);
			}
			else {
				a1OutputDir.mkdirs();
			}

	        if (rdfOutputDir.exists())  {
				System.err.println("rdf output directory exists and will not be over-written.: " + rdfOutputDir.getAbsolutePath() );
				System.exit(1);
			}
			else {
				rdfOutputDir.mkdirs();
			}
			System.out.println("going with numToSkip:" + numToSkip 
				+ " numToProcess:" + numToProcess
				+ " dictionaryFile:" + dictionaryFile
				+ " a1OutputDir:" + a1OutputDir
				+ " inputDir:" + inputDir);	
			pipeline.go(numToSkip, numToProcess, dictionaryFile,  a1OutputDir, rdfOutputDir, inputDir, extension);
		}
		catch(Exception x) {
			System.err.println(x);
			x.printStackTrace();
			System.exit(3);
		}
	}

}
