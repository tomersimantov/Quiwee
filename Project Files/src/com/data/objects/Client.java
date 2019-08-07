package com.data.objects;
import java.sql.SQLException;
import java.time.LocalDateTime;
import com.data.MysqlPuller;
import com.data.tables.ClientsTable;
import com.main.User;
import com.util.data.MysqlRow;
import com.util.real_time.TimeStampConverter;

/**
 * This class represents a client that ordered a queue, and directly fetches data from the database.  
 */
public class Client extends MysqlRow
{
	/**
	 * @param name - The client's full name
	 * @param phone - Client's phone number
	 * @param country - The country name in the client's address
	 * @param city - The city name in the client's address
	 * @param street - The street name in the client's address
	 * @param stNum - The street number in the client's address
	 */
	public Client(String name, String phone, String country, String city, String street, Integer stNum) {
		super("clients");
		
		setName(name);
		setField(ClientsTable.PHONE.getColumn(), phone);
		setCountry(country);
		setCity(city);
		setStreet(street);
		setStreetNum(stNum);
		setJoinDate(LocalDateTime.now());
	}
	
	/**
	 * A constructor that imports a client from the database.
	 * 
	 * @param phone - Client's phone number
	 * @throws SQLException when the client does not exist in the database.
	 */
	public Client(String phone) throws SQLException {
		super("clients");
		setField(ClientsTable.PHONE.getColumn(), phone);
		
		if (isInDatabase()) {
			Object[][] rows = MysqlPuller.pullRows(ClientsTable.class, selectAllQuery());
			
			setName((String) rows[0][2]);
			setCountry((String) rows[0][3]);
			setCity((String) rows[0][4]);
			setStreet((String) rows[0][5]);
			setStreetNum((int) rows[0][6]);
			setJoinDate(TimeStampConverter.toLocalDateTime((String) rows[0][7]));
		}
		else throw new SQLException();
	}
	
	@Override
	protected void addFields() {
		addKeyField(ClientsTable.USER_ID.getColumn(), User.getKey());
		addKeyField(ClientsTable.PHONE.getColumn(), null);
		addLiquidField(ClientsTable.NAME.getColumn(), null);
		addLiquidField(ClientsTable.COUNTRY.getColumn(), null);
		addLiquidField(ClientsTable.CITY.getColumn(), null);
		addLiquidField(ClientsTable.STREET.getColumn(), null);
		addLiquidField(ClientsTable.STREET_NUM.getColumn(), null);
		addLiquidField(ClientsTable.JOIN_DATE.getColumn(), null);
	}
	
	/**
	 * @param n - The new full name of the client
	 */
	public void setName(String n) { setField(ClientsTable.NAME.getColumn(), n); }

	/**
	 * @param c - The new country of the client
	 */
	public void setCountry(String c) { setField(ClientsTable.COUNTRY.getColumn(), c); }
	
	/**
	 * @param c - The new city of the client
	 */
	public void setCity(String c) { setField(ClientsTable.CITY.getColumn(), c); }
	
	/**
	 * @param s - The new street of the client
	 */
	public void setStreet(String s) { setField(ClientsTable.STREET.getColumn(), s); }
	
	/**
	 * @param num - The new street number of the client
	 */
	public void setStreetNum(Integer num) { setField(ClientsTable.STREET_NUM.getColumn(), num); }
	
	/**
	 * @param t - The date in which the client joined the system
	 */
	private void setJoinDate(LocalDateTime t) {
		setField(ClientsTable.JOIN_DATE.getColumn(), TimeStampConverter.toString(t));
	}
	
	/**
	 * @return the client's full name.
	 */
	public String getName() { return (String) getField(ClientsTable.NAME.getColumn()); }
	
	/**
	 * @return the client's phone number (primary key).
	 */
	public String getPhoneNumber() { return (String) getField(ClientsTable.PHONE.getColumn()); }
	
	/**
	 * @return the client's country.
	 */
	public String getCountry() { return (String) getField(ClientsTable.COUNTRY.getColumn()); }
	
	/**
	 * @return the client's city.
	 */
	public String getCity() { return (String) getField(ClientsTable.CITY.getColumn()); }
	
	/**
	 * @return the client's street.
	 */
	public String getStreet() { return (String) getField(ClientsTable.STREET.getColumn()); }
	
	/**
	 * @return the client's street number.
	 */
	public int getStreetNum() { return (int) getField(ClientsTable.STREET_NUM.getColumn()); }
	
	/**
	 * @return the date in which the client joined the system.
	 */
	public LocalDateTime getJoinDate() {
		return TimeStampConverter.toLocalDateTime((String) getField(ClientsTable.JOIN_DATE.getColumn()));
	}
}