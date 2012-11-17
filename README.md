uima_sample
===========

some basic, simple, uima pipelines and analysis engines cribbed from
both UIMA and uimaFIT tutorials.

Chris Roeder, Nov. 2012


* GetStartedQuickAE.java
	This is a pipeline-less AE with direct JCAS creation and engine
	running. It's about as simple as you can get.

* BaseUimaFitPipeline.java
	This one is the basic RoomNumberAnnotator code from the UIMA tutorial.
	It adds a pipeline, driven here by uimaFIT.:w
	RoomNumber.java
	RoomNumberAnnotator.java

* ProteinPipeline.java
	This is basically a type system modification over the RoomNumberAnnotator.
	The maven pom includes steps to generate and include for compilation the type system classes.
	It adds usage of the CASDumpWriter from uimafit.
	Protein.java
	ProteinAnnotator.java

* ParameterExamplePipeline.java (TBD)
	We need a subtle modification to show parameters uimaFIT-style.

* ConceptMapperPipeline.java (TBD)
	This set includes work from a number of people showing how to create a ConceptMapper
	Dictionary from an OBO file, then run CM in a pipeline to create RDF output.
	ConceptMapper
	RdfCasConsumer
	RdfGenerator
	OboDictionaryCreator
