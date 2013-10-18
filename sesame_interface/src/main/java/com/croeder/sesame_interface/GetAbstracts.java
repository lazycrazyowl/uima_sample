package com.croeder.sesame_interface;

import org.openrdf.repository.base.RepositoryConnectionBase;

import org.openrdf.model.impl.ValueFactoryBase;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.Statement;;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.RDF;

import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.BindingSet;
import org.openrdf.query.Binding;
import org.openrdf.query.Update;


import org.apache.log4j.Logger;


// http://www.franz.com/agraph/support/documentation/v4/sparql-tutorial.html

public class GetAbstracts {

	public final static String bibo  = "<http://purl.org/ontology/bibo/>";
	public final static String ro    = "<http://www.obofoundry.org/ro/ro.owl#>";
	public final static String iao   = "<http://purl.obolibrary.org/obo/>";
	public final static String chris = "http://com.croeder/chris/";
	public final static String rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns/";

	public final URI denotesUri;

	static Logger logger = Logger.getLogger(GetAbstracts.class);
	RepositoryConnectionBase con;
	ValueFactoryBase valueFactory;
	QueryLanguage ql = QueryLanguage.SPARQL;

	public GetAbstracts() throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		ConnectionInstance ci = factory.getConnection("AG");
		con = ci.getConnection();
		valueFactory = ci.getValueFactory();
		denotesUri = valueFactory.createURI(iao, "IAO0000219");
	}

	public void query(String queryString) throws Exception {
		TupleQuery tq = con.prepareTupleQuery(ql, queryString);
		TupleQueryResult result = tq.evaluate();

		System.out.println("== bindings == ");		
		for (String name : result.getBindingNames() ) {
			System.out.println("binding:" + name);
		}

		System.out.println("== results == ");		
		while (result.hasNext()) {
			BindingSet bs = (BindingSet) result.next();
			for (Binding b : bs) {
				System.out.println(b.getName() + ", " + b.getValue());
			}
		}
		
		result.close();
	}

	public void createSets() throws Exception{
		TupleQuery tq = con.prepareTupleQuery(ql, getPmidsQuery);
		TupleQueryResult result = tq.evaluate();

		// uberBatch type bag
		URI uberBatchUri = valueFactory.createURI(chris, "pmid_batch_set");
		Statement uberBatchStmt = new StatementImpl(uberBatchUri,  RDF.TYPE, RDF.BAG);
		logger.info("Create UBER:" + uberBatchStmt.toString());
		con.add(uberBatchStmt);

		final int batch_size = 10;
		URI batchUri = null;
		int i=-1;
		while (result.hasNext() && ++i < 100 ) {	
			if (i % batch_size == 0) {
				int batch_number = i / batch_size;

				// batch  type bag
				batchUri = valueFactory.createURI(chris, "pmid_batch_" + batch_number);
				Statement batchStmt = new StatementImpl(batchUri,  RDF.TYPE, RDF.BAG);
				logger.info("CREATE BATCH: " + batchStmt.toString());
				con.add(batchStmt);

				// uberBatch contains batch
				URI batchCounterUri = valueFactory.createURI(rdf, "_" + batch_number);
				Statement uberContainsStmt = new StatementImpl(uberBatchUri, batchCounterUri, batchUri);
				logger.info("UBER CONTAINS: " + uberContainsStmt.toString());
				con.add(uberContainsStmt);
			}

			// batch contains pmid item
			BindingSet bs = (BindingSet) result.next();
			Binding b = bs.iterator().next();
			//URI counterUri = valueFactory.createURI(RDF.NS.getPrefix(), "_" + i);
			URI counterUri = valueFactory.createURI(rdf, "_" + i);
			Value v = b.getValue();
			Statement  itemStmt = new StatementImpl(batchUri, counterUri, v);
			logger.info("BATCH CONTAINS: " + itemStmt.toString());
			con.add(itemStmt);
		}
	}


// prefix chris: <http://com.croeder/chris/>
//delete where  {chris:pmid_batch_5 ?p1 ?o1 }


	public static void main(String args[]) {
		try {
			GetAbstracts ga = new GetAbstracts();
			//ga.query(basicQuery);
			//ga.query(abstractsQuery);
			ga.createSets();
			
		}
		catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
	}


	static final String basicQuery = "select ?s ?p ?o {?s ?p ?o} LIMIT 10";

	static final String prefixes 
		= "prefix bibo: <http://purl.org/ontology/bibo/>\n"
		+ "prefix ro:   <http://www.obofoundry.org/ro/ro.owl#>\n"
		+ "prefix iao:  <http://purl.obolibrary.org/obo/>\n"
		+ "prefix chris:  <http://com.croeder/chris/>\n";

// ro:integral_part_of

	static final String getPmidsQuery = prefixes 
		+ "select ?s "
		+ "{ ?s  bibo:pmid ?p . } order by ?s ";

	static final String abstractsQuery = prefixes 
		+ "select ?s ?o2 "
		+ "{ ?s  bibo:pmid \"23025167\"@en . \n"
		+ "  ?s  iao:IAO_0000219 ?s2 . \n"
		+ "  ?s2 dcterms:abstract ?o2 } limit 100";
}
