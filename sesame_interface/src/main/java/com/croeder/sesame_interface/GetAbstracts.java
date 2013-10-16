package com.croeder.sesame_interface;

import org.openrdf.repository.base.RepositoryConnectionBase;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.BindingSet;
import org.openrdf.query.Binding;
import org.apache.log4j.Logger;


public class GetAbstracts {

	static Logger logger = Logger.getLogger(GetAbstracts.class);
	RepositoryConnectionBase con;
	QueryLanguage ql = QueryLanguage.SPARQL;

	public GetAbstracts() throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		con = factory.getConnection("AG");
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

	public static void main(String args[]) {
		try {
			GetAbstracts ga = new GetAbstracts();
			//ga.query(basicQuery);
			ga.query(abstractsQuery);
		}
		catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
	}


	static final String basicQuery = "select ?s ?p ?o {?s ?p ?o} LIMIT 10";

	static final String prefixes 
		= "prefix bibo: <http://purl.org/ontology/bibo/>\n"
		+ "prefix iao:  <http://purl.obolibrary.org/obo/>\n";

	static final String abstractsQuery = prefixes 
		+ "select ?s ?o2 "
		+ "{ ?s  bibo:pmid \"23025167\"@en . \n"
		+ "  ?s  iao:IAO_0000219 ?s2 . \n"
		+ "  ?s2 dcterms:abstract ?o2 } "
		+ "LIMIT 10";
}
