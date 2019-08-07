package com.GUI.states.dashboard;
import java.awt.Color;
import java.util.LinkedList;
import java.util.Queue;
import javax.swing.JOptionPane;
import com.controllers.ClientsController;

public class ClientsTab extends DatabaseTab
{
	private static final long serialVersionUID = 191160680735231675L;
	
	public ClientsTab() {
		super("clients", new ClientsController());
	}

	@Override
	protected String getTitle() { return "Clients"; }
	
	@Override
	protected Color getTabColor() { return new Color(60, 177, 137); }
	
	@Override
	protected String[] getDescription() {
		return new String[] {
			"Take a look at all of the clients that ever visited your business.",
			"You can add clients or delete them manually."
		};
	}
	
	@Override
	protected void deleteButtonFunction() {
		if (!table.isEditing()) {
			int[] rows = table.getSelectedRows();
			
			for(int i = 0; i < rows.length; i++) {
				//remove the selected rows from the data base
				String phone = (String) table.getValueAt(rows[i], 1);
				controller.delete(phone);
				
				//remove the selected rows from the table
				model.removeRow(rows[i] - i);
			}
			
			table.clearSelection();
		}
	}

	@Override
	protected void saveButtonFunction() {
		Queue<Integer> rowsToAdd = new LinkedList<Integer>();
		boolean add;
		
		//find the rows that need to be updated in the database
		for (int i = 0; i < table.getRowCount(); i++) {
			add = true;
			for (int j = 0; j < getTableColumns().length; j++) {
				String value = null;
				
				try {
					value = (String) table.getValueAt(i, j);
					if (value == null || value.equals(""))
						throw new ClassCastException();
				}
				catch (ClassCastException e) {
					add = false;
					break;
				}
			}
			if (add) rowsToAdd.add(i);
			else table.approveRow(i, false);
		}
		
		//insert the new rows to the database or update them if they already exist
		while (!rowsToAdd.isEmpty()) {
			int row = rowsToAdd.poll();
			String name = (String) table.getValueAt(row, 0);
			String phone = (String) table.getValueAt(row, 1);
			String country = (String) table.getValueAt(row, 2);
			String city = (String) table.getValueAt(row, 3);
			String street = (String) table.getValueAt(row, 4);
			Integer stNum;
			
			try { stNum = Integer.parseInt((String) table.getValueAt(row, 5)); }
			catch (ClassCastException e) {
				table.approveRow(row, false);
				continue;
			}
			
			controller.insertNew(name, phone, country, city, street, stNum);
			table.approveRow(row, true);
		}
		
		JOptionPane.showMessageDialog(this, "All valid changes were saved successfully.",
				  					  "", JOptionPane.INFORMATION_MESSAGE, null);
	}

	@Override
	protected String[] getTableColumns() {
		return new String[] {"Name", "Phone No.", "Country", "City", "Street", "Street No."};
	}

	@Override
	protected String[] getMysqlColumns() {
		return new String[] {"full_name", "phone_number", "country", "city", "street", "st_num"};
	}
}