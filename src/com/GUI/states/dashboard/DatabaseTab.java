package com.GUI.states.dashboard;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import com.GUI.constants.ColorConstants;
import com.GUI.constants.FontConstants;
import com.controllers.Controller;
import com.main.User;
import com.util.data.MysqlModifier;

public abstract class DatabaseTab extends Tab
{
	protected static class ColoredTable extends JTable
	{
		private static final long serialVersionUID = -6282672436053955358L;
		private static final Color NEUTRAL_COLOR = Color.WHITE;
		private static final Color APPROVE_COLOR = new Color(202, 255, 227);
		private static final Color DISAPPROVE_COLOR = new Color(255, 202, 202);
		
		private Set<Integer> approved, disapproved;
		
		public ColoredTable() {
			this.approved = new HashSet<Integer>();
			this.disapproved = new HashSet<Integer>();
		}
		
		/**
		 * Colorize a row that had been approved or disapproved.
		 * 
		 * @param row - The row to paint
		 * @param flag - True to approve or false to disapprove
		 */
		public void approveRow(int row, boolean flag) {
			neutralizeRow(row);
			if (flag) approved.add(row);
			else disapproved.add(row);
		}
		
		/**
		 * Restore a row's color back to neutral.
		 *  
		 * @param row - The row to paint
		 */
		public void neutralizeRow(int row) {
			approved.remove(row);
			disapproved.remove(row);
		}
		
		@Override
        public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
			Component c = super.prepareRenderer(renderer, row, col);
			Color color;
            
			if (!getSelectedRowsList().contains(row)) {
				if (approved.contains(row)) color = APPROVE_COLOR;
				else if (disapproved.contains(row)) color = DISAPPROVE_COLOR;
				else color = NEUTRAL_COLOR;
				
				c.setBackground(color);
				invalidate();
				revalidate();
				repaint();
			}
			
			return c;
		 }
		
		private List<Integer> getSelectedRowsList(){
			return Arrays.stream(getSelectedRows()).boxed().collect(Collectors.toList());
		}
	}
	
	private static final long serialVersionUID = -1581118177227279546L;
	
	protected ColoredTable table;
	protected DefaultTableModel model;
	protected Controller<?> controller;
	
	public DatabaseTab(String tableName, Controller<?> controller) {
		super(true, true, true);
		this.controller = controller;
		
		//retrieve all information from data base
		String[] columns = getTableColumns();
		String overallQuery = "SELECT * FROM " + tableName + " WHERE user_id = '" + User.getKey() + "'";
		List<List<String>> lists = new ArrayList<List<String>>();
		
		this.model = new DefaultTableModel(0, columns.length);
		model.setColumnIdentifiers(columns);
		
		Dimension tableDim = new Dimension(550, 400);
		this.table = new ColoredTable();
		table.setPreferredSize(tableDim);
		table.setModel(model);
		table.setForeground(ColorConstants.TEXT_COLOR_DARK);
		table.setFont(FontConstants.SMALL_LABEL_FONT.deriveFont((float) 12));
		table.getTableHeader().setBackground(getTabColor().darker());
		table.getTableHeader().setForeground(ColorConstants.TEXT_COLOR_BRIGHT);
		table.setRowHeight(30);
		
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		
		for (int i = 0; i < getMysqlColumns().length; i++) {
			//align all cells of that column to the center
			table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
			
			//retrieve a list for that column from the data base
			try { lists.add(MysqlModifier.readAllVARCHAR(overallQuery, getMysqlColumns()[i])); }
			catch (SQLException e) {}
			model.setRowCount(lists.get(i).size());
			
			for (int j = 0; j < lists.get(i).size(); j++)
				model.setValueAt(lists.get(i).get(j), j, i);
		}
		
		//set headers
		for (int i = 0; i < columns.length; i++)
			table.getColumnModel().getColumn(i).setHeaderValue(columns[i]);
		
		//table's scroll pane
		JPanel tableContainer = new JPanel(new BorderLayout());
		tableContainer.setPreferredSize(tableDim);
		
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setPreferredSize(tableDim);
		tableContainer.add(scrollPane, BorderLayout.PAGE_START);
		gbc.insets = new Insets(20, 20, 10, 10);
		addComponent(tableContainer, 0, 0);
	}
	
	@Override
	protected void addButtonFunction() {
		model.setNumRows(model.getRowCount() + 1);
	}
	
	/**
	 * @return an array of the table columns' names.
	 */
	protected abstract String[] getTableColumns();
	
	/**
	 * @return an array of the table columns' names as they appear in the data base.
	 */
	protected abstract String[] getMysqlColumns();
}