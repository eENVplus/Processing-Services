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

public class DbUtils {
	
	private static Connection connection;
	
	private static final Logger LOGGER = Logger.getLogger("DbUtils");
	
	private static String host;
	private static String port;
	private static String passwd;
	private static String user;
	private static String database;
	
	public static boolean executeQuery(String query) throws Exception {
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
	
	public static ResultSet executeQueryWithResultSet(String query) throws Exception {
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
	
	public static Connection initConnection(DataStoreInfo storeInfo) {
		try {
			Class.forName("org.postgresql.Driver");
			
			Map<String, Serializable> params = storeInfo.getConnectionParameters();
			
			port = (String) params.get("port");
			host = (String) params.get("host");
			passwd = (String) params.get("passwd");
			user = (String) params.get("user");
			database = (String) params.get("database");
			
			connection = DriverManager.getConnection("jdbc:postgresql://"+host+":"+port+"/"+database, user,
					passwd);
			return connection;
		}
		catch (ClassNotFoundException e) {
			LOGGER.error("errore nel recupero del driver postgresql", e);
			e.printStackTrace();
		}
		catch (SQLException e) {
			LOGGER.error("errore nell'inizializzazione della connessione", e);
			e.printStackTrace();
		}
		return null;
	}
	
	/*
	 * metodo che crea la tabella dei risultati
	 */
	public static boolean createResultTable(DataStoreInfo storeInfo, String resultTableName, String schema) throws Exception {
		if (connection == null) {
			connection = DbUtils.initConnection(storeInfo);
		}
		
		if (connection!=null) {
			String createTable = "CREATE TABLE " + schema + ".\"" + resultTableName
					+ "\" (id SERIAL UNIQUE, geometry_type text, result_geom geometry, operation_type text);";
			LOGGER.debug("creazione della tabella "+createTable);
			return DbUtils.executeQuery(createTable);
		}
		return false;
	}
	
	public static boolean createResultTable(DataStoreInfo storeInfo, Layer layer) throws Exception {
		if (connection == null) {
			connection = DbUtils.initConnection(storeInfo);
		}
		
		if (connection!=null) {
			String createTable = "CREATE TABLE " + layer.getSchema() + ".\"" + layer.getName()
					+ "\" (id SERIAL UNIQUE, geometry_type text, result_geom geometry, operation_type text);";
			LOGGER.debug("creazione della tabella "+createTable);
			return DbUtils.executeQuery(createTable);
		}
		return false;
	}
	
	public static boolean createResultTable(DataStoreInfo storeInfo, String createTable) throws Exception {
		if (connection == null) {
			connection = DbUtils.initConnection(storeInfo);
		}
		
		if (connection!=null) {
			LOGGER.debug("creazione della tabella "+createTable);
			return DbUtils.executeQuery(createTable);
		}
		return false;
	}
	
	public static boolean deleteTable(String table) throws Exception {
		String deleteQuery = "DROP TABLE " + table;
		return executeQuery(deleteQuery);
	}
	
	public static boolean deleteTable(Layer layer) throws Exception {
		String deleteQuery = "DROP TABLE " +  layer.getSchema()  + ".\"" + layer.getName() + "\"";
		return executeQuery(deleteQuery);
	}
	
	/*
	 * metodo che restituisce come si chiama/chiamano il/i campo/i primary key 
	 */
	public static String getFieldPrimaryKey(String layerName, String schema) throws SQLException {
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
	
	public static void closeConnection() {
		try {
			connection.close();
		}
		catch (SQLException e) {
			LOGGER.error("errore nella chiusura della connessione",e);
			e.printStackTrace();
		}
		connection = null;
	}
	
	public static void createShp() throws Exception {
		String pathOutput = "C:\\Users\\a2sb0132\\Downloads\\prova.shp";
		String query = "pgsql2shp -f "+pathOutput+" -h "+ host + " -u "+user +" -P "+ passwd + " databasename "+ database;
		executeQuery(query);
	}
}
