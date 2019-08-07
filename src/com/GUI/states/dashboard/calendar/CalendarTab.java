package com.GUI.states.dashboard.calendar;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Callable;
import javax.swing.JLabel;
import javax.swing.JPanel;
import com.GUI.constants.ColorConstants;
import com.GUI.constants.FontConstants;
import com.GUI.states.dashboard.Tab;
import com.controllers.ClientsController;
import com.controllers.Controller;
import com.controllers.QueuesController;
import com.data.MysqlPuller;
import com.data.objects.Client;
import com.data.tables.QueuesTable;
import com.util.GUI.swing.components.InteractiveIcon;
import com.util.GUI.swing.components.InteractiveLabel;
import com.util.math.DimensionalHandler;
import com.util.math.NumeralHandler;
import com.util.real_time.TimeStampConverter;

public class CalendarTab extends JPanel
{
	private static class CurrentDate extends JLabel
	{
		private static final long serialVersionUID = -6950255606994746339L;
		
		private LocalDateTime date;
		private int addedWeeks;
		
		public CurrentDate(LocalDateTime date) {
			this.date = date;
			this.addedWeeks = 0;
			setDate(date, false);
		}
		
		public void forward() {
			date = date.plusDays(7);
			addedWeeks++;
			setDate(date, false);
		}
		
		public void backward() {
			date = date.minusDays(7);
			addedWeeks--;
			setDate(date, false);
		}
		
		public void now() {
			LocalDateTime nearestSaturday = LocalDateTime.now();
			while (nearestSaturday.getDayOfWeek() != DayOfWeek.SATURDAY)
				nearestSaturday = nearestSaturday.plusDays(1);
			
			setDate(nearestSaturday, true);
		}
		
		public void setDate(LocalDateTime d, boolean randomAccess) {
			date = d;
			//move the date to the nearest Saturday
			while (date.getDayOfWeek() != DayOfWeek.SATURDAY) date = date.plusDays(1);
			
			LocalDateTime prefix = date.minusDays(6);
			setText(formatDate(prefix) + "   to   " + formatDate(date));
			
			//count added weeks if date was set randomly at runtime
			if (randomAccess) {
				addedWeeks = 0;
				LocalDateTime now = LocalDateTime.now();
				LocalDateTime pivot = d;
				int counter = 0;
				
				//make hours and minutes irrelevant
				pivot = pivot.withHour(now.getHour());
				pivot = pivot.withMinute(now.getMinute());
				
				while (pivot.isAfter(now)) {
					pivot = pivot.minusDays(1);
					
					if (++counter == 7) {
						addedWeeks++;
						counter = 0;
					}
				}
				
				while (pivot.isBefore(now)) {
					pivot = pivot.plusDays(1);
					
					if (++counter == 7) {
						addedWeeks--;
						counter = 0;
					}
				}
			}
		}
		
		private String formatDate(LocalDateTime date) {
			String dayOfWeek = date.getDayOfWeek().name();
			dayOfWeek = dayOfWeek.charAt(0) + dayOfWeek.substring(1, dayOfWeek.length()).toLowerCase();
			
			String day = NumeralHandler.shiftRight(date.getDayOfMonth(), 2);
			String month = NumeralHandler.shiftRight(date.getMonthValue(), 2);
			String year = NumeralHandler.shiftRight(date.getYear(), 4);
			
			return dayOfWeek + ", " + day + "-" + month + "-" + year;
		}
		
		public int getAddedWeeks() { return addedWeeks; }
	}
	
	private static final long serialVersionUID = 6286301053089922829L;
	public static final Dimension MENU_DIM = new Dimension(Tab.DIM.width, 80);
	
	private JPanel menu, viewSelector;
	private CalendarTimeView currentView;
	private CurrentDate currentDate;
	private InteractiveLabel daily, weekly;
	private QueuesController controller;
	
	public CalendarTab() {
		super(new BorderLayout());
		setPreferredSize(Tab.DIM);
		setBackground(Color.YELLOW);
		this.controller = new QueuesController();
		
		this.menu = new JPanel(new BorderLayout());
		menu.setPreferredSize(MENU_DIM);
		menu.setBackground(ColorConstants.COLOR_3);
		
		//view selector panel
		this.viewSelector = new JPanel(new BorderLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		viewSelector.setPreferredSize(new Dimension(Tab.DIM.width, MENU_DIM.height / 2));
		viewSelector.setBackground(ColorConstants.COLOR_3);
		
		//west panel
		JPanel westPane = new JPanel(new BorderLayout());
		westPane.setPreferredSize(DimensionalHandler.adjust(MENU_DIM, 60, 100));
		westPane.setOpaque(false);
		
		LocalDateTime saturday = LocalDateTime.now();
		while (saturday.getDayOfWeek() != DayOfWeek.SATURDAY) saturday = saturday.plusDays(1);
		
		this.currentDate = new CurrentDate(saturday);
		currentDate.setFont(FontConstants.SMALL_LABEL_FONT);
		currentDate.setForeground(ColorConstants.TEXT_COLOR_BRIGHT);
		
		JPanel dateAssistPane = new JPanel(new BorderLayout());
		dateAssistPane.setPreferredSize(DimensionalHandler.adjust(westPane.getPreferredSize(), 80, 100));
		dateAssistPane.setOpaque(false);
		dateAssistPane.add(currentDate, BorderLayout.LINE_START);
		westPane.add(dateAssistPane, BorderLayout.EAST);
		
		JPanel westIconsAssistPane = new JPanel(new GridBagLayout());
		westIconsAssistPane.setPreferredSize(DimensionalHandler.adjust(westPane.getPreferredSize(), 20, 100));
		westIconsAssistPane.setOpaque(false);
		
		gbc.insets = new Insets(10, -10, 10, 10);
		gbc.gridx = 0;
		gbc.gridy = 0;
		InteractiveIcon back = new InteractiveIcon("date_back.png");
		back.setSelectedIcon("date_back_ptr.png");
		back.setFunction(new Callable<Void>() {
			public Void call() throws Exception {
				currentDate.backward();
				currentView.apply(menu, currentDate.getAddedWeeks());
				return null;
			}
		});
		westIconsAssistPane.add(back, gbc);
		
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.gridx = 1;
		InteractiveIcon refresh = new InteractiveIcon("refresh.png");
		refresh.setSelectedIcon("refresh_ptr.png");
		refresh.setFunction(new Callable<Void>() {
			public Void call() throws Exception {
				currentDate.now();
				currentView.apply(menu, currentDate.getAddedWeeks());
				return null;
			}
		});
		westIconsAssistPane.add(refresh, gbc);
		
		westPane.add(westIconsAssistPane, BorderLayout.WEST);
		viewSelector.add(westPane, BorderLayout.WEST);
		
		//center panel
		JPanel centerPane = new JPanel(new GridBagLayout());
		centerPane.setPreferredSize(DimensionalHandler.adjust(MENU_DIM, 40, 100));
		centerPane.setOpaque(false);
		
		gbc.insets.left = -30;
		gbc.gridx = 0;
		this.daily = new InteractiveLabel("Daily");
		daily.setFont(FontConstants.SMALL_LABEL_FONT);
		daily.setForeground(ColorConstants.TEXT_COLOR_BRIGHT);
		daily.setSelectColor(ColorConstants.COLOR_2.brighter());
		daily.setFunction(new Callable<Void>() {
			public Void call() {
				setTimeView(CalendarTimeView.getDailyView());
				weekly.release();
				return null;
			}
		});
		centerPane.add(daily, gbc);
		
		gbc.insets.left = 10;
		gbc.gridx = 1;
		this.weekly = new InteractiveLabel("Weekly");
		weekly.setFont(FontConstants.SMALL_LABEL_FONT);
		weekly.setForeground(ColorConstants.TEXT_COLOR_BRIGHT);
		weekly.setSelectColor(ColorConstants.COLOR_2.brighter());
		weekly.setFunction(new Callable<Void>() {
			public Void call() {
				setTimeView(CalendarTimeView.getWeeklyView());
				daily.release();
				return null;
			}
		});
		centerPane.add(weekly, gbc);
		
		gbc.insets = new Insets(10, 10, 10, -150);
		gbc.gridx = 2;
		InteractiveIcon forward = new InteractiveIcon("date_forward.png");
		forward.setSelectedIcon("date_forward_ptr.png");
		forward.setFunction(new Callable<Void>() {
			public Void call() throws Exception {
				currentDate.forward();
				currentView.apply(menu, currentDate.getAddedWeeks());
				return null;
			}
		});
		centerPane.add(forward, gbc);
		
		viewSelector.add(centerPane, BorderLayout.CENTER);
		menu.add(viewSelector, BorderLayout.NORTH);
		add(menu, BorderLayout.NORTH);
		
		//show weekly calendar view by default
		weekly.mousePressed(null);
		
		importAllQueues();
	}
	
	/**
	 * Change the periodic view of the calendar.
	 * 
	 * @param timeView - The time period to display
	 */
	private void setTimeView(CalendarTimeView timeView) {
		if (currentView == timeView) return;
		else if (timeView.apply(menu, currentDate.getAddedWeeks())) {
			if (currentView != null) remove(currentView);
			currentView = timeView;
			add(currentView, BorderLayout.CENTER);
			revalidate();
			repaint();
		}
	}
	
	/**
	 * Import all queues from the database to the calendar.
	 */
	private void importAllQueues() {
		List<Object> clientPhoneNumber = MysqlPuller.pullAll(QueuesTable.CLIENT_PHONE);
		List<Object> startTime = MysqlPuller.pullAll(QueuesTable.START_TIME);
		Controller<Client> clientCont = new ClientsController();
		
		for (int i = 0; i < clientPhoneNumber.size(); i++) {
			LocalDateTime startTimeFormat = TimeStampConverter.toLocalDateTime((String) startTime.get(i));
			Client client = (Client) clientCont.getObj(clientPhoneNumber.get(i));
			if (client != null) controller.importQueue(client, startTimeFormat);
		}
	}
}