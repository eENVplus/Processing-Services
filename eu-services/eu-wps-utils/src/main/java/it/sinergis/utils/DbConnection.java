package it.sinergis.utils;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.geoserver.catalog.DataStoreInfo;

public class DbConnection {
	
	private static final Logger LOGGER = Logger.getLogger("DbUtils");
	
	private String host;
	private String port;
	private String passwd;
	private String user;
	private String database;
	
	public DbConnection() {
	}
	
	public boolean executeQuery(String query, Connection connection) throws Exception {
		PreparedStatement stm = null;
		try {
			stm = connection.prepareStatement(query);
			stm.execute();
			return true;
		}
		catch (Exception e) {
			LOGGER.error("errore nell'esecuzione della query " + query, e);
			throw e;
		}
		finally {
			if (stm != null) {
				try {
					stm.close();
				}
				catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public ResultSet executeQueryWithResultSet(String query, Connection connection) throws Exception {
		PreparedStatement stm = null;
		try {
			stm = connection.prepareStatement(query);
			return stm.executeQuery();
		}
		catch (Exception e) {
			LOGGER.error("errore nell'esecuzione della query " + query, e);
			throw e;
		}
	}
	
	public Connection initConnection(String driver, String jdbc, String host, String port, String database,
			String user, String password) {
		Connection connection = null;
		try {
			Class.forName(driver);
			
			connection = DriverManager.getConnection(jdbc + "://" + host + ":" + port + "/" + database, user, passwd);
		}
		catch (SQLException e) {
			LOGGER.error("errore nell'inizializzazione della connessione", e);
			e.printStackTrace();
		}
		catch (ClassNotFoundException e) {
			LOGGER.error("errore nel recupero del driver postgresql", e);
			e.printStackTrace();
		}
		return connection;
	}
	
	/**
	 * init connection from storeInfo
	 * 
	 * @param storeInfo
	 * @return
	 */
	public Connection initConnection(DataStoreInfo storeInfo) {
		
		Map<String, Serializable> params = storeInfo.getConnectionParameters();
		
		port = (String) params.get("port");
		host = (String) params.get("host");
		passwd = (String) params.get("passwd");
		user = (String) params.get("user");
		database = (String) params.get("database");
		
		Connection connection = initConnection("org.postgresql.Driver", "jdbc:postgresql", host, port, database, user,
				passwd);
		
		return connection;
	}
	
	/*
	 * metodo che crea la tabella dei risultati
	 */
	public boolean createResultTable(DataStoreInfo storeInfo, String resultTableName, String schema,
			Connection connection) throws Exception {
		
		if (connection != null) {
			String createTable = "CREATE TABLE " + schema + ".\"" + resultTableName
					+ "\" (id SERIAL UNIQUE, geometry_type text, result_geom geometry, operation_type text);";
			LOGGER.debug("creazione della tabella " + createTable);
			return executeQuery(createTable, connection);
		}
		return false;
	}
	
	public boolean createResultTable(String createTable, Connection connection) throws Exception {
		
		if (connection != null) {
			LOGGER.debug("creazione della tabella " + createTable);
			return executeQuery(createTable, connection);
		}
		return false;
	}
	
	public boolean deleteTable(String table, Connection connection) throws Exception {
		String deleteQuery = "DROP TABLE " + table;
		return executeQuery(deleteQuery, connection);
	}
	
	public boolean deleteTable(String schema, String layerName, Connection connection) throws Exception {
		String deleteQuery = "DROP TABLE " + schema + ".\"" + layerName + "\"";
		return executeQuery(deleteQuery, connection);
	}
	
	/*
	 * metodo che restituisce come si chiama/chiamano il/i campo/i primary key
	 */
	public String getFieldPrimaryKey(String layerName, String schema, Connection connection) throws SQLException {
		DatabaseMetaData dm = connection.getMetaData();
		ResultSet rs = dm.getPrimaryKeys(connection.getCatalog(), schema, layerName);
		String pkey = "";
		while (rs.next()) {
			pkey += rs.getString("COLUMN_NAME") + ",";
		}
		if (!pkey.equals("")) {
			pkey = pkey.substring(0, pkey.length() - 1);
		}
		LOGGER.debug("primary key della tabella " + layerName + ": " + pkey);
		return pkey;
	}
	
	public void closeConnection(Connection connection) {
		try {
			if (connection != null) {
				connection.close();
			}
		}
		catch (SQLException e) {
			LOGGER.error("errore nella chiusura della connessione", e);
			e.printStackTrace();
		}
		connection = null;
	}
	
	/*
	 * metodo che restituisce la lunghezza massima del campo
	 */
	public Integer getMaxLengthFieldOnDb(String field, String tableName, String schema, Connection connection)
			throws Exception {
		String query = "select MAX(length(trim(\"" + field + "\"::text))) FROM " + schema + ".\"" + tableName + "\"";
		ResultSet result = executeQueryWithResultSet(query, connection);
		if (result.next()) {
			return result.getInt(1);
		}
		return null;
	}
}
