package com.croeder.sesame_interface;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;

import java.util.Properties;
import java.util.HashMap;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;

import org.openrdf.repository.base.RepositoryConnectionBase;
import org.openrdf.repository.RepositoryException;


public class ConnectionFactory {

	public final static String  defaultPropertiesFileName = "target/classes/connection.properties";
	public String  propertiesFileName = "connection.properties";
	private Logger logger = Logger.getLogger(ConnectionFactory.class);

	private String[] vendorNames = {"AG"};
	private String[] classNames  = {"com.croeder.sesame_interface.AGConnection"};
	String vendor="AG";
	HashMap<String, String>  classHash;
	
	public ConnectionFactory() {
		this(defaultPropertiesFileName);
	}

	public ConnectionFactory(String propertiesFileName) {
		this.propertiesFileName = propertiesFileName;
		assert(vendorNames.length == classNames.length);
		readProperties(propertiesFileName);
		classHash = new HashMap<String, String>();
		HashMap<String, String>  hash = new HashMap<String, String>();
		for (int i=0; i<vendorNames.length; i++) {
			classHash.put(vendorNames[i], classNames[i]);
		}
	}

	public RepositoryConnectionBase getConnection(String vendor) 
	throws ClassNotFoundException, InstantiationException, NoSuchMethodException, IllegalAccessException, InvocationTargetException,  RepositoryException {
		assert(classHash.keySet().contains(vendor));
		Class<?> clazz = Class.forName(classHash.get(vendor));
		Constructor<?> ctor = clazz.getConstructor(String.class);
		ConnectionInstance conn = (ConnectionInstance) ctor.newInstance(propertiesFileName);
		return conn.getConnection();
	}

	private void readProperties(String propertiesFileName) {
		try {
			// TODO: do stream from classpath
			InputStream propsStream = new FileInputStream(new File(propertiesFileName));
			Properties props = new Properties();
           	props.load(propsStream);
			vendor = props.getProperty("conn.vendor");
		}
		catch (IOException e) {
			logger.error(e);
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}

