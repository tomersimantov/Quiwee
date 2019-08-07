package com.data;
import java.sql.SQLException;
import java.util.List;
import com.util.data.DataType;
import com.util.data.MysqlColumn;
import com.util.data.MysqlModifier;

/**
 * This class pulls entire rows from the database tables,
 * given as a matrix of values all ready to use.
 */
public class MysqlPuller
{
	private static String userKey;
	
	/**
	 * Initialize the loader.
	 * 
	 * @param userKey - The user primary key
	 */
	public static void init(String key) {
		userKey = new String(key);
	}
	
	/**
	 * Pull an entire column from the database.
	 * 
	 * @param column - The column to pull from
	 * @return a list of all values that the column contains.
	 */
	public static List<Object> pullAll(Pullable column) {
		//try to pull an existing list of that column
		List<Object> list = column.getList();
		
		//create a new list
		if (list == null) {
			DataType dataType = column.getColumn().getDataType();
			String columnName = column.getColumn().getName();
			String tableName = column.getTableName();
			list = dataType.getAllValues(getOverallQuery(tableName), columnName);
			column.setList(list);
		}
		
		return list;
	}
	
	/**
	 * Pull all rows of a table from the database
	 * 
	 * @param pullableClass - The table to pull from
	 * @return an Object matrix containing all of the table's values.
	 */
	public static Object[][] pullRows(Class<? extends Pullable> pullableClass) {
		Pullable[] enumConstants = pullableClass.getEnumConstants();
		return pullRows(pullableClass, getOverallQuery(enumConstants[0].getTableName()));
	}
	
	/**
	 * Pull all rows of a table from the database, using a query
	 * 
	 * @param pullableClass - The table to pull from
	 * @return an Object matrix containing all of the table's values.
	 */
	public static Object[][] pullRows(Class<? extends Pullable> pullableClass, String query) {
		Pullable[] enumConstants = pullableClass.getEnumConstants();
		MysqlColumn[] columns = new MysqlColumn[enumConstants.length];
		
		for (int i = 0; i < columns.length; i++)
			columns[i] = enumConstants[i].getColumn();
		
		try { return MysqlModifier.readRows(query, columns); }
		catch (SQLException ex) { return new Object[0][enumConstants.length]; }
	}
	
	/**
	 * Pull all rows from a table, that contain a certain keyword.
	 * If the keyword is null or empty, all rows are pulled.
	 * 
	 * @param pullableClass - The table to pull from
	 * @param keyword - The keyword to search
	 * @param fields - All fields that may contain the keyword
	 * @return an Object matrix containing all of the table's values.
	 */
	public static Object[][] pullKeywordRows(Class<? extends Pullable> pullableClass, String keyword, Pullable... fields) {
		Pullable[] enumConstants = pullableClass.getEnumConstants();
		String tableName = enumConstants[0].getTableName();
		char firstChar = tableName.charAt(0);
		String query = getOverallQuery(tableName);
		
		//build query
		if (keyword != null && !keyword.equals("")) {
			query = query.concat("AND (");
			for (int i = 0; i < fields.length; i++) {
				query = query.concat(firstChar + "." + fields[i].getColumn().getName() + " "
								   + "LIKE '%" + keyword + "%'");
				
				if (i < fields.length - 1) query = query.concat(" OR ");
				else query = query.concat(")");
			}
		}
		
		//retrieve corresponding rows
		return pullRows(pullableClass, query);
	}
	
	/**
	 * @param tableName - The table to pull from
	 * @return a query that pulls everything from the table, but only for the user.
	 */
	private static String getOverallQuery(String tableName) {
		return "SELECT * "
			 + "FROM " + tableName + " " + tableName.charAt(0) + " "
			 + "WHERE user_id = '" + userKey + "'";
	}
}