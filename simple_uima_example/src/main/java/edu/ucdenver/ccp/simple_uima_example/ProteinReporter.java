package edu.ucdenver.ccp.simple_uima_example;

import static org.uimafit.util.JCasUtil.select;

import org.apache.log4j.Logger;

import org.apache.uima.jcas.JCas;


import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
//import org.uimafit.component.JCasAnnotator_ImplBase;


/**
 * Example annotator  just reports what Proteins it finds in the CAS.
 */
public class ProteinReporter extends JCasAnnotator_ImplBase {

	Logger logger = Logger.getLogger(ProteinReporter.class);

	public void process(JCas jCas) {
		for (Protein prot : select(jCas, Protein.class)) {
            String word = prot.getCoveredText();
			System.out.println(word + " prefix:" + prot.getPrefix() + ", " + prot.getSuffix());
        }

	
	}

}
