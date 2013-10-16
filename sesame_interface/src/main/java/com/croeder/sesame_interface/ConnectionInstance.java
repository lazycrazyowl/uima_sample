package com.croeder.sesame_interface;

import org.openrdf.repository.base.RepositoryConnectionBase;
import org.openrdf.repository.RepositoryException;


public interface ConnectionInstance {
	public RepositoryConnectionBase getConnection() throws RepositoryException;
}

