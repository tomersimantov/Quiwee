package com.GUI.states.dashboard.calendar;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import com.GUI.constants.ColorConstants;
import com.GUI.states.dashboard.Tab;
import com.util.real_time.Week;

/**
 * A singleton class, providing two objects:
 * 		1. Daily calendar view
 * 		2. Weekly calendar view 
 */
public class CalendarTimeView extends JPanel
{
	private static final long serialVersionUID = 4429666829928454250L;
	private static final Border BORDER = new EtchedBorder(EtchedBorder.LOWERED);
	private static final int HOURS = 24;
	private static final int INDEX_THICKNESS = 40;
	private static final Font DESELECTED_DAY = new Font("Aller Display", Font.PLAIN, 15);
	private static final Font SELECTED_DAY = new Font("Aller Display", Font.BOLD, 15);
	private static final Color QUEUE_COLOR = Color.BLACK;
	
	private static HashMap<String, CalendarGrid> grids = new HashMap<String, CalendarGrid>();
	private GridBagConstraints gbc;
	private JPanel calendar, dayIndex, hourIndex, tablePane;
	private JLabel[] dayLabels;
	private HourPanel[] hourLabels;
	private CalendarGrid[][] table;
	private Dimension cellDim;
	private int todayIndex, cols;
	
	/* Singleton objects */
	private static final CalendarTimeView DAILY = new CalendarTimeView(1);
	private static final CalendarTimeView WEEKLY = new CalendarTimeView(7);
	
	/* Storage of singletons */
	@SuppressWarnings("unused")
	private static boolean myLittleInitHack = init();
	private static CalendarTimeView appliedView;
	private static Set<CalendarTimeView> unappliedViews;
	
	/**
	 * @param cols - Amount of days in the period
	 */
	private CalendarTimeView(int cols) {
		super(new BorderLayout());
		setBackground(Color.DARK_GRAY);
		
		this.cols = cols;
		this.gbc = new GridBagConstraints();
		this.calendar = new JPanel(new BorderLayout());
		this.calendar.setPreferredSize(new Dimension(Tab.DIM.width, Tab.DIM.height * 2 + 180));
		calendar.setBackground(QUEUE_COLOR);
		
		JScrollPane calendarScroll = new JScrollPane(calendar);
		calendarScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		calendarScroll.getVerticalScrollBar().setUnitIncrement(16);
		
		//declare dimensions
		int scrollBarWidth = calendarScroll.getVerticalScrollBar().getPreferredSize().width;
		int tableLength = Tab.DIM.height * 2;
		Dimension tableDim = new Dimension(Tab.DIM.width - INDEX_THICKNESS - scrollBarWidth - 20, tableLength);
		Dimension indexDim = new Dimension(INDEX_THICKNESS, tableLength);
		this.cellDim = new Dimension(tableDim.width / cols, tableDim.height / (HOURS * 2));
		
		calendarScroll.setPreferredSize(calendar.getPreferredSize());
		
		JPanel supportEastPane = new JPanel();
		supportEastPane.setPreferredSize(new Dimension(scrollBarWidth + 20, tableLength));
		calendar.add(supportEastPane, BorderLayout.EAST);
		
		this.tablePane = new JPanel(new GridBagLayout());
		tablePane.setPreferredSize(tableDim);
		tablePane.setOpaque(false);
		calendar.add(tablePane, BorderLayout.CENTER);
		
		//days index
		int todayIndexValue = LocalDateTime.now().getDayOfWeek().getValue();
		todayIndex = (todayIndexValue != 7) ? todayIndexValue : 0;
		DayOfWeek[] week;
		
		if (cols == 1) week = Week.getWeek(DayOfWeek.of(todayIndexValue)); //daily view
		else week = Week.getWeek(DayOfWeek.SUNDAY); //weekly view
		
		dayIndex = new JPanel(new GridBagLayout());
		dayIndex.setPreferredSize(new Dimension(Tab.DIM.width, INDEX_THICKNESS));
		dayIndex.setBackground(ColorConstants.COLOR_3);

		String tempStr;
		this.dayLabels = new JLabel[cols];
		Insets ins = new Insets(5, 50, 5, 46);
		for (int i = 0, x = 0; i < cols; i++, x++) {
			//if first, push a little more to the right
			if (i == 0) gbc.insets = new Insets(5, 75, 5, 45);
			else gbc.insets = ins;
			
			//decide if showing full name or only acronym
			if (cols == 1) tempStr = "" + week[i].name();
			else tempStr = "" + week[i].name().charAt(0);
			dayLabels[i] = new JLabel(tempStr);
			
			//colorize today
			if (i == todayIndex) {
				dayLabels[i].setForeground(Color.CYAN);
				dayLabels[i].setFont(SELECTED_DAY);
			}
			else {
				dayLabels[i].setForeground(ColorConstants.TEXT_COLOR_BRIGHT);
				dayLabels[i].setFont(DESELECTED_DAY);
			}
			
			//placement
			gbc.gridx = x;
			dayIndex.add(dayLabels[i], gbc);
		}
		
		//hours index
		hourIndex = new JPanel(new GridBagLayout());
		hourIndex.setPreferredSize(indexDim);
		hourIndex.setOpaque(false);
		
		Dimension hourDim = new Dimension(hourIndex.getPreferredSize().width, cellDim.height);
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = hourDim.width;
		gbc.ipady = hourDim.height - 10;
		gbc.gridx = 0;
		int minutes;
		
		this.hourLabels = new HourPanel[HOURS * 2];
		for (int i = 0, hour, j = 0, y = 0; i < hourLabels.length; i++, j++, y++) {
			hour = j / 2;
			
			if (i == 0) minutes = 0;
			else minutes = (i % 2 == 1) ? 30 : 0;
			
			hourLabels[i] = new HourPanel(hour, minutes);
			hourLabels[i].setBackground(ColorConstants.COLOR_2.darker());
			hourLabels[i].setPreferredSize(hourDim);
			hourLabels[i].setForeground(ColorConstants.TEXT_COLOR_BRIGHT);
			hourLabels[i].setBorder(BORDER);
			
			gbc.gridy = y;
			hourIndex.add(hourLabels[i], gbc);
		}
		calendar.add(hourIndex, BorderLayout.WEST);
		
		//calendar table
		gbc.ipadx = cellDim.width;
		gbc.ipady = cellDim.height;

		this.table = new CalendarGrid[HOURS * 2][cols];
		for (int i = 0, y = 0; i < table.length; i++, y++) {
			for (int j = 0, x = 0; j < table[i].length; j++, x++) {
				gbc.gridx = x;
				gbc.gridy = y;
				table[i][j] = getGrid(calculateGridDate(i, j, todayIndex, 0), i, j);
				table[i][j].setPreferredSize(cellDim);
				
				//if today, brighten the color
				if (j == todayIndex && getColumns() > 1)
					table[i][j].setBackground(table[i][j].getBackground().brighter());
				
				tablePane.add(table[i][j], gbc);
			}
		}
		add(calendarScroll, BorderLayout.CENTER);
	}
	
	/**
	 * Calculate the date of a grid.
	 * 
	 * @param row - The row of the grid
	 * @param col - The column of the grid
	 * @param todayIndex - Index of today (where Sunday is 0)
	 * @param addedWeeks - Amount of week added from today (or negative number if subtracted)
	 * @return the date of the specified grid.
	 */
	private LocalDateTime calculateGridDate(int row, int col, int todayIndex, int addedWeeks) {
		if (cols == 1) todayIndex = 0;
		
		LocalDateTime date = LocalDateTime.now().plusDays(col - todayIndex + addedWeeks * 7);
		date = date.withHour(row / 2);
		int minutes = (row % 2 == 0) ? 0 : 30;
		date = date.withMinute(minutes);
		
		return date;
	}
	
	/**
	 * @return the amount of columns in the periodic view.
	 */
	public int getColumns() { return table[0].length; }
	
	/**
	 * @return the panel of days.
	 */
	public JPanel getDayIndex() { return dayIndex; }
	
	/**
	 * @param row - The row of the grid
	 * @param col - The column of the grid
	 * @return the grid at the selected location.
	 */
	public CalendarGrid getGrid(int row, int col) { return table[row][col]; }
	
	/**
	 * Display the time view on the calendar tab.
	 * 
	 * @param menu - The menu panel
	 * @param addedWeeks - amount of weeks added from today (or negative if subtracted)
	 * @return true if the view changed.
	 */
	public boolean apply(JPanel menu, int addedWeeks) {
		
		//remove the previous index
		if (appliedView != null) {
			menu.remove(appliedView.dayIndex);
			unappliedViews.add(appliedView);
		}
		
		for (CalendarTimeView view : unappliedViews)
			menu.remove(view.dayIndex);
		
		//apply the new index
		unappliedViews.remove(this);
		appliedView = this;
		menu.add(dayIndex, BorderLayout.SOUTH);
		updateGrids(addedWeeks);
		
		revalidate();
		repaint();
		return true;
	}
	
	/**
	 * Update the calendar grids according to the requested date.
	 * 
	 * @param addedWeeks - Amount of weeks added from today (or negative number if subtracted)
	 */
	private void updateGrids(int addedWeeks) {
		tablePane.removeAll();
		for (int i = 0, y = 0; i < table.length; i++, y++) {
			for (int j = 0, x = 0; j < table[i].length; j++, x++) {
				gbc.gridx = x;
				gbc.gridy = y;
				table[i][j] = getGrid(calculateGridDate(i, j, todayIndex, addedWeeks), i, j);
				table[i][j].setPreferredSize(cellDim);
				
				if (!table[i][j].isOccupied()) {
					//if today, brighten the color
					if (j == todayIndex && addedWeeks == 0 && getColumns() > 1)
						table[i][j].setBackground(table[i][j].getBackground().brighter());
				}
				
				table[i][j].setBorder(BORDER);
				tablePane.add(table[i][j], gbc);
			}
		}
		
		revalidate();
		repaint();
	}
	
	/**
	 * Get a grid of the calendar, that represents 30 minutes.
	 * A grid points to the same object for all views.
	 * 
	 * @param date - The exact date of the grid (down to minutes)
	 * @param row - The grid's row
	 * @param col - The grid's column
	 * @return the grid at the selected location.
	 */
	public static CalendarGrid getGrid(LocalDateTime date, int row, int col) {
		String dateStr = date.getYear() + ":"
					   + date.getMonthValue() + ":"
					   + date.getDayOfMonth() + ":"
					   + date.getHour() + ":"
					   + date.getMinute();
		
		CalendarGrid cg = grids.get(dateStr);
		
		if (cg == null) {
			cg = new CalendarGrid(date, row, col);
			grids.put(dateStr, cg);
		}
		
		return cg;
	}
	
	/**
	 * Highlight the hour grid of both CalendarTimeView objects.
	 * 
	 * @param index - The index of the hour (row).
	 * 				  Put -1 to turn off.
	 */
	public static void highlightHour(int index) {
		for (int i = 0; i < appliedView.hourLabels.length; i++) {
			if (i == index) appliedView.hourLabels[i].setForeground(ColorConstants.TEXT_COLOR_SELECTED);
			else appliedView.hourLabels[i].setForeground(ColorConstants.TEXT_COLOR_BRIGHT);
		}
	}
	
	/**
	 * Highlight the day grid of both CalendarTimeView objects.
	 * 
	 * @param index - The index of the day (column).
	 * 				  Put -1 to turn off.
	 */
	public static void highlightDay(int index) {
		for (int i = 0; i < appliedView.dayLabels.length; i++) {
			if (i == index)
				appliedView.dayLabels[i].setForeground(ColorConstants.TEXT_COLOR_SELECTED);
			else if (i != appliedView.todayIndex)
				appliedView.dayLabels[i].setForeground(ColorConstants.TEXT_COLOR_BRIGHT);
			else
				appliedView.dayLabels[i].setForeground(Color.CYAN);
		}
	}
	
	/**
	 * Get a singleton CalendarTimeView object, displaying the calendar in bulks of days.
	 * 
	 * @return a daily calendar view.
	 */
	public static CalendarTimeView getDailyView() { return DAILY; }
	
	/**
	 * Get a singleton CalendarTimeView object, displaying the calendar in bulks of weeks.
	 * 
	 * @return a weekly calendar view.
	 */
	public static CalendarTimeView getWeeklyView() { return WEEKLY; }
	
	/**
	 * Initialize singleton objects.
	 * 
	 * @return garbage value.
	 */
	private static boolean init() {
		unappliedViews = new HashSet<CalendarTimeView>();
		unappliedViews.add(DAILY);
		unappliedViews.add(WEEKLY);
		return true;
	}
}