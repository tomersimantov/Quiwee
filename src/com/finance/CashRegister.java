package com.finance;
import com.controllers.QueuesController;
import com.data.objects.Queue;
import com.data.objects.Service;

/**
 * This class manages the financial part of a queue,
 * and also assures that the payment of the queue is not negative.
 */
public class CashRegister
{
	private double price;
	private boolean hourly;
	private QueuesController queueCont;
	
	public CashRegister() {
		this.price = -1;
		this.hourly = false;
		this.queueCont = new QueuesController();
	}
	
	/**
	 * Conclude a passed queue and lock it.
	 * 
	 * @param q - The queue to check out
	 * @return true if the queue has been checked out successfully.
	 */
	public boolean checkout(Queue q) {
		return checkout(q, q.getDuration());
	}
	
	/**
	 * @param duration - The duration of the queue.
	 * 					 Only enter this argument if the queue
	 * 					 lasted a different duration from what
	 * 					 it was originally scheduled for.
	 * 
	 * @see checkout(Queue)
	 */
	public boolean checkout(Queue q, int duration) {
		if (price < 0) return false;
		
		double finalPayment = hourly ? price * (duration / 60) : price;
		queueCont.conclude(q, finalPayment);
		return true;
	}
	
	/**
	 * Use a service pricing.
	 * Calling setPrice(double) or setAsPricePerHour(boolean) may cancel this method.
	 * 
	 * @param s - The service to use
	 */
	public void priceByService(Service s) {
		if (s != null) {
			setAsPricePerHour(s.isHourly());
			setPrice(s.getPrice());
		}
	}
	
	/**
	 * @param price - The overall cost of the queue
	 */
	public void setPrice(double price) { this.price = price; }
	
	/**
	 * @param flag - True if the cost of the queue is per hour
	 */
	public void setAsPricePerHour(boolean flag) { hourly = flag; }
}