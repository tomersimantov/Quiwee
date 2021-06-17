package com.controllers;
import java.sql.SQLException;
import com.data.MysqlPuller;
import com.data.Pullable;
import com.data.objects.Client;
import com.data.tables.ClientsTable;

/**
 * This class controls and manages clients in the database.
 * 
 * All arguments:
 * 		1. String - full name
 *		2. String - phone number
 *		3. String - country name
 *		4. String - city name
 *		5. String - street name
 *		6. int - street number
 * 
 * Key arguments:
 * 		1. String - phone number
 * 
 * 
 */
public class ClientsController extends Controller<Client>
{
	@Override
	public Client getObj(Object... obj) {
		try {
			String phone = (String) obj[0];
			return new Client(phone);
		}
		catch (SQLException | ClassCastException | ArrayIndexOutOfBoundsException ex) { return null; }
	}
	
	@Override
	public Client createObj(Object... obj) {
		try {
			String name = (String) obj[0];
			String phone = (String) obj[1];
			String country = (String) obj[2];
			String city = (String) obj[3];
			String street = (String) obj[4];
			int stNum = (int) obj[5];
			return new Client(name, phone, country, city, street, stNum);
		}
		catch (ClassCastException | ArrayIndexOutOfBoundsException ex) { return null; }
	}

	@Override
	public Object[][] getkeywordResults(String keyword, Pullable... fields) {
		return MysqlPuller.pullKeywordRows(ClientsTable.class, keyword, fields);
	}
}
