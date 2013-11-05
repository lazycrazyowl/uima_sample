#!/bin/bash
mvn -e exec:java \
-Dexec.mainClass="edu.ucdenver.ccp.simple_uima_example.OpenNlpPipeline" \
-Dexec.args="target/classes/input"
