package com.GUI.states.dashboard;
import java.awt.Color;
import java.util.LinkedList;
import java.util.Queue;
import javax.swing.JOptionPane;
import com.controllers.ServicesController;

public class ServicesTab extends DatabaseTab
{
	private static final long serialVersionUID = 4305725457016866299L;

	public ServicesTab() {
		super("services", new ServicesController());
	}

	@Override
	protected String getTitle() { return "Services"; }
	
	@Override
	protected Color getTabColor() { return new Color(201, 162, 58); }

	@Override
	protected String[] getDescription() {
		return new String[] {
			"Services are there to make the creation of similar queues easier for you.",
			"Create and modify your services right here!"
		};
	}
	
	@Override
	protected void addButtonFunction() {
		super.addButtonFunction();
		table.setValueAt(0, table.getRowCount() - 1, 2);
	}
	
	@Override
	protected void deleteButtonFunction() {
		if (!table.isEditing()) {
			int[] rows = table.getSelectedRows();
			
			for(int i = 0; i < rows.length; i++) {
				//remove the selected rows from the data base
				String name = (String) table.getValueAt(rows[i], 0);
				controller.delete(name);
				
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
			String name = ((String) table.getValueAt(row, 0)).replaceAll("'", "''");
			double price;
			boolean hourly;
			
			try {
				price = Double.parseDouble((String) table.getValueAt(row, 1));
				hourly = Boolean.parseBoolean((String) table.getValueAt(row, 2));
			}
			catch (ClassCastException e) {
				table.approveRow(row, false);
				continue;
			}

			controller.insertNew(name, price, hourly);
			table.approveRow(row, true);
		}
		
		JOptionPane.showMessageDialog(this, "All valid changes were saved successfully.",
									  "", JOptionPane.INFORMATION_MESSAGE, null);
	}
	
	@Override
	protected String[] getTableColumns() {
		return new String[] {"Name", "Price", "Price is hourly"};
	}
	
	@Override
	protected String[] getMysqlColumns() {
		return new String[] {"name", "price", "hourly"};
	}
}