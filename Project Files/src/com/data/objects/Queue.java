package com.data.objects;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import com.data.MysqlPuller;
import com.data.tables.QueuesTable;
import com.main.User;
import com.util.data.MysqlModifier;
import com.util.data.MysqlRow;
import com.util.real_time.TimeStampConverter;

/**
 * This class represents a scheduled queue, and directly fetches data from the database.  
 */
public class Queue extends MysqlRow
{
	private int duration;
	
	/**
	 * @param client - The client that the queue is for
	 * @param service - The service of the queue
	 * @param start - Queue's start time
	 * @param end - Queue's end time
	 */
	public Queue(Client client, Service service, LocalDateTime start, LocalDateTime end) {
		super("queues");
		
		setField(QueuesTable.CLIENT_PHONE.getColumn(), client.getPhoneNumber());
		
		String serviceName = null;
		double price = 0;
		if (service != null) {
			serviceName = service.getName();
			price = service.getPrice();
		}
		
		setField(QueuesTable.START_TIME.getColumn(), TimeStampConverter.toString(start));
		setServiceName(serviceName);
		setConcluded(false);
		setEndTime(end);
		setPrice(price);
		
		duration = (int) ChronoUnit.MINUTES.between(getStartTime(), getEndTime());
	}
	
	/**
	 * A constructor that imports a queue from the database.
	 * 
	 * @param client - The client that the queue is for
	 * @param start - Queue's start time
	 * @throws SQLException when the queue does not exist in the database.
	 */
	public Queue(Client client, LocalDateTime start) throws SQLException {
		super("queues");
		
		setField(QueuesTable.CLIENT_PHONE.getColumn(), client.getPhoneNumber());
		setField(QueuesTable.START_TIME.getColumn(), TimeStampConverter.toString(start));
		
		if (isInDatabase()) {
			Object[][] rows = MysqlPuller.pullRows(QueuesTable.class, selectAllQuery());
			
			setServiceName((String) rows[0][2]);
			setPrice((double) rows[0][3]);
			setEndTime(TimeStampConverter.toLocalDateTime((String) rows[0][5]));
			setConcluded((boolean) rows[0][6]);
		}
		else throw new SQLException();
	}
	
	/**
	 * Conclude the queue after its over.
	 * Prevent any changes to the queue and allow the use of its data for the statistics. 
	 * 
	 * @param price - The cost of the queue
	 * @return true is the queue has been concluded successfully.
	 */
	public boolean conclude(double price) {
		setPrice(price);
		setConcluded(true);
		boolean save = save();
		return save;
	}
	
	@Override
	public boolean save() {
		if (isConflicted()) return false;
		else return super.save();
	}
	
	/**
	 * Check if the queue is conflicted with another scheduled queue.
	 * A conflict occurs when the start time or end time of a queue collide
	 * with those of another queue.
	 * 
	 * @return true is this queue is conflicted with another queue.
	 */
	public boolean isConflicted() {
		char acronyms = tableName.charAt(0);
		String query = "SELECT EXISTS ( "
					 + "SELECT * "
					 + "FROM " + tableName + " " + acronyms + " "
					 
					  /* The checked queue is not this queue */
					 + "WHERE (" + acronyms + "." + QueuesTable.CLIENT_PHONE.getColumn().getName() + " "
					 + "!= '" + getClientPhoneNumber() + "' "
					 + "OR " + acronyms + "." + QueuesTable.START_TIME.getColumn().getName() + " "
					 + "!= '" + TimeStampConverter.toString(getStartTime()) + "') " 
					 
					 /* End time is between the next queue's start and end */
					 + "AND ('" + TimeStampConverter.toString(getEndTime()) + "' "
					 + "BETWEEN " + acronyms + "." + QueuesTable.START_TIME.getColumn().getName() + " "
					 + "AND " + acronyms + "." + QueuesTable.END_TIME.getColumn().getName() + ") "
					 
					 /* Start time is before other's start and end time is after other's end */
					 + "OR ('" + TimeStampConverter.toString(getStartTime()) + "' "
					 + "< " + acronyms + "." + QueuesTable.START_TIME.getColumn().getName() + " "
					 + "AND '" + TimeStampConverter.toString(getEndTime()) + "' "
					 + "> " + acronyms + "." + QueuesTable.END_TIME.getColumn().getName() + ")) "
					
					 + "AS 'invalid'";
		
		System.out.println(query);
		
		try { if (MysqlModifier.readBOOLEAN(query, "invalid")) return true; }
		catch (SQLException ex) { ex.printStackTrace(); }
		
		return false;
	}
	
	@Override
	protected void addFields() {
		addKeyField(QueuesTable.USER_ID.getColumn(), User.getKey());
		addKeyField(QueuesTable.CLIENT_PHONE.getColumn(), null);
		addKeyField(QueuesTable.START_TIME.getColumn(), null);
		addLiquidField(QueuesTable.SERVICE_NAME.getColumn(), null);
		addLiquidField(QueuesTable.PRICE.getColumn(), null);
		addLiquidField(QueuesTable.END_TIME.getColumn(), null);
		addLiquidField(QueuesTable.IS_CONCLUDED.getColumn(), null);
	}
	
	/**
	 * @param s - The name of the service
	 */
	public void setServiceName(String s) { setField(QueuesTable.SERVICE_NAME.getColumn(), s); }

	/**
	 * @param t - New end time for the queue
	 */
	public void setEndTime(LocalDateTime t) {
		setField(QueuesTable.END_TIME.getColumn(), TimeStampConverter.toString(t));
		duration = (int) ChronoUnit.MINUTES.between(getStartTime(), getEndTime());
	}
	
	/**
	 * @param p - An updated price for the queue
	 */
	public void setPrice(double p) { setField(QueuesTable.PRICE.getColumn(), p); }
	
	/**
	 * @param flag - True to consider this queue as concluded or false otherwise
	 */
	private void setConcluded(boolean flag) { setField(QueuesTable.IS_CONCLUDED.getColumn(), flag); }
	
	/**
	 * @return the client that own the queue.
	 */
	public Client getClient() {
		try { return new Client(getClientPhoneNumber()); }
		catch (SQLException e) { return null; }
	}
	
	/**
	 * @return the service of the queue.
	 */
	public Service getService() {
		try { return new Service(getServiceName()); }
		catch (SQLException e) { return null; }
	}
	
	/**
	 * @return the client's phone number (primary key).
	 */
	public String getClientPhoneNumber() {
		return (String) getField(QueuesTable.CLIENT_PHONE.getColumn());
	}
	
	/**
	 * @return the name of the queue's service.
	 */
	public String getServiceName() {
		return (String) getField(QueuesTable.SERVICE_NAME.getColumn());
	}
	
	/**
	 * @return the queue's start time (primary key).
	 */
	public LocalDateTime getStartTime() {
		return TimeStampConverter.toLocalDateTime((String) getField(QueuesTable.START_TIME.getColumn()));
	}
	
	/**
	 * @return the queue's end time.
	 */
	public LocalDateTime getEndTime() {
		return TimeStampConverter.toLocalDateTime((String) getField(QueuesTable.END_TIME.getColumn()));
	}
	
	/**
	 * @return true if the queue is already concluded.
	 */
	public boolean isConcluded() {
		return (boolean) getField(QueuesTable.IS_CONCLUDED.getColumn());
	}
	
	/**
	 * @return the cost of the queue (for the client).
	 */
	public double getPrice() { return (double) getField(QueuesTable.PRICE.getColumn()); }
	
	/**
	 * @return the overall duration of the queue in minutes.
	 */
	public int getDuration() { return duration; }
}