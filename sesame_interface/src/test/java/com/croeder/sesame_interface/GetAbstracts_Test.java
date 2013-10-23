package com.croeder.sesame_interface;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import java.io.File;
import java.util.List;
import java.util.List;

import org.apache.log4j.Logger;

import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.Statement;;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.RDF;

import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.BindingSet;
import org.openrdf.query.Binding;
import org.openrdf.query.Update;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;

import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;


public class GetAbstracts_Test {
	Logger logger = Logger.getLogger(GetAbstracts_Test.class);
	GetAbstracts ga;

	@Before
	public void setup() throws Exception { 
		//ga = new GetAbstracts("conn.sail");
		ga = new GetAbstracts("conn.ag");


		ga.deleteBatches();

		ga.createSets();
	}

	@After
	public void teardown() throws Exception {
		ga.deleteBatches();
		ga.close();
	}

	@Test
	public void test_getPmidsBatch_1() {
		List<URI> list = ga.getPmidsBatch(0);  

		logger.info("test 1 batch size " + list.size());

		assertEquals("the batches should be full-size", 100, list.size());
		logger.info(list.get(0));
	}

	//@Test
	public void test_getPmidsBatch_4() {
		List<URI> list = ga.getPmidsBatch(1);  
		logger.info("test 1 batch size " + list.size());
		assertEquals("the batches should be full-size", 100, list.size());
	}


	//@Test
	public void test_getPmidsBatch_2() {
		List<URI> list = ga.getPmidsBatch(99);  
		logger.info("test 1 batch size " + list.size());
		assertEquals("the batches should be full-size", 100, list.size());
	}

	//@Test
	public void test_getPmidsBatch_3() {
		List<URI> list = ga.getPmidsBatch(100);  
		logger.info("test 1 batch size " + list.size());
		assertEquals("the batches should be full-size", 0, list.size());
	}

	//@Test
	public void test_getAbstract_1() {
		String a = ga.getAbstract("bogus");
	}

	//@Test
	public void test_getAbstract_2() { 
		///String a = getAbstract(Value medlineUri);
	}


}
