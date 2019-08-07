package com.data.objects;
import java.sql.SQLException;
import com.data.MysqlPuller;
import com.data.tables.ServicesTable;
import com.main.User;
import com.util.data.MysqlRow;

/**
 * This class represents a known service of the business, and directly fetches data from the database.  
 */
public class Service extends MysqlRow
{
	/**
	 * @param name - The name of the service
	 * @param price - Default price of the service
	 * @param hourly - True if the price is per hour
	 */
	public Service(String name, double price, boolean hourly) {
		super("services");
		
		setField(ServicesTable.NAME.getColumn(), name);
		setPrice(price);
		setHourlyPrice(hourly);
	}
	
	/**
	 * A constructor that imports a service from the database.
	 * 
	 * @param name - The name of the service
	 * @throws SQLException when the service does not exist in the database.
	 */
	public Service(String name) throws SQLException {
		super("services");
		
		setField(ServicesTable.NAME.getColumn(), name);
		Object[][] row = MysqlPuller.pullRows(ServicesTable.class, selectAllQuery());
		
		if (isInDatabase()) {
			setPrice((double) row[0][2]);
			setHourlyPrice((boolean) row[0][3]);
		}
		else throw new SQLException();
	}
	
	@Override
	protected void addFields() {
		addKeyField(ServicesTable.USER_ID.getColumn(), User.getKey());
		addKeyField(ServicesTable.NAME.getColumn(), null);
		addLiquidField(ServicesTable.PRICE.getColumn(), null);
		addLiquidField(ServicesTable.HOURLY_PRICE.getColumn(), null);
	}
	
	/**
	 * @param p - The new price
	 */
	public void setPrice(double p) { setField(ServicesTable.PRICE.getColumn(), p); }
	
	/**
	 * @param flag - True if the price is per hour
	 */
	public void setHourlyPrice(boolean flag) { setField(ServicesTable.HOURLY_PRICE.getColumn(), flag); }
	
	/**
	 * @return the service's name (primary key).
	 */
	public String getName() { return (String) getField(ServicesTable.NAME.getColumn()); }
	
	/**
	 * @return the fixed price of the service.
	 */
	public double getPrice() { return (double) getField(ServicesTable.PRICE.getColumn()); }
	
	/**
	 * @return true if the service's price is per hour.
	 */
	public boolean isHourly() { return (boolean) getField(ServicesTable.HOURLY_PRICE.getColumn()); }
}