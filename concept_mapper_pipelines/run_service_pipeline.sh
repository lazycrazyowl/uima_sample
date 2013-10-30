#!/bin/bash
mvn -e exec:java \
-Dexec.mainClass="edu.ucdenver.ccp.uima_sample.ConceptMapperServicePipeline" \
-Dexec.args="/Users/roederc/work/data/medline/pubmed_batches/small/pmid_22730558.txt"
