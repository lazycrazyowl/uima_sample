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






public class ConceptMapperPipeline  {

	private static Logger logger = Logger.getLogger(BaseUimaFitPipeline.class);

	protected static final String[] typeSystemStrs = {
 		"analysis_engine.primitive.DictTerm",
		"org.apache.uima.conceptMapper.support.tokenizer.TokenAnnotation",
		"org.apache.uima.examples.SourceDocumentInformation"
	};

	// Where are these from?
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
	// private in ConceptMapper
  	private static final String PARAM_TOKENIZERDESCRIPTOR = "TokenizerDescriptorPath";



	ConceptMapperPipeline() {
        tsd = TypeSystemDescriptionFactory.createTypeSystemDescription(typeSystemStrs);
    }

	protected List<AnalysisEngineDescription> getPipelineAeDescriptions(
		File dictionaryFile)
	throws UIMAException, IllegalArgumentException, IOException {



		Object[] configData = new Object[] {
			ConceptMapper.PARAM_DICT_FILE,  dictionaryFile.getAbsolutePath(),
			//"DictionaryFileName",  dictionaryFile.getAbsolutePath(),
			//"DictionaryFile",  dictionaryFile.getAbsolutePath()
			//ConceptMapper.PARAM_TOKENIZERDESCRIPTOR, "analysis_engine.primitive.OffsetTokenizer"
			PARAM_TOKENIZERDESCRIPTOR, "target/classes/descriptors/analysis_engine/primitive/OffsetTokenizer.xml"
		};
		AnalysisEngineDescription conceptMapperDescription = 
			AnalysisEngineFactory.createAnalysisEngineDescription(
				"analysis_engine.primitive.ConceptMapperOffsetTokenizer",
				ConceptMapper.PARAM_DICT_FILE,  dictionaryFile.getAbsolutePath(),
				PARAM_TOKENIZERDESCRIPTOR, "target/classes/descriptors/analysis_engine/primitive/OffsetTokenizer.xml"
				);






        //AnalysisEngineDescription offsetTokenizerDescription = OffsetTokenizerFactory.buildOffsetTokenizerDescription(
        //        tsd, 
	//			OffsetTokenizerFactory.buildConfigurationData(CaseMatchParamValue.CASE_IGNORE)
		//);


		List<AnalysisEngineDescription> descriptions = new ArrayList<AnalysisEngineDescription>();
		descriptions.add(conceptMapperDescription);
		//descriptions.add(rdfDesc);
		//if (mentionNameConverterDesc != null) { descriptions.add(mentionNameConverterDesc); }
		//descriptions.add(bionlpDesc);

		return descriptions;
	}


	public void go(File dictionaryFile, File inputDir)
	throws UIMAException, ResourceInitializationException, FileNotFoundException, IOException {

		List<AnalysisEngineDescription> aeDescList
			= getPipelineAeDescriptions(dictionaryFile);

        CollectionReader cr = CollectionReaderFactory.createCollectionReader(
			FileSystemCollectionReader.class,
			tsd,
			FileSystemCollectionReader.PARAM_INPUTDIR,	inputDir,
			FileSystemCollectionReader.PARAM_ENCODING,	"UTF-8",
			FileSystemCollectionReader.PARAM_LANGUAGE, 	"English",
			FileSystemCollectionReader.PARAM_XCAS, 		"false",
			FileSystemCollectionReader.PARAM_LENIENT,	"true"
        );


		SimplePipeline.runPipeline(cr, aeDescList.toArray(new AnalysisEngineDescription[0]));
    }

	
	protected static void usage() {
		System.out.println("mvn exec:java -Dinput=<input tree> -Ddictionar=<dictionary file>");
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
