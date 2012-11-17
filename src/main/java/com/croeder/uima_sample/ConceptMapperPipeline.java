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
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.jcas.JCas;
import org.apache.uima.pear.util.FileUtil;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.tools.components.FileSystemCollectionReader;

import org.apache.uima.conceptMapper.ConceptMapper; 
import org.apache.uima.conceptMapper.DictTerm;
import org.apache.uima.conceptMapper.support.tokens.TokenFilter;
import org.apache.uima.conceptMapper.support.dictionaryResource.DictionaryResource_impl;

import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.CollectionReaderFactory;
import org.uimafit.factory.TypeSystemDescriptionFactory;
import org.uimafit.pipeline.SimplePipeline;
import org.uimafit.pipeline.JCasIterable;
import org.uimafit.component.xwriter.CASDumpWriter;



import uima.tt.TokenAnnotation;


public class ConceptMapperPipeline  {

	private static Logger logger = Logger.getLogger(BaseUimaFitPipeline.class);

	protected static final String[] typeSystemStrs = {
 		"analysis_engine.primitive.DictTerm",
		"org.apache.uima.conceptMapper.support.tokenizer.TokenAnnotation",
		"org.apache.uima.examples.SourceDocumentInformation"
	};

	protected TypeSystemDescription tsd;
  	private static final String PARAM_TOKENIZERDESCRIPTOR = "TokenizerDescriptorPath";


	ConceptMapperPipeline() {
        tsd = TypeSystemDescriptionFactory.createTypeSystemDescription(typeSystemStrs);
    }


	protected List<AnalysisEngineDescription> getPipelineAeDescriptions(
		File dictionaryFile)
	throws UIMAException, IllegalArgumentException, IOException {

		List<AnalysisEngineDescription> descriptions = new ArrayList<AnalysisEngineDescription>();


		// Concept Mapper
 		//File tokenizerDescriptorPath = FileUtil.createTempFile("tokenizer-desc", "xml");
        //UIMA_Util.outputDescriptorToFile(tokenizerDescription, tokenizerDescriptorPath);

		AnalysisEngineDescription conceptMapperDescription = 
			AnalysisEngineFactory.createAnalysisEngineDescription(
				"analysis_engine.primitive.ConceptMapperOffsetTokenizer",
				ConceptMapper.PARAM_DICT_FILE,  dictionaryFile.getAbsolutePath(),
				"TokenizerDescriptorPath", "target/classes/descriptors/analysis_engine/primitive/OffsetTokenizer.xml",
				//"TokenizerDescriptorPath", tokenizerDescriptorPath,
				ConceptMapper.PARAM_ATTRIBUTE_LIST, 	new String[] { "canonical" },	
				//ConceptMapper.PARAM_ATTRIBUTE_LIST, 	new String[] { "canonical", "id" },	
                //ConceptMapper.PARAM_FEATURE_LIST,		new String[] {"DictCanon", "ID"},
                ConceptMapper.PARAM_FEATURE_LIST,		new String[] {"DictCanon"},
                ConceptMapper.PARAM_MATCHEDFEATURE,		"matchedText",
                ConceptMapper.PARAM_ANNOTATION_NAME,	DictTerm.class.getName(),
                ConceptMapper.PARAM_ENCLOSINGSPAN,		"enclosingSpan",
                ConceptMapper.PARAM_TOKENANNOTATION,	TokenAnnotation.class.getName(),
				// tokenClassFeatuerName,tokenTextFeatureName, tokenTypeFeaturename
                ConceptMapper.PARAM_TOKENCLASSWRITEBACKFEATURENAMES, new String[0],
                TokenFilter.PARAM_EXCLUDEDTOKENCLASSES, new String[0],
                TokenFilter.PARAM_EXCLUDEDTOKENTYPES, 	new Integer[0],
                TokenFilter.PARAM_INCLUDEDTOKENCLASSES, new String[0],
                TokenFilter.PARAM_INCLUDEDTOKENTYPES, 	new Integer[0],
                "LanguageID", 							"en",
                DictionaryResource_impl.PARAM_DUMPDICT, true 
				);
		ExternalResourceDescription[] externalResources 
			= conceptMapperDescription.getResourceManagerConfiguration().getExternalResources();
        ExternalResourceDescription dictionaryFileResourceDesc = externalResources[0];
        dictionaryFileResourceDesc.getResourceSpecifier().setAttributeValue(
			"fileUrl", dictionaryFile.getAbsolutePath());
        conceptMapperDescription.getResourceManagerConfiguration().setExternalResources(
                new ExternalResourceDescription[] { dictionaryFileResourceDesc });

		// DEBUG org.springframework.validation.DataBinder  - DataBinder requires binding of required fields [outFile,writeDocumentMetaData,rawFeaturePatterns,rawTypePatterns]


		descriptions.add(conceptMapperDescription);

        descriptions.add(AnalysisEngineFactory.createPrimitiveDescription(
            CASDumpWriter.class,
            CASDumpWriter.PARAM_OUTPUT_FILE, "cm_output.txt"));

        descriptions.add(AnalysisEngineFactory.createPrimitiveDescription(
			DictTermReporter.class));

		return descriptions;
	}

/***
  		public static Object[] buildConfigurationData(
			String[] attributeList, 
			File dictionaryFile, 
			String[] featureList,
            boolean findAllMatches, 
			String matchedTokensFeatureName, 
			boolean orderIndependentLookup,
            String resultingAnnotationMatchedTextFeature, 
			Class<? extends Annotation> resultingAnnotationClass,
            String resultingEnclosingSpanName, 
			SearchStrategyParamValue searchStrategyParamValue,
            Class<? extends Annotation> spanFeatureStructureClass, 
			Class<? extends Annotation> tokenAnnotationClass,
            String tokenClassFeatureName, 
			String[] tokenClassWriteBackFeatureNames, 
			String tokenTextFeatureName,
            String tokenTypeFeatureName, 
			File tokenizerDescriptorPath, 
			CaseMatchParamValue caseMatchParamValue,
            boolean replaceCommaWithAnd, 
			Class<? extends Stemmer> stemmerClass, 
			File stemmerDictionaryFile,
            String[] excludedTokenClasses, 
			Integer[] excludedTokenTypes, 
			String[] includedTokenClasses,
            Integer[] includedTokenTypes, 
			String[] stopwords, 
			String languageId, 
			boolean printDictionary) {
				Object[] configData = new Object[] {
				ConceptMapper.PARAM_ATTRIBUTE_LIST, 	attributeList,
                ConceptMapper.PARAM_DICT_FILE,			dictionaryFile.getAbsolutePath(),
                ConceptMapper.PARAM_FEATURE_LIST,		featureList,
                ConceptMapper.PARAM_FINDALLMATCHES,		findAllMatches,
                ConceptMapper.PARAM_MATCHEDTOKENSFEATURENAME,matchedTokensFeatureName,
                ConceptMapper.PARAM_ORDERINDEPENDENTLOOKUP,orderIndependentLookup,
                ConceptMapper.PARAM_MATCHEDFEATURE,		resultingAnnotationMatchedTextFeature,
                ConceptMapper.PARAM_ANNOTATION_NAME,	resultingAnnotationClass.getName(),
                ConceptMapper.ENCLOSINGSPAN,			resultingEnclosingSpanName,
                ConceptMapper.PARAM_SEARCHSTRATEGY,		searchStrategyParamValue.paramValue(),
                "SpanFeatureStructure", 				spanFeatureStructureClass.getName(),
                ConceptMapper.PARAM_TOKENANNOTATION,	tokenAnnotationClass.getName(),
                ConceptMapper.PARAM_TOKENCLASSWRITEBACKFEATURENAMES,tokenClassWriteBackFeatureNames,
                "TokenizerDescriptorPath",				tokenizerDescriptorPath.getAbsolutePath(),

                TokenNormalizer.PARAM_CASE_MATCH, 		caseMatchParamValue.paramValue(),
                "ReplaceCommaWithAND", 					replaceCommaWithAnd,

                TokenFilter.PARAM_EXCLUDEDTOKENCLASSES, excludedTokenClasses,
                TokenFilter.PARAM_EXCLUDEDTOKENTYPES, 	excludedTokenTypes,
                TokenFilter.PARAM_INCLUDEDTOKENCLASSES, includedTokenClasses,
                TokenFilter.PARAM_INCLUDEDTOKENTYPES, 	includedTokenTypes,
                TokenFilter.PARAM_STOPWORDS, 			stopwords,

                "LanguageID", 							languageId,
                DictionaryResource_impl.PARAM_DUMPDICT, printDictionary 
				};
		};
***/

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
