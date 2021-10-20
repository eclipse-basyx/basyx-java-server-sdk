/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.tools.sql.driver;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaxxer.hikari.HikariDataSource;


/**
 * Access SQL database
 * 
 * @author kuhn
 *
 */
public class SQLDriver implements ISQLDriver {
	private static Logger logger = LoggerFactory.getLogger(SQLDriver.class);

	
	/**
	 * Store user name
	 */
	protected String userName = null;
	
	/**
	 * Store password
	 */
	protected String password = null;

	/**
	 * Store path to database server
	 */
	protected String dbPath = null;
	
	
	/**
	 * Store query prefix
	 */
	protected String queryPrefix = null;
	
	
	/**
	 * Store driver class (with package name)
	 */
	protected String qualDriverClass = null;
	
	
	/**
	 * JDBC connection
	 */
	protected Connection connect = null;
	
	/**
	 * Data source
	 */
	protected HikariDataSource ds = null;

	
	
	
	/**
	 * Create a SQL driver and a SQL connection
	 */
	public SQLDriver(String path, String user, String pass, String qryPfx, String qDrvCls) {
		// Store parameter
		userName        = user;
		password        = pass;
		dbPath          = path;
		queryPrefix     = qryPfx;
		qualDriverClass = qDrvCls;
		
		// This will load the MySQL driver, each DB has its own driver
		try {
			Class.forName(qualDriverClass);
		} catch (ClassNotFoundException e) {
			logger.error("Could not init SQLDriver", e);
		}
	}
	
	
	
	/**
	 * Execute a SQL query
	 */
	@Override
	public CachedRowSet sqlQuery(String queryString) {
		// Store SQL statement, flag that indicates whether the connection was created by this 
		// operation (and needs to be closed), and result
		Statement statement              = null;
		CachedRowSet rowSet              = null;
		
		
		// Access database
		try {
			// Open a connection with data source
			openConnection();

			// Statements allow to issue SQL queries to the database
			statement = connect.createStatement();
			
			// ResultSet gets the result of the SQL query
			ResultSet resultSet = statement.executeQuery(queryString);
			
			// Convert DB data to memory cache
			rowSet = getCachedRowSet(resultSet);
			
			// Close connection with data source
			closeConnection();
		} catch (SQLException e) {
			logger.error("sqlQuery failed", e);
		}
		
		// Return result of query
		return rowSet;
	}
	
	
	
	/**
	 * Execute a SQL update
	 */
	@Override
	public void sqlUpdate(String updateString) {
		// Store SQL statement
		Statement statement              = null;
		
		// Access database
		try {
			// Open a connection with data source
			openConnection();

			// Statements allow to issue SQL queries to the database
			statement = connect.createStatement();

			// ResultSet gets the result of the SQL query
			statement.executeUpdate(updateString);

			// Close connection with data source
			closeConnection();
		} catch (SQLException e) {
			logger.error("sqlUpdate failed", e);
		}
	}
	
	
	
	/**
	 * Open connection
	 */
	public void openConnection() {
		// Access database
		try {
			// Open connection
			if (connect == null) {
				openDataSource();
				connect = ds.getConnection();
			}
		} catch (SQLException e) {
			logger.error("Failed to open sql driver connection", e);
		}
	}
	
	
	/**
	 * Close connection
	 */
	public void closeConnection() {
		// Access database
		try {
			// Close connection
			if (connect != null) {
				connect.close();
				connect = null;
			}
		} catch (SQLException e) {
			logger.error("Failed to close sql driver connection", e);
		}
	}
	
	
	/**
	 * Get connection
	 */
	public Connection getConnection() {
		return connect;
	}
	
	
	/**
	 * Indicate if driver has open connection
	 */
	public boolean hasOpenConnection() {
		return (connect == null);
	}
	
	/**
	 * Open Data source
	*/
	private void openDataSource() {
		if (ds == null) {
			ds = new HikariDataSource();
			ds.setJdbcUrl(queryPrefix+dbPath);
			ds.setUsername(userName);
			ds.setPassword(password);
			ds.setMaximumPoolSize(5);
		}
	}
	
	private CachedRowSet getCachedRowSet(ResultSet resultSet) throws SQLException {
		RowSetFactory factory = RowSetProvider.newFactory();
		CachedRowSet rowset = factory.createCachedRowSet();
		rowset.populate(resultSet);
		return rowset;
	}
}

