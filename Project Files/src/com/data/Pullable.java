package com.data;
import java.sql.SQLException;
import java.util.List;
import com.util.data.MysqlColumn;
import com.util.data.MysqlModifier;

public interface Pullable
{
	/**
	 * @return a list of the most recent pulled objects from the database.
	 */
	List<Object> getList();
	
	/**
	 * @param l - A list of the most recent pulled objects from the database.
	 */
	void setList(List<Object> l);
	
	/**
	 * @return the table's name as type in the database.
	 */
	String getTableName();
	
	/**
	 * @return the column that represents the enum constant in the databse.
	 */
	MysqlColumn getColumn();
	
	/**
	 * @return all enum constants.
	 */
	private static Pullable[] getValues() { return new Pullable[0]; }
	
	/**
	 * Pull a set of rows from the database.
	 * 
	 * @param query - The query to execute
	 * @return an Object matrix containing all relevant rows for the table
	 * @throws SQLException
	 */
	static Object[][] getRows(String query) throws SQLException {
		MysqlColumn[] columns = new MysqlColumn[getValues().length];
		
		for (int i = 0; i < columns.length; i++)
			columns[i] = getValues()[i].getColumn();

		return MysqlModifier.readRows(query, columns);
	}
}