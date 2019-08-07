package com.GUI.states.dashboard;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import com.GUI.constants.ColorConstants;
import com.finance.RevenueGraph;

public class RevenueTab extends Tab
{
	private static final long serialVersionUID = 8938381598113494448L;
	
	private RevenueGraph graph;
	
	public RevenueTab() {
		super(false, false, false);
		
		this.graph = new RevenueGraph(ColorConstants.COLOR_2);
		graph.setPreferredSize(new Dimension(550, 400));
		
		gbc.insets = new Insets(20, 20, 10, 10);
		addComponent(graph, 0, 0);
	}

	@Override
	protected String getTitle() { return "Revenue"; }

	@Override
	protected String[] getDescription() {
		return new String[] {
			"The revenue graph helps your examine your sales over the month",
			"and calculate your actions accordingly."
		};
	}

	@Override
	protected Color getTabColor() { return new Color(138, 209, 75); }

	@Override
	protected void addButtonFunction() {}

	@Override
	protected void deleteButtonFunction() {}

	@Override
	protected void saveButtonFunction() {}
	
	public void update() { graph.update(); }
}