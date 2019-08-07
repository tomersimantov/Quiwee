package com.util.threads;

public abstract class LazyThread extends Thread implements Handleable
{
	private boolean run;
	
	public LazyThread() {
		this.run = false;
		start();
	}
	
	@Override
	public void run() {
		while (true) {
			if (run) {
				try {
					run = false;
					eventFunction();
				}
				catch (Exception e) { handleException(e); }
			}
			else ThreadUtility.delay(50);
		}
	}
	
	/**
	 * The function to call whenever the thread is woken up.
	 * 
	 * @throws Exception when something goes wrong with the function.
	 */
	protected abstract void eventFunction() throws Exception;
	
	/**
	 * Wake the thread and call its function exactly once.
	 * After the function execution, this thread goes back to sleep.
	 */
	public void wakeUp() { run = true; }
	
	@Override
	public void handleException(Exception e) {}
}