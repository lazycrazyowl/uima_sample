package com.croeder.sesame_interface;


import com.franz.agraph.repository.AGCatalog;
import com.franz.agraph.repository.AGRepository;
import com.franz.agraph.repository.AGServer;
import com.franz.agraph.repository.AGValueFactory;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;

import java.util.Properties;

import org.apache.log4j.Logger;

import org.openrdf.repository.base.RepositoryConnectionBase;
import org.openrdf.repository.RepositoryException;
import org.openrdf.model.impl.ValueFactoryBase;


public class AGConnectionInstance implements ConnectionInstance {

	private Logger logger = Logger.getLogger(AGConnectionInstance.class);

	private RepositoryConnectionBase conn;
	private AGServer server;
	private AGRepository repo;

	private String serverURI;
	private String username;
	private String password;
	private String repoName;
	

	public AGConnectionInstance(String propertiesFilename) {
		readProperties(propertiesFilename);
		server = new AGServer(serverURI, username, password);
		AGCatalog catalog = server.getRootCatalog();
		try {
			repo = catalog.openRepository(repoName);
			repo.initialize();
			conn = repo.getConnection();
		}
		catch (RepositoryException e) {
			logger.error("serverURI is:\"" + serverURI + "\" repo name is \"" + repoName + "\"");
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public RepositoryConnectionBase getConnection() throws RepositoryException{
		return repo.getConnection();
	}

	public ValueFactoryBase getValueFactory() {
		return new AGValueFactory(repo);
	}

	private void readProperties(String propertiesFileName) {
		try {
			// TODO: do stream from classpath
			InputStream propsStream = new FileInputStream(new File(propertiesFileName));
			Properties props = new Properties();
            	props.load(propsStream);
            	serverURI = props.getProperty("conn.uri");
            	username = props.getProperty("conn.username");
            	password = props.getProperty("conn.password");
            	repoName = props.getProperty("conn.reponame");
		}
		catch (IOException e) {
			logger.error(e);
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}

