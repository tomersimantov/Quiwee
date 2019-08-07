package com.controllers;
import java.sql.SQLException;
import com.data.MysqlPuller;
import com.data.Pullable;
import com.data.objects.Service;
import com.data.tables.ServicesTable;

/**
 * This class controls and manages clients in the database.
 * 
 * All arguments:
 * 		1. String - service name
 *		2. double - price
 *		3. boolean - is the price per hour

 * Key arguments:
 * 		1. String - service name
 * 
 * @author Niv Kor
 */
public class ServicesController extends Controller<Service>
{
	@Override
	public Service getObj(Object... obj) {
		try {
			String name = (String) obj[0];
			return new Service(name);
		}
		catch (SQLException | ClassCastException | ArrayIndexOutOfBoundsException ex) { return null; }
	}
	
	@Override
	public Service createObj(Object... obj) {
		try {
			String name = (String) obj[0];
			double price = (double) obj[1];
			boolean hourly = (boolean) obj[2];
			return new Service(name, price, hourly);
		}
		catch (ClassCastException | ArrayIndexOutOfBoundsException ex) { return null; }
	}
	
	@Override
	public Object[][] getkeywordResults(String keyword, Pullable... fields) {
		return MysqlPuller.pullKeywordRows(ServicesTable.class, keyword, fields);
	}
}