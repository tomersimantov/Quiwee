package com.GUI.windows.queue_dialog;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import com.GUI.constants.ColorConstants;
import com.GUI.constants.FontConstants;
import com.GUI.states.dashboard.calendar.CalendarGrid;
import com.controllers.Controller;
import com.controllers.QueuesController;
import com.controllers.ServicesController;
import com.data.objects.Client;
import com.data.objects.Queue;
import com.data.objects.Service;
import com.finance.CashRegister;
import com.util.GUI.swing.components.FocusField;
import com.util.GUI.swing.components.InteractiveLabel;
import com.util.math.DimensionalHandler;

public class QueueDialogWindow extends JFrame implements PropertyChangeListener
{
	private static final long serialVersionUID = 5348484787032161945L;
	private static final String MESSAGE = "New queue appointment for ";
	private static final Dimension DIM = new Dimension(620, 500);
	private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy HH:mm");
	private static final String SEARCH_MESSAGE = "search name or phone number";
	
	private LocalDateTime startTime, endTime;
	private CalendarGrid calendarGrid;
	private JLabel clientName, timeLab;
	private JPanel buttonsPane;
	private Client selectedClient;
	private Service selectedService;
	private ClientsSelectorTable clientTable;
	private JComboBox<String> servicesDropDown, endDropDown;
	private JCheckBox useServicePriceBox;
	private GridBagConstraints gridConstraints;
	private QueuesController controller;
	private CashRegister cashRegister;
	private boolean existingQueue;
	
	public QueueDialogWindow(CalendarGrid datedPanel, LocalDateTime date) {
		super(MESSAGE + date.format(FORMAT));
		setSize(DIM);
		setResizable(false);
		setLocationRelativeTo(null);
		
		this.controller = new QueuesController();
		this.gridConstraints = new GridBagConstraints();
		this.cashRegister = new CashRegister();
		this.calendarGrid = datedPanel;
		this.startTime = date;
		this.endTime = date.plusMinutes(30);
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setFocusable(true);
		mainPanel.requestFocus();
		mainPanel.setPreferredSize(DIM);
		mainPanel.setBackground(ColorConstants.BASE_COLOR);
		
		JPanel clientsPane = new JPanel(new GridBagLayout());
		clientsPane.setPreferredSize(DimensionalHandler.adjust(DIM, 50, 50));
		clientsPane.setOpaque(false);
		createClientPane(clientsPane);
		mainPanel.add(clientsPane, BorderLayout.WEST);
		
		JPanel detailsPane = new JPanel(new BorderLayout());
		detailsPane.setPreferredSize(DimensionalHandler.adjust(DIM, 50, 50));
		detailsPane.setOpaque(false);
		createDetailsPane(detailsPane);
		mainPanel.add(detailsPane, BorderLayout.CENTER);
		
		JPanel southPane = new JPanel(new BorderLayout());
		southPane.setPreferredSize(DimensionalHandler.adjust(DIM, 100, 50));
		southPane.setBackground(ColorConstants.BASE_COLOR);
		createSouthPane(southPane);
		mainPanel.add(southPane, BorderLayout.SOUTH);
		
		addWindowListener(new WindowListener() {
			@Override
			public void windowActivated(WindowEvent e) {}

			@Override
			public void windowClosed(WindowEvent e) {}

			@Override
			public void windowClosing(WindowEvent e) { calendarGrid.release(); }

			@Override
			public void windowDeactivated(WindowEvent e) {}

			@Override
			public void windowDeiconified(WindowEvent e) {}

			@Override
			public void windowIconified(WindowEvent e) {}

			@Override
			public void windowOpened(WindowEvent e) {}
		});
		
		add(mainPanel);
		pack();
		setVisible(true);
	}
	
	@Override
	public void dispose() {
		calendarGrid.release();
		super.dispose();
	}
	
	private void createClientPane(JPanel panel) {
		//dimensions and panel creation
		Dimension tableDim = DimensionalHandler.adjust(panel, 90, 80);
		Dimension searchButtonDim = new Dimension(20, 20);
		Dimension searchFieldDim = new Dimension(tableDim.width - searchButtonDim.width, searchButtonDim.height);
		
		//table
		this.clientTable = new ClientsSelectorTable();
		clientTable.setPreferredSize(tableDim);
		clientTable.setRowHeight(15);
		clientTable.setBackground(Color.WHITE);
		clientTable.subscribeToChanges(this);
		
		//search field and button
		FocusField searchField = new FocusField(SEARCH_MESSAGE);
		searchField.setPreferredSize(searchFieldDim);
		searchField.setHorizontalAlignment(JTextField.LEFT);
		gridConstraints.insets.top = 10;
		addComponent(panel, searchField, 0, 0);
		
		JButton searchButton = new JButton("...");
		searchButton.setPreferredSize(searchButtonDim);
		searchButton.setFont(new Font("Arial", Font.PLAIN, 7));
		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				clientTable.search(searchField.getText());
			}
		});
		addComponent(panel, searchButton, 1, 0);
		
		//force selection of only 1 row
		DefaultListSelectionModel selectionModel = new DefaultListSelectionModel();
		selectionModel.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
		clientTable.setSelectionModel(selectionModel);
		clientTable.setDefaultEditor(Object.class, null);
		
		//include table within a scroll pane
		JScrollPane clientScrollPane = new JScrollPane(clientTable);
		clientScrollPane.setPreferredSize(tableDim);
		gridConstraints.insets.right = -20;
		addComponent(panel, clientScrollPane, 0, 1);
	}
	
	private void createDetailsPane(JPanel panel) {
		//north details panel
		JPanel northPane = new JPanel(new BorderLayout());
		northPane.setPreferredSize(DimensionalHandler.adjust(panel, 100, 80));
		northPane.setOpaque(false);
		panel.add(northPane, BorderLayout.CENTER);
		
		//left mini pane
		JPanel northWestPane = new JPanel(new GridBagLayout());
		northWestPane.setPreferredSize(DimensionalHandler.adjust(northPane, 40, 100));
		northWestPane.setOpaque(false);
		northPane.add(northWestPane, BorderLayout.WEST);
		
		//right mini pane
		JPanel northEastPane = new JPanel(new GridBagLayout());
		northEastPane.setPreferredSize(DimensionalHandler.adjust(northPane, 60, 100));
		northEastPane.setOpaque(false);
		northPane.add(northEastPane, BorderLayout.EAST);
		
		//labels
		gridConstraints.insets = new Insets(10, 10, 10, 10);
		
		JLabel clientLab = new JLabel("client:");
		clientLab.setFont(FontConstants.SMALL_LABEL_FONT);
		gridConstraints.insets.left = 42;
		addComponent(northWestPane, clientLab, 0, 0);
		
		JLabel serviceLab = new JLabel("service:");
		serviceLab.setFont(FontConstants.SMALL_LABEL_FONT);
		gridConstraints.insets.left = 31;
		addComponent(northWestPane, serviceLab, 0, 1);
		
		JLabel endTimeLab = new JLabel("end time:");
		endTimeLab.setFont(FontConstants.SMALL_LABEL_FONT);
		gridConstraints.insets.left = 19;
		addComponent(northWestPane, endTimeLab, 0, 2);
		
		JLabel queueTime = new JLabel("queue time:");
		queueTime.setFont(FontConstants.SMALL_LABEL_FONT);
		gridConstraints.insets.bottom = 40;
		gridConstraints.insets.left = 0;
		addComponent(northWestPane, queueTime, 0, 3);
		
		//details
		gridConstraints.insets = new Insets(10, 10, 10, 10);
		Font detailsFont = FontConstants.SMALL_LABEL_FONT;
		Font dropDownFont = detailsFont.deriveFont((float) (4 * detailsFont.getSize() / 5));
		Dimension dropDownSize = DimensionalHandler.adjust(northEastPane, 80, 0);
		dropDownSize.height = 25;
		
		//name of the selected client
		this.clientName = new JLabel("-not selected-");
		clientName.setPreferredSize(dropDownSize);
		clientName.setForeground(ColorConstants.TEXT_COLOR_DARK);
		clientName.setFont(FontConstants.SMALL_LABEL_FONT);
		gridConstraints.insets.left = -25;
		gridConstraints.insets.top = 8;
		addComponent(northEastPane, clientName, 1, 0);
		
		//services drop down menu
		//retrieve all services from database
		Controller<Service> serviceCont = new ServicesController();
		Object[][] servicesRows = serviceCont.getkeywordResults(null);
		List<String> servicesNamesList = new ArrayList<String>();
		
		//add all services names to the list
		for (int i = 0; i < servicesRows.length; i++)
			servicesNamesList.add((String) servicesRows[i][1]);
		
		//create a new list with the options "-" and those that the data base has
		List<String> serviceChoices = new ArrayList<String>();
		serviceChoices.add("-");
		serviceChoices.addAll(servicesNamesList);
		
		//convert the list to a String[] object
		String[] serviceChoicesArr = serviceChoices.toArray(new String[serviceChoices.size()]);
		
		this.servicesDropDown = new JComboBox<String>(serviceChoicesArr);
		servicesDropDown.setPreferredSize(dropDownSize);
		servicesDropDown.setFocusable(false);
		servicesDropDown.setForeground(ColorConstants.TEXT_COLOR_DARK);
		servicesDropDown.setFont(dropDownFont);
		servicesDropDown.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				Controller<Service> serviceCont = new ServicesController();
				selectedService = (Service) serviceCont.getObj(servicesDropDown.getSelectedItem());
				if (selectedService != null) useServicePriceBox.setSelected(true);
			}
		});
		
		gridConstraints.insets.left = -25;
		gridConstraints.insets.top = 10;
		addComponent(northEastPane, servicesDropDown, 1, 1);
		
		//ending time drop down menu
		this.endDropDown = new JComboBox<String>();
		endDropDown.setPreferredSize(dropDownSize);
		endDropDown.setFocusable(false);
		endDropDown.setForeground(ColorConstants.TEXT_COLOR_DARK);
		endDropDown.setFont(dropDownFont);
		endDropDown.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				if (endDropDown.getItemCount() > 0 && !timeLab.getText().equals("")) {
					String item = (String) endDropDown.getSelectedItem();
					String hour = item.substring(0, 2);
					String minutes = item.substring(3, 5);
					endTime = endTime.withHour(Integer.parseInt(hour));
					endTime = endTime.withMinute(Integer.parseInt(minutes));
					timeLab.setText(timeLab.getText().substring(0, 8) + hour + ":" + minutes);
				}
			}
		});
		gridConstraints.insets.left = -25;
		addComponent(northEastPane, endDropDown, 1, 2);
		
		//time of the queue from beginning to end
		this.timeLab = new JLabel(getTimeRange(startTime, endTime));
		timeLab.setForeground(ColorConstants.COLOR_2);
		timeLab.setFont(FontConstants.SMALL_LABEL_FONT);
		gridConstraints.insets.bottom = 38;
		addComponent(northEastPane, timeLab, 1, 3);
		
		//update the end time in drop down menu and time range
		updateEndTimeMenu();
	}
	
	private void createSouthPane(JPanel panel) {
		//advanced options pane 
		JPanel advancedOptionsPane = new JPanel(new GridBagLayout());
		advancedOptionsPane.setPreferredSize(DimensionalHandler.adjust(panel, 100, 60));
		advancedOptionsPane.setOpaque(false);
		panel.add(advancedOptionsPane, BorderLayout.CENTER);
		
		//labels and fields
		Font smallerFont = FontConstants.SMALL_LABEL_FONT.deriveFont((float) 12);
		
		JLabel priceLab = new JLabel("Price:");
		priceLab.setFont(FontConstants.SMALL_LABEL_FONT);
		gridConstraints.insets = new Insets(10, -452, 10, 10);
		addComponent(advancedOptionsPane, priceLab, 0, 1);
		
		JTextField priceFld = new JTextField();
		priceFld.setPreferredSize(new Dimension(80, 20));
		priceFld.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) { updateRegister(); }
			
			@Override
			public void insertUpdate(DocumentEvent e) { updateRegister(); }
			
			@Override
			public void changedUpdate(DocumentEvent e) {}
			
			private void updateRegister() {
				try { cashRegister.setPrice(Double.parseDouble(priceFld.getText())); }
				catch (NumberFormatException ex) { cashRegister.setPrice(-1); }
			}
		});
		
		gridConstraints.insets.left = -297;
		addComponent(advancedOptionsPane, priceFld, 1, 1);
		
		JLabel currencyLab = new JLabel("NIS.");
		currencyLab.setFont(smallerFont);
		gridConstraints.insets.left = -177;
		addComponent(advancedOptionsPane, currencyLab, 2, 1);
		
		JCheckBox hourlyPriceBox = new JCheckBox("Per hour");
		hourlyPriceBox.setFont(smallerFont);
		hourlyPriceBox.setOpaque(false);
		hourlyPriceBox.setFocusable(false);
		hourlyPriceBox.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				cashRegister.setAsPricePerHour(hourlyPriceBox.isSelected());
			}
		});
		
		gridConstraints.insets.top = -10;
		gridConstraints.insets.left = -424;
		addComponent(advancedOptionsPane, hourlyPriceBox, 0, 2);
		
		this.useServicePriceBox = new JCheckBox("Use service pricing");
		useServicePriceBox.setFont(smallerFont);
		useServicePriceBox.setOpaque(false);
		useServicePriceBox.setFocusable(false);
		useServicePriceBox.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				//apply service pricing
				if (useServicePriceBox.isSelected())
					cashRegister.priceByService(selectedService);
				
				//apply manual pricing as selected in the fields below
				else {
					try { cashRegister.setPrice(Double.parseDouble(priceFld.getText())); }
					catch (NumberFormatException ex) { cashRegister.setPrice(0); }
				
					cashRegister.setAsPricePerHour(hourlyPriceBox.isSelected());
				}
			}
		});
		
		gridConstraints.insets = new Insets(-30, -452, 10, 10);
		addComponent(advancedOptionsPane, useServicePriceBox, 0, 0);
		
		//buttons panel
		this.buttonsPane = new JPanel(new GridBagLayout());
		buttonsPane.setPreferredSize(DimensionalHandler.adjust(panel, 100, 40));
		buttonsPane.setBackground(ColorConstants.COLOR_3);
		panel.add(buttonsPane, BorderLayout.SOUTH);
		
		//queue creation button
		InteractiveLabel saveBtn = new InteractiveLabel("Save");
		saveBtn.setPreferredSize(new Dimension(90, 20));
		saveBtn.setFont(FontConstants.SMALL_LABEL_FONT);
		saveBtn.setForeground(ColorConstants.TEXT_COLOR_BRIGHT);
		saveBtn.enableSelectionColor(false);
		saveBtn.setFunction(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				if (selectedClient == null) return null;
				
				try {
					//remove previous queue first
					if (existingQueue) calendarGrid.removeQueue();
					
					//add new one
					controller.insertNew(selectedClient, selectedService, startTime, endTime);
					
					dispose();
				}
				catch (NullPointerException ex) { call(); }
				return null;
			}
		});
		
		gridConstraints.insets = new Insets(10, 10, 10, 10);
		addComponent(buttonsPane, saveBtn, 0, 0);
		
		clientTable.search(null);
	}
	
	private void updateEndTimeMenu() {
		endDropDown.removeAllItems();
		int hour = startTime.getHour();
		int minutes = startTime.getMinute();
		boolean hasStartTime30 = minutes == 30;
		String excess, minutesStr;
		int amount = (24 - hour) * 2 - 1;

		for (int i = 0, j = 1, currentHour; i < amount; i++, j++) {
			//user chose start time XX:30
			if (i == 0 && hasStartTime30) {
				currentHour = hour + 1; 
				minutesStr = "00";
				i++;
				j++;
			}
			else {
				currentHour = hour + (j / 2);
				minutesStr = (i % 2 == 0) ? "30" : "00";
			}
			
			excess = (currentHour <= 9) ? "0" : "";
			endDropDown.addItem(excess + currentHour + ":" + minutesStr);
		}
		
		endDropDown.addItem("00:00");
	}
	
	/**
	 * Input a queue this dialog will show.
	 * 
	 * @param q - Queue to show
	 */
	public void inputQueueInfo(Queue q) {
		existingQueue = true;
		selectedClient = q.getClient();
		clientName.setText(selectedClient.getName());
		startTime = q.getStartTime();
		updateEndTimeMenu();
		endDropDown.setSelectedIndex(q.getDuration() / 30 - 1);
		endTime = q.getStartTime().plusMinutes(q.getDuration());
		timeLab.setText(getTimeRange(startTime, endTime));
		
		//change title
		setTitle(getTitle().replaceAll("New", "Edit"));
		
		try { servicesDropDown.setSelectedItem(q.getService().getName()); }
		//item might no longer be available
		catch (NullPointerException e) { servicesDropDown.setSelectedItem("-"); }
		
		//add extra buttons
		gridConstraints.insets = new Insets(10, 10, 10, 10);
		
		//add delete button
		InteractiveLabel deleteQueueBtn = new InteractiveLabel("Delete");
		deleteQueueBtn.setPreferredSize(new Dimension(90, 20));
		deleteQueueBtn.setFont(FontConstants.SMALL_LABEL_FONT);
		deleteQueueBtn.setForeground(ColorConstants.TEXT_COLOR_BRIGHT);
		deleteQueueBtn.enableSelectionColor(false);
		deleteQueueBtn.setFunction(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				controller.delete(q);
				dispose();
				return null;
			}
		});
		addComponent(buttonsPane, deleteQueueBtn, 1, 0);
		
		//add conclude button
		InteractiveLabel concludeQueueBtn = new InteractiveLabel("Conclude");
		concludeQueueBtn.setPreferredSize(new Dimension(90, 20));
		concludeQueueBtn.setFont(FontConstants.SMALL_LABEL_FONT);
		concludeQueueBtn.setForeground(ColorConstants.TEXT_COLOR_BRIGHT);
		concludeQueueBtn.enableSelectionColor(false);
		JFrame currentFrame = this;
		concludeQueueBtn.setFunction(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				//check if checkout is successful
				if (cashRegister.checkout(q)) dispose();
				else {
					JOptionPane.showMessageDialog(currentFrame,
												  "Please enter a valid pricing.",
												  "", JOptionPane.ERROR_MESSAGE, null);
				}
				
				return null;
			}
		});
		addComponent(buttonsPane, concludeQueueBtn, 2, 0);
	}
	
	private String getTimeRange(LocalDateTime start, LocalDateTime end) {
		int hour, min;
		String hourPrefix, minPrefix;
		
		hour = start.getHour();
		min = start.getMinute();
		hourPrefix = (hour < 10) ? "0" : "";
		minPrefix = (min < 10) ? "0" : "";
		String startStr = hourPrefix + hour + ":" + minPrefix + min;
		
		hour = end.getHour();
		min = end.getMinute();
		hourPrefix = (hour < 10) ? "0" : "";
		minPrefix = (min < 10) ? "0" : "";
		String endStr = hourPrefix + hour + ":" + minPrefix + min;
		
		return startStr + " - " + endStr;
	}
	
	private void addComponent(JPanel panel, Component component, int x, int y) {
		gridConstraints.gridx = x;
		gridConstraints.gridy = y;
		panel.add(component, gridConstraints);
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		switch (e.getPropertyName()){
			case "client selection": {
				selectedClient = (Client) e.getNewValue();
				
				if (selectedClient != null) {
					clientName.setText(selectedClient.getName());
					clientName.setForeground(ColorConstants.COLOR_2);
				}
			}
		}
	}
}