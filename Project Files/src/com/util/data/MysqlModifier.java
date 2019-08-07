package com.util.data;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.util.threads.DaemonThread;

/**
 * Connect to a MySQL database and perform any modifications to it -
 * all within the available authorizations of the authenticated user.
 * 
 * @author Niv Kor
 */
public class MysqlModifier
{
	/**
	 * A spool thread that runs all data modification requests.
	 * Data modification is fairly slow and requires an alternative thread.
	 * 
	 * @author Niv Kor
	 */
	private static class MysqlWriter extends DaemonThread<String>
	{
		private Statement statement;
		
		/**
		 * @param statement - A statement object that's connected to the database.
		 */
		public MysqlWriter(MysqlConnection connection) {
			this.statement = connection.getStatement();
		}
		
		@Override
		protected void spoolingFunction(String node) throws Exception {
			statement.executeUpdate(node);
		}
	}
	
	private static final String NO_CONNECTION_ESTABLISED = "Please establish a connection to the schema first.";
	private static final String RESULT_UPDATE_COUNT_ERROR = "Result set representing update count of -1";
	
	private static Statement statement;
	private static MysqlWriter writer;
	private static Map<String, ResultSet> resultMap;
	
	/**
	 * Connect to a MySQL database.
	 * 
	 * @param connection - The MysqlConnection object that was used to connect to the data base
	 * @return true if the connection was successful.
	 */
	public static boolean connect(MysqlConnection connection) {
		statement = connection.getStatement();
		writer = new MysqlWriter(connection);
		writer.start();
		resultMap = new HashMap<String, ResultSet>();
		return statement != null;
	}
	
	/**
	 * Write to the data base.
	 * Actions that can be performed using this method:
	 * 'INSERT', 'UPDATE', 'DELETE', 'CREATE', 'DROP'
	 * 
	 * @param query - The query to execute
	 * @throws SQLException when the query has errors, or the connection has yet to be established.
	 */
	public static void write(String query) throws SQLException {
		checkConnection();
		writer.spool(query);
		resultMap.clear();
	}
	
	/**
	 * Get a set of rows from the database, based on a query.
	 * 
	 * @param query - The query to execute
	 * @param columns - The columns to receive data from
	 * @return an Object matrix containing all the values of the rows.
	 * @throws SQLException when the query has errors, or the connection has yet to be established.
	 */
	public static Object[][] readRows(String query, MysqlColumn... columns) throws SQLException {
		checkConnection();
		ResultSet resultSet = execute(query);
		resultSet.last();
		int lines = resultSet.getRow();
		resultSet.first();
		Object[][] obj = new Object[lines][columns.length];
		
		for (int i = 0; i < obj.length; i++, resultSet.next()) {
			for (int j = 0; j < obj[0].length; j++) {
				try { obj[i][j] = columns[j].getDataType().getValue(resultSet, columns[j].getName()); }
				catch (SQLException | NullPointerException e) { obj[i][j] = null; }
			}
		}
		
		return obj;
	}
	
	/**
	 * Execute a query.
	 * 
	 * @param query - The query to execute
	 * @return a result set of that query.
	 * @throws SQLException when the query is invalid.
	 */
	public static ResultSet execute(String query) throws SQLException {
		ResultSet resultSet = resultMap.get(query);
		
		if (resultSet == null || resultSet.isClosed()) {
			resultSet = statement.executeQuery(query);
			resultMap.put(query, resultSet);
		}
		
		//the result set encountered an error - create it again
		if (resultSet.toString().equals(RESULT_UPDATE_COUNT_ERROR) || resultSet.isClosed()) {
			resultMap.remove(query);
			return execute(query);
		}
		
		//move its position to the beginning
		else {
			try { resultSet.beforeFirst(); }
			catch (NullPointerException ex) {}
		}
		
		return resultSet;
	}
	
	/**
	 * Get an Integer (int) value from a table.
	 * 
	 * @param query - The query to execute
	 * @param column - Name of the column
	 * @return an Integer (int) value.
	 * @throws SQLException when the query has errors, or the connection has yet to be established.
	 */
	public static int readINT(String query, String column) throws SQLException {
		checkConnection();
		ResultSet resultSet = execute(query);
		
		if (resultSet.next()) return resultSet.getInt(column);
		else throw new SQLException();
	}
	
	/**
	 * Get a list of all Integer (int) values from a column of a table.
	 * 
	 * @param query - The query to execute
	 * @param column - Name of the column
	 * @return a list of Integer (int) values.
	 * @throws SQLException when the query has errors, or the connection has yet to be established.
	 */
	public static ArrayList<Integer> readAllINT(String query, String column) throws SQLException {
		checkConnection();
		ArrayList<Integer> list = new ArrayList<Integer>();
		
		ResultSet resultSet = execute(query);
		while (resultSet.next()) list.add(resultSet.getInt(column));

		return list;
	}
	
	/**
	 * Get a Decimal (double) value from a table.
	 * 
	 * @param query - The query to execute
	 * @param column - Name of the column
	 * @return a Decimal (double) value.
	 * @throws SQLException when the query has errors, or the connection has yet to be established.
	 */
	public static double readDECIMAL(String query, String column) throws SQLException {
		checkConnection();
		ResultSet resultSet = execute(query);
		
		if (resultSet.next()) return resultSet.getDouble(column);
		else throw new SQLException();
	}
	
	/**
	 * Get a list of all Decimal (double) values from a column of a table.
	 * 
	 * @param query - The query to execute
	 * @param column - Name of the column
	 * @return a list of Decimal (double) values.
	 * @throws SQLException when the query has errors, or the connection has yet to be established.
	 */
	public static ArrayList<Double> readAllDECIMAL(String query, String column) throws SQLException {
		checkConnection();
		ArrayList<Double> list = new ArrayList<Double>();
		
		ResultSet resultSet = execute(query);
		while (resultSet.next()) list.add(resultSet.getDouble(column));

		return list;
	}
	
	/**
	 * Get a Varchar (String) value from a table.
	 * 
	 * @param query - The query to execute
	 * @param column - Name of the column
	 * @return a Varchar (String) value.
	 * @throws SQLException when the query has errors, or the connection has yet to be established.
	 */
	public static String readVARCHAR(String query, String column) throws SQLException {
		checkConnection();
		ResultSet resultSet = execute(query);
		
		if (resultSet.next()) return resultSet.getString(column);
		else throw new SQLException();
	}
	
	/**
	 * Get a list of all Varchar (String) values from a column of a table.
	 * 
	 * @param query - The query to execute
	 * @param column - Name of the column
	 * @return a list of Varchar (String) values.
	 * @throws SQLException when the query has errors, or the connection has yet to be established.
	 */
	public static ArrayList<String> readAllVARCHAR(String query, String column) throws SQLException {
		checkConnection();
		ArrayList<String> list = new ArrayList<String>();
		
		ResultSet resultSet = execute(query);
		while (resultSet.next()) list.add(resultSet.getString(column));
		
		return list;
	}
	
	/**
	 * Get a Boolean value from a table.
	 * 
	 * @param query - The query to execute
	 * @param column - Name of the column
	 * @return a Boolean value.
	 * @throws SQLException when the query has errors, or the connection has yet to be established.
	 */
	public static boolean readBOOLEAN(String query, String column) throws SQLException {
		checkConnection();
		ResultSet resultSet = execute(query);
		
		if (resultSet.next()) return resultSet.getBoolean(column);
		else throw new SQLException();
	}
	
	/**
	 * Get a list of all Boolean values from a column of a table.
	 * 
	 * @param query - The query to execute
	 * @param column - Name of the column
	 * @return a list of Boolean values.
	 * @throws SQLException when the query has errors, or the connection has yet to be established.
	 */
	public static ArrayList<Boolean> readAllBOOLEAN(String query, String column) throws SQLException {
		checkConnection();
		ArrayList<Boolean> list = new ArrayList<Boolean>();
		
		ResultSet resultSet = execute(query);
		while (resultSet.next()) list.add(resultSet.getBoolean(column));

		return list;
	}
	
	/**
	 * Get a Timestamp value from a table.
	 * The value is converted to String.
	 * 
	 * @param query - The query to execute
	 * @param column - Name of the column
	 * @return a Timestamp value.
	 * @throws SQLException when the query has errors, or the connection has yet to be established.
	 */
	public static String readDATETIME(String query, String column) throws SQLException {
		return readVARCHAR(query, column);
	}
	
	/**
	 * Get a list of all Timestamp values from a column of a table.
	 * The values are converted to Strings.
	 * 
	 * @param query - The query to execute
	 * @param column - Name of the column
	 * @return a list of String values.
	 * @throws SQLException when the query has errors, or the connection has yet to be established.
	 */
	public static ArrayList<String> readAllDATETIME(String query, String column) throws SQLException {
		return readAllVARCHAR(query, column);
	}
	
	/**
	 * Build a simple structural query.
	 * 
	 * @param select - SELECT [column(s) argument]
	 * @param from - FROM [table argument]
	 * @param where - WHERE [1 column argument]
	 * @param equals - = [1 value argument]
	 * @return the query as a String object.
	 */
	public static String buildQuery(String select, String from, String where, Object equals) {
		String apostrophe = (equals instanceof String) ? "'" : "";
		
		return "SELECT " + select + " "
			 + "FROM " + from + " "
			 + "WHERE " + where + " = " + apostrophe + equals + apostrophe;
	}
	
	/**
	 * @throws SQLException when no connection to the data base has been established.
	 */
	private static void checkConnection() throws SQLException {
		if (statement == null) {
			System.err.println(NO_CONNECTION_ESTABLISED);
			throw new SQLException();
		}
	}
}