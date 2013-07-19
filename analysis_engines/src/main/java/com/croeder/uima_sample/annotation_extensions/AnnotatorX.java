package com.croeder.uima_sample.annotation_extensions;

import org.apache.uima.jcas.JCas;

import com.croeder.uima_sample.annotation.Annotator;

public class AnnotatorX extends Annotator {

	public AnnotatorX(JCas jCas, int id, String firstName, String lastName, String affiliation) {
		super(jCas);
		setAnnotatorID(id);
		setFirstName(firstName);
		setLastName(lastName);
		setAffiliation(affiliation);
	}
}
