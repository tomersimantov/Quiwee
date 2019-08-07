package com.controllers;
import java.sql.SQLException;
import java.time.LocalDateTime;
import com.GUI.states.StateManager;
import com.GUI.states.dashboard.Dashboard;
import com.GUI.states.dashboard.calendar.CalendarGrid;
import com.GUI.states.dashboard.calendar.CalendarTimeView;
import com.data.MysqlPuller;
import com.data.Pullable;
import com.data.objects.Client;
import com.data.objects.Queue;
import com.data.objects.Service;
import com.data.tables.QueuesTable;

/**
 * This class controls and manages queues in the database.
 * 
 * All arguments:
 * 		1. Client - The queue's client
 *		2. Service - The service of the queue (can be null)
 *		3. LocalDateTime - Queue start time
 *		4. LocalDateTime - Queue end time
 * 
 * Key arguments:
 * 		1. Client - The queue's client
 *		2. LocalDateTime - Queue start time
 * 
 * @author Niv Kor
 */
public class QueuesController extends Controller<Queue>
{
	@Override
	public Queue getObj(Object... obj) {
		try {
			Client client = (Client) obj[0];
			LocalDateTime start = (LocalDateTime) obj[1];
			return new Queue(client, start);
		}
		catch (SQLException | ClassCastException | ArrayIndexOutOfBoundsException ex) { return null; }
	}
	
	@Override
	public Queue createObj(Object... obj) {
		try {
			Client client = (Client) obj[0];
			Service service = (Service) obj[1];
			LocalDateTime start = (LocalDateTime) obj[2];
			LocalDateTime end = (LocalDateTime) obj[3];
			
			Queue queue = new Queue(client, service, start, end);
			
			//check if the queue overrides another one
			if (!queue.isConflicted()) {
				CalendarGrid grid = getGrid(start);
				if (!grid.isOccupied()) grid.addQueue(queue);
				return queue;
			}
			else return null;
		}
		catch (ClassCastException | ArrayIndexOutOfBoundsException ex) { return null; }
	}
	
	/**
	 * Import a queue from the database to the calendar.
	 * 
	 * @param client - The queue's client
	 * @param start - Queue's start time
	 * @return true if the queue was imported successfully.
	 */
	public boolean importQueue(Client client, LocalDateTime start) {
		try {
			CalendarGrid grid = getGrid(start);
			Queue queue = getObj(client, start);
			grid.addQueue(queue);
			return true;
		}
		catch (NullPointerException ex) { return false; }
	}
	
	@Override
	public boolean delete(Queue queue) {
		boolean deleted = super.delete(queue);
		
		if (deleted) removeFromCalendar(queue.getStartTime());
		return deleted;
	}
	
	@Override
	public boolean delete(Object... obj) {
		boolean deleted = super.delete(obj);
		
		if (deleted) removeFromCalendar((LocalDateTime) obj[1]);
		return deleted;
	}
	
	public boolean conclude(Queue q, double payment) {
		if (q == null || payment < 0) return false;
		else {
			if (q.conclude(payment)) {
				CalendarGrid grid = getGrid(q.getStartTime());
				grid.concludeQueue();
				
				//update the revenue graph
				Dashboard dashboard = (Dashboard) StateManager.getAppliedState(Dashboard.class);
				if (dashboard != null) dashboard.updateRevenue();
				
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Remove a queue from the calendar.
	 * 
	 * @param startTime - Queue's start time
	 */
	private void removeFromCalendar(LocalDateTime startTime) {
		CalendarGrid grid = getGrid(startTime);
		grid.removeQueue();
	}
	
	/**
	 * @param startTime - Queue's start time
	 * @return the calendar's grid of the queue.
	 */
	private CalendarGrid getGrid(LocalDateTime startTime) {
		int halfHour = (startTime.getMinute() == 30) ? 1 : 0;
		int row = startTime.getHour() * 2 + halfHour;
		int column = startTime.getDayOfWeek().getValue();
		if (column == 7) column = 0; //sunday
		
		return CalendarTimeView.getGrid(startTime, row, column);
	}
	
	@Override
	public Object[][] getkeywordResults(String keyword, Pullable... fields) {
		return MysqlPuller.pullKeywordRows(QueuesTable.class, keyword, fields);
	}
}