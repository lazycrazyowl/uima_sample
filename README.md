uima_sample
===========

some basic, simple, uima pipelines and analysis engines cribbed from
both UIMA and uimaFIT tutorials. Some requires code from here:
http://sourceforge.net/projects/bionlp-uima/files/ccp-nlp/v3.1/

Chris Roeder, Nov. 2012

* concept_mapper_pipelines

	** GetStartedQuickAE.java
		This is a pipeline-less AE with direct JCAS creation and engine
		running. It's about as simple as you can get.

	** BaseUimaFitPipeline.java
		This one is the basic RoomNumberAnnotator code from the UIMA tutorial.
		It adds a pipeline, driven here by uimaFIT.:w
		RoomNumber.java
		RoomNumberAnnotator.java

	** ProteinPipeline.java
		This is basically a type system modification over the RoomNumberAnnotator.
		The maven pom includes steps to generate and include for compilation the type system classes.
		It adds usage of the CASDumpWriter from uimafit.
		Protein.java
		ProteinAnnotator.java

	** ParameterExamplePipeline.java (TBD)
		We need a subtle modification to show parameters uimaFIT-style.

	** ConceptMapperPipeline.java 
		This set includes work from a number of people showing how to create a ConceptMapper
		Dictionary from an OBO file, then run CM in a pipeline to create RDF output.
		It uses xml files to describe the UIMA Analysis Engines.
		ConceptMapper
		RdfCasConsumer
		RdfGenerator
		OboDictionaryCreator

	** ConceptMapperUIMAFitPipeline.java 
		This is an aborted attempt at creating a ConceptMapper AE from uimaFIT
	    calls without the use of xml files or the ConceptMapperAggregateFactory
	    used elsewhere. Sources of Difficulties include that base class of 
	    ConceptMapper. It's the stock JCasAnnotator_ImplBase, not the uimaFIT
	    one. There are subtle ways around that however. The uimaFIT parameter
	    initialization can be called directly. (TBc)
		See: https://groups.google.com/forum/?fromgroups#!topic/uimafit-users/BiCdfJrwGBE

* lucene_pipelines
