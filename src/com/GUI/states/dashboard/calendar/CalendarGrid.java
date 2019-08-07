package com.GUI.states.dashboard.calendar;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.time.LocalDateTime;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.MatteBorder;
import com.GUI.constants.ColorConstants;
import com.GUI.constants.FontConstants;
import com.GUI.windows.queue_dialog.QueueDialogWindow;
import com.controllers.QueuesController;
import com.data.objects.Queue;

public class CalendarGrid extends JPanel implements MouseListener
{
	private static final long serialVersionUID = 3709458352878645312L;
	private static final Border BORDER = new EtchedBorder(EtchedBorder.LOWERED);
	private static final Color BASE_COLOR = new Color(221, 221, 221);
	private static final Color CONCLUSION_COLOR = new Color(76, 185, 46);
	private static final Color PRESSED_COLOR = new Color(235, 121, 29);
	private static final Color HOVER_COLOR = ColorConstants.TEXT_COLOR_SELECTED;
	
	private static boolean handleDialog;
	private static CalendarGridGroup focusGroup;
	private LocalDateTime date;
	private Color originColor;
	private int row, col;
	private boolean occupied, firstRow, locked;
	private String occupyingName;
	private QueuesController controller;
	private Queue queue;
	
	/**
	 * @param date - Date of the grid, as implied from the whole calendar table
	 * @param row - Row of the calendar table
	 * @param col - Column of the calendar table
	 */
	public CalendarGrid(LocalDateTime date, int row, int col) {
		super(new BorderLayout());
		setBackground(BASE_COLOR);
		addMouseListener(this);
		setBorder(BORDER);
		
		this.controller = new QueuesController();
		this.date = date;
		this.row = row;
		this.col = col;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {}
	
	@Override
	public void mouseReleased(MouseEvent e) {}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		if (!handleDialog) {
			CalendarTimeView.highlightHour(row);
			CalendarTimeView.highlightDay(col);
			highlight(HOVER_COLOR);
		}
	}
	
	public void mouseExited(MouseEvent e) {
		if (!handleDialog && !isOccupied()) {
			CalendarTimeView.highlightHour(-1);
			CalendarTimeView.highlightDay(-1);
			colorize(originColor);
		}
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		if (locked || handleDialog) return;
		
		//open a queue dialog
		QueueDialogWindow dialog = new QueueDialogWindow(this, date);
		
		//insert info from the original first row queue
		if (isOccupied()) dialog.inputQueueInfo(queue);
		
		if (focusGroup != null) focusGroup.colorize(PRESSED_COLOR);
		colorize(PRESSED_COLOR);
		handleDialog = true;
	}
	
	/**
	 * Temporarily highlight this grid.
	 * If this grid is a part of a larger group, highlight its group as a whole.
	 * 
	 * @param color - The color to temporarily paint this grid with
	 */
	private void highlight(Color color) {
		//highlight single grid with no group
		if (!isOccupied()) {
			colorize(color);
			
			
			//turn off and remove previous group
			if (focusGroup != null) {
				focusGroup.decolorize();
				focusGroup = null;
			}
		}
		//this is a grid that needs to be in a group - call the first row
		else if (!firstRow) getMainQueueGrid().highlight(color);
		//this is the first row in a group
		else {
			//form a new group
			if (focusGroup == null || focusGroup.getQueue() != queue) {
				//turn off previous group
				if (focusGroup != null) focusGroup.decolorize();
				
				//regroup
				int rows = queue.getDuration() / 30;
				focusGroup = group(rows, 1);
			}
			
			focusGroup.colorize(color);
		}
	}
	
	/**
	 * Get the first grid that this grid's queue appears on.
	 * If this is the first grid, or it rather contains no queue, return this grid.
	 * 
	 * @return the first grid that contains this grid's queue.
	 */
	private CalendarGrid getMainQueueGrid() {
		if (firstRow || !isOccupied()) return this;
		else {
			LocalDateTime tempDate = date.minusMinutes(30);
			return CalendarTimeView.getGrid(tempDate, row - 1, col).getMainQueueGrid();
		}
	}
	
	/**
	 * Deselect the grid and release the entire table.
	 */
	public void release() {
		if (focusGroup != null) focusGroup.colorize(originColor);
		colorize(originColor);
		handleDialog = false;
	}
	
	/**
	 * Add a queue, starting from this grid downwards.
	 * 	
	 * @param q - The new queue to add
	 */
	public void addQueue(Queue q) {
		//form new group
		int rows = q.getDuration() / 30;
		CalendarGridGroup group = group(rows, 1);
		group.addQueue(q);
	}
	
	/**
	 * Remove this grid's queue.
	 * This is an encapsulated method.
	 * 
	 * @param q - The queue to add
	 * @param row - This grid's row within the group
	 */
	void operativeAddQueue(Queue q, int row) {
		Insets insets = new Insets(0, 1, 0, 1);
		
		/* 
		 * Decide which kind of border should the grid possess.
		 * Also write down the client's name in the first row of the queue
		 */
		if (row == 0) {
			String clientName = q.getClient().getName();
			occupyingName = clientName.substring(0, clientName.indexOf(" "));
			firstRow = true;
			insets.top = 1;
		}
		
		//last row
		if (row == (q.getDuration() / 30 - 1)) insets.bottom = 1;
		
		setBorder(new MatteBorder(insets, Color.BLACK));
		setBackground(ColorConstants.COLOR_2);
		occupied = true;
		queue = q;
		
		//lock the grid if the queue is already finished and counted
		if (q.isConcluded()) operativeConcludeQueue();
	}
	
	/**
	 * Remove the queue of this grid's group.
	 */
	public void removeQueue() {
		//form new group
		int rows = queue.getDuration() / 30;
		CalendarGridGroup group = group(rows, 1);
		group.removeQueue();
	}
	
	/**
	 * Remove this grid's queue.
	 * This is an encapsulated method.
	 */
	void operativeRemoveQueue() {
		super.setBorder(BORDER);
		setBackground(originColor);
		occupied = false;
		firstRow = false;
		controller.delete(queue);
		queue = null;
	}
	
	/**
	 * Conclude the queue of this grid's group.
	 */
	public void concludeQueue() {
		//form new group
		int rows = queue.getDuration() / 30;
		CalendarGridGroup group = group(rows, 1);
		group.concludeQueue();
	}
	
	/**
	 * Conclude this grid's queue.
	 * This is an encapsulated method.
	 */
	void operativeConcludeQueue() {
		locked = true;
		setBackground(CONCLUSION_COLOR);
	}
	
	/**
	 * Set the background of the grid without overwriting its original color.
	 * The color set here is rather temporary.
	 * 
	 * @param color - Temporary background color
	 */
	public void colorize(Color color) {
		super.setBackground(color);
		revalidate();
        repaint();
	}
	
	@Override
	public void setBackground(Color color) {
		colorize(color);
		originColor = color;
	}
	
	@Override
	public void setBorder(Border border) {
		if (!isOccupied()) super.setBorder(border);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if (firstRow) {
			Color color;
			if (getBackground().equals(HOVER_COLOR))
				color = ColorConstants.TEXT_COLOR_DARK;
			else
				color = ColorConstants.TEXT_COLOR_BRIGHT;
			
			Font font = FontConstants.SMALL_LABEL_FONT;
			g.setFont(font);
			g.setColor(color);
			g.drawString(occupyingName, 5, font.getSize());
		}
	}
	
	/**
	 * Create a group of grids, starting from this.
	 * 
	 * @param rows - Amount of rows to include downwards (including this grid)
	 * @param columns - Amount of columns to include to the right (including this grid)
	 * @return a group of grids.
	 */
	private CalendarGridGroup group(int rows, int columns) {
		CalendarGridGroup group = new CalendarGridGroup(queue, rows, columns);
		LocalDateTime tempDate = date;
		
		for (int c = 0; c < columns; c++) {
			tempDate = date.plusDays(c);
			
			for (int r = 0; r < rows; r++, tempDate = tempDate.plusMinutes(30)) {
				CalendarGrid nextGrid = CalendarTimeView.getGrid(tempDate, row + r, col + c);
				group.addGrid(nextGrid, r, c);
			}
		}
		
		return group;
	}
	
	/**
	 * @return the queue this grid contains (null if it doesn't)
	 */
	public Queue getQueue() { return queue; }
	
	/**
	 * @return true if this grid contains a queue.
	 */
	public boolean isOccupied() { return occupied; }
	
	/**
	 * @return the grid's date
	 */
	public LocalDateTime getDate() { return date; }
	
	/**
	 * @return the original color of the grid (without mouse events)
	 */
	public Color getOriginColor() { return (originColor != null) ? originColor : getBackground(); }
}