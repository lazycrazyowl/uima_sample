package  com.croeder.uima_sample;


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
import org.apache.uima.UIMAFramework;
import org.apache.uima.conceptMapper.support.stemmer.Stemmer;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.CollectionReaderFactory;
import org.uimafit.factory.ResourceCreationSpecifierFactory;
import org.uimafit.factory.TypeSystemDescriptionFactory;
import org.uimafit.pipeline.SimplePipeline;
import org.uimafit.pipeline.JCasIterable;


public class MedlineLineProteinExtractionPipeline  {

	private static Logger logger = Logger.getLogger(MedlineLineProteinExtractionPipeline.class);

	protected static final String[] typeSystemStrs = {
 		"analysis_engine.primitive.DictTerm", //"org.apache.uima.conceptMapper.DictTerm",
		"org.apache.uima.conceptMapper.support.tokenizer.TokenAnnotation"
	};

	protected TypeSystemDescription tsd;
	String[] emptyStopWordList =  {};


	MedlineLineProteinExtractionPipeline() {
        tsd = TypeSystemDescriptionFactory.createTypeSystemDescription(typeSystemStrs);
    }


        AnalysisEngineDescription conceptMapperDescription = null;
		if (ontologyName.equals("GO_CC")) { // permutation 31
        	conceptMapperDescription = ConceptMapperFactory.buildConceptMapperDescription(
				tsd,
				dictionaryFile,
				ConceptMapperFactory.TokenNormalizerConfigParam.CaseMatchParamValue.CASE_INSENSITIVE,
	    		ConceptMapperFactory.SearchStrategyParamValue.CONTIGUOUS_MATCH, 
				ConceptMapperPorterStemmer.class, //null,
				emptyStopWordList,
				false, //orderIndependentLookup,
				false, // findAllMatches,
				false, 	// replaceCommaWithAnd,
				Sentence.class, 
            	tokenizerXmlFile);
		}


	protected List<AnalysisEngineDescription> getPipelineAeDescriptions(
		File dictionaryDir,
		String ontologyName,
		File outputDir)
	throws UIMAException, IllegalArgumentException, IOException {

		List<AnalysisEngineDescription> descriptions 
			=  getBasePipelineAeDescriptions(dictionaryDir, ontologyName);


		return descriptions;
	}
	
	protected List<AnalysisEngineDescription> getBasePipelineAeDescriptions(
		File dictionaryDir,
		String ontologyName)
	throws UIMAException, IllegalArgumentException, IOException {

		
    	String TOKENIZER_XML_NAME = "analysis_engine/primitive/OffsetTokenizer.xml";
		File tokenizerXmlFile = new File("offset_tokenizer.xml"); // TODO: tmp
		if (!tokenizerXmlFile.canRead())  {
			// lazy install, safe when run solo the first time, as when you run maven
			// without -o to download jars.
			ClassPathUtil.copyClasspathResourceToFile(getClass(), TOKENIZER_XML_NAME, tokenizerXmlFile);
		}

		// SENTENCE DETECTOR  ****
		AnalysisEngineDescription sentenceDetectorDesc 
			= LingPipeSentenceDetector_AE.createAnalysisEngineDescription(tsd, ExplicitSentenceCasInserter.class, true);


		File dictFile =	new  File(dictionaryDir, firstDictionary);
		AnalysisEngineDescription conceptMapperDescription 
			= getConceptMapperByOntology(
				ontologyName,
	            tsd, 	
				dictFile,
				tokenizerXmlFile
			);

		// TODO verify this!! doe sthe CMByOntology fun. above set this consistently?
        CaseMatchParamValue caseMatchParamValue = CaseMatchParamValue.CASE_INSENSITIVE;
        Object[] tokenizerConfigData = OffsetTokenizerFactory.buildConfigurationData(caseMatchParamValue);
        AnalysisEngineDescription offsetTokenizerDescription
            = (AnalysisEngineDescription)  ResourceCreationSpecifierFactory.createResourceCreationSpecifier(
                tokenizerXmlFile.getAbsolutePath(),
                tokenizerConfigData);
        offsetTokenizerDescription.getAnalysisEngineMetaData().setTypeSystem(tsd);


		List<AnalysisEngineDescription> descriptions = new ArrayList<AnalysisEngineDescription>();
		descriptions.add(sentenceDetectorDesc);
		descriptions.add(offsetTokenizerDescription);
		descriptions.add(conceptMapperDescription);

		return descriptions;
	}


	public void go(int numToSkip, int numToProcess, File dictionaryDir, String ontologyName, 
					 File a1OutputDir, File rdfOutputDir, File csvOutputDir,
					 File inputDir, String extension)
	throws UIMAException, ResourceInitializationException, FileNotFoundException, IOException {

		List<AnalysisEngineDescription> aeDescList
			= getPipelineAeDescriptions(dictionaryDir, ontologyName, a1OutputDir, rdfOutputDir, csvOutputDir);
		AnalysisEngineDescription[] aeDescriptions 
			= aeDescList.toArray(new AnalysisEngineDescription[0]);

      	CollectionReaderDescription crd =  FileSystemCollectionReader.createDescription(
            tsd,
            inputDir,
            true,
            CharacterEncoding.UTF_8,
            "English",
            true,
            numToProcess,
            numToSkip,
            View.DEFAULT.name(),
            extension
        );

        CollectionReader cr = CollectionReaderFactory.createCollectionReader(crd);

		SimplePipeline.runPipeline(cr, aeDescriptions);
    }

	
	protected static void usage() {
		System.out.println("java -cp . <this class> <dict dir> <input tree> <a1 output dir> <rdf output dir> <csv output dir> <numToSkip> <numToProcess> [<input file extension>]");
		System.out.println("  Set numToProcess to -1 to process all files under input tree.");
	}


	public static void main(String[] args) {

		try {
			MedlineLineProteinExtractionPipeline pipeline 
				= new MedlineLineProteinExtractionPipeline ();
	
			BasicConfigurator.configure();
	
			pipeline.go(numToSkip, numToProcess, dictionaryDir, ontologyName, a1OutputDir, inputDir, extension);
		}
		catch(Exception x) {
			System.err.println(x);
			x.printStackTrace();
			System.exit(3);
		}
	}

}
