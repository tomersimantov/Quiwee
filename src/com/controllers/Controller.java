package com.controllers;
import com.data.Pullable;
import com.util.data.MysqlRow;

public abstract class Controller<T extends MysqlRow>
{
	/**
	 * Get an object from the database.
	 * 
	 * @param args - Primary key arguments of the object
	 * @return the object from the database or null if it's not in the database.
	 */
	public abstract T getObj(Object... args);
	
	/**
	 * Create an object to modify and use.
	 * This object is not automatically saved to the database,
	 * so this must be done manually.
	 * 
	 * @param args - All arguments of the object's constructor
	 * @return a new object to modify and use.
	 */
	public abstract T createObj(Object... args);
	
	/**
	 * Either insert a new object to the database automatically or update an existing one.
	 * This depends on whether the object can be found in the database or not.
	 * 
	 * @param args - All arguments of the object's constructor
	 * @return true if the object was updated successfully in the database.
	 */
	public boolean insertNew(Object... args) {
		return save(createObj(args));
	}
	
	/**
	 * Delete an object from the database.
	 * 
	 * @param args - Primary key arguments of the object
	 * @return true if the object was deleted successfully.
	 */
	public boolean delete(Object... args) {
		try { return getObj(args).delete(); }
		catch (NullPointerException ex) { return false; }
	}
	
	/**
	 * Delete an object from the database.
	 * 
	 * @param obj - The object to delete from the database 
	 * @return true if the object was deleted successfully.
	 */
	public boolean delete(T obj) {
		try { return obj.delete(); }
		catch (NullPointerException ex) { return false; }
	}
	
	/**
	 * Save all changes of an object to the database.
	 * If the object exists in the database, use the UPDATE command,
	 * if it doesn't use the INSERT command.
	 * 
	 * @param args - Primary key arguments of the object
	 * @return true if the object was saved successfully.
	 */
	public boolean save(Object... args) {
		try { return getObj(args).save(); }
		catch (NullPointerException ex) { return false; }
	}

	/**
	 * Save all changes of an object to the database.
	 * If the object exists in the database, use the UPDATE command,
	 * if it doesn't use the INSERT command.
	 * 
	 * @param obj - The object to delete from the database 
	 * @return true if the object was saved successfully.
	 */
	public boolean save(T obj) {
		try { return obj.save(); }
		catch (NullPointerException ex) { return false; }
	}
	
	/**
	 * Get rows from the database that contain the keyword.
	 * 
	 * @param keyword - A search word
	 * @param fields - The fields that need to contain the keyword
	 * @return rows from the database that contain the keyword.
	 */
	public abstract Object[][] getkeywordResults(String keyword, Pullable... fields);
}