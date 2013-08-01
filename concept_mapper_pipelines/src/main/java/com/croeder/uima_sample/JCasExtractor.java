package com.croeder.uima_sample;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JFSIndexRepository;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.cas.text.AnnotationIndex;

import com.croeder.uima_sample.annotation.IdDictTerm;

import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.descriptor.SofaCapability;


import com.croeder.uima_sample.annotation.SentenceAnnotation;
import com.croeder.uima_sample.annotation.TextAnnotation;
import org.apache.uima.conceptMapper.support.tokenizer.TokenAnnotation;
import com.croeder.uima_sample.annotation.Annotator;
import com.croeder.uima_sample.mention.ClassMention;
import com.croeder.uima_sample.mention_extensions.ClassMentionX;
import com.croeder.uima_sample.mention.IntegerSlotMention;
import com.croeder.uima_sample.mention.StringSlotMention;
import com.croeder.uima_sample.mention.PrimitiveSlotMention;
import com.croeder.uima_sample.mention.ComplexSlotMention;


/**
 * This is not an AnalysisEngine, though it reads a CAS and extracts
 * information from it, returning a Collection of Results.
 * Only deals with String slots for now..
 */
public class  JCasExtractor {

	public JCasExtractor() { }

	static class Result {
		String name;
		Map<String, String> attributes;
		
		public Result(String name) {
			this.name = name;
			attributes = new TreeMap<String, String>();
		}

		public String getName() { return name; }

		public void add(String name, String value) {
			attributes.put(name, value);
		}
	
		public String get(String name) {
			return attributes.get(name);
		}
		public Set<String> getKeys() {
			return attributes.keySet();
		}
	}


	public Collection<Result> extract(JCas jcas) {

		List<Result> resultsList = new ArrayList<Result>();
        FSIterator<Annotation> annotIter = jcas.getJFSIndexRepository().getAnnotationIndex().iterator();
        while (annotIter.hasNext()) {
	        Annotation annot = (Annotation) annotIter.next();
			if (annot instanceof IdDictTerm) {
                IdDictTerm dt = (IdDictTerm) annot;
				Result result = new Result("dictTerm");
				result.add("id", dt.getId());						
				result.add("canonical", dt.getDictCanon());						
				result.add("text", dt.getMatchedText());						
				resultsList.add(result);
            }
		}

		return resultsList;
    }


}

