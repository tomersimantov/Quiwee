package com.GUI.windows.queue_dialog;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import com.controllers.ClientsController;
import com.controllers.Controller;
import com.data.objects.Client;
import com.data.tables.ClientsTable;

public class ClientsSelectorTable extends JTable
{
	private static final long serialVersionUID = 4435162125281120381L;
	
	private Controller<Client> clientsCont;
	private PropertyChangeSupport propertyChanger;
	private Client selectedClient;
	
	public ClientsSelectorTable() {
		super(0, 0);
		this.propertyChanger = new PropertyChangeSupport(this);
		this.clientsCont = new ClientsController();
		
		//build model and columns
		DefaultTableModel model = new DefaultTableModel(0, 2);
		setModel(model);
		getColumnModel().getColumn(0).setHeaderValue("Name");
		getColumnModel().getColumn(1).setHeaderValue("Phone No.");
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				Client oldClient = selectedClient;
				selectedClient = selectClient(e);
				propertyChanger.firePropertyChange("client selection", oldClient, selectedClient);
			}
		});
	}
	
	/**
	 * Search for a specific keyword in the table.
	 * Only results that contain the keyword will be displayed.
	 * 
	 * @param keyword - The search term
	 */
	public void search(String keyword) {
		Object[][] clientsRows = clientsCont.getkeywordResults(keyword, ClientsTable.NAME, ClientsTable.PHONE);
		((DefaultTableModel) getModel()).setRowCount(clientsRows.length);
		
		//set all values in the table
		for (int i = 0; i < clientsRows.length; i++) {
			setValueAt(clientsRows[i][2], i, 0); //name
			setValueAt(clientsRows[i][1], i, 1); //phone
		}
	}
	
	/**
	 * Select a client by double clicking it inside the table.
	 * 
	 * @param e - Relevant MouseEvent object
	 * @return the selected client.
	 */
	private Client selectClient(MouseEvent e) {
        Point point = e.getPoint();
        int row = rowAtPoint(point);
        
        if (e.getClickCount() == 2 && getSelectedRow() != -1 && row != -1) {
			Object phone = getValueAt(row, 1);
			return clientsCont.getObj(phone);
        }
        else return null;
	}
	
	/**
	 * Subscribe to property changes from this table.
	 * 
	 * @param observer - The subscriber
	 */
	public void subscribeToChanges(PropertyChangeListener observer) {
		propertyChanger.addPropertyChangeListener(observer);
	}
}