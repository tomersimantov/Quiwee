package com.GUI.states.dashboard.calendar;
import java.awt.Color;
import com.data.objects.Queue;

public class CalendarGridGroup
{
	private Queue queue;
	private CalendarGrid[][] grids;
	
	/**
	 * @param queue - The queue that the group will possess (can be null)
	 * @param rows - Amount of rows in the matrix of grids
	 * @param columns - Amount of columns in the matrix of grids
	 */
	public CalendarGridGroup(Queue queue, int rows, int columns) {
		this.grids = new CalendarGrid[rows][columns];
		this.queue = queue;
	}
	
	/**
	 * Add a grid to the matrix.
	 * 
	 * @param grid - The grid to add
	 * @param row - Row of the matrix
	 * @param col - Column of the matrix
	 */
	public void addGrid(CalendarGrid grid, int row, int col) {
		grids[row][col] = grid;
	}
	
	/**
	 * Remove the temporary color of the entire matrix. 
	 */
	public void decolorize() {
		if (grids.length < 1) return;
		else colorize(grids[0][0].getOriginColor());
	}
	
	/**
	 * Temporarily colorize the entire matrix.
	 * 
	 * @param color - The color to use
	 */
	public void colorize(Color color) {
		if (grids.length < 1 || grids[0][0].getBackground().equals(color)) return;
		
		for (int i = 0; i < grids.length; i++)
			for (int j = 0; j < grids[i].length; j++)
				if (grids[i][j] != null)
					grids[i][j].colorize(color);
	}
	
	/**
	 * Permanently colorize the entire matrix.
	 * 
	 * @param color - The color to use
	 */
	public void setBackground(Color color) {
		if (grids.length < 1 || grids[0][0].getBackground().equals(color)) return;
		
		for (int i = 0; i < grids.length; i++)
			for (int j = 0; j < grids[i].length; j++)
				if (grids[i][j] != null)
					grids[i][j].setBackground(color);
	}
	
	/**
	 * Add a queue to the entire matrix as a group.
	 * 
	 * @param q - The queue to add
	 */
	public void addQueue(Queue q) {
		if (grids.length < 1 || grids[0][0].isOccupied()) return;
		
		for (int i = 0; i < grids.length; i++)
			for (int j = 0; j < grids[i].length; j++)
				if (grids[i][j] != null)
					grids[i][j].operativeAddQueue(q, i);
	}
	
	/**
	 * Remove the group's queue.
	 */
	public void removeQueue() {
		if (grids.length < 1 || !grids[0][0].isOccupied()) return;
		
		for (int i = 0; i < grids.length; i++)
			for (int j = 0; j < grids[i].length; j++)
				if (grids[i][j] != null)
					grids[i][j].operativeRemoveQueue();
	}
	
	/**
	 * Conclude the group's queue.
	 */
	public void concludeQueue() {
		if (grids.length < 1 || !grids[0][0].isOccupied()) return;
		
		for (int i = 0; i < grids.length; i++)
			for (int j = 0; j < grids[i].length; j++)
				if (grids[i][j] != null)
					grids[i][j].operativeConcludeQueue();
	}
	
	/**
	 * @return this group's queue.
	 */
	public Queue getQueue() { return queue; }
}