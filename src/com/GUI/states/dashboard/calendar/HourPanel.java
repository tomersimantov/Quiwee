package com.GUI.states.dashboard.calendar;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.JPanel;

public class HourPanel extends JPanel
{
	private static final long serialVersionUID = 1276077948686426652L;
	private final static Font FONT = new Font("Tahoma", Font.PLAIN, 11);
	
	private String hourStr;
	private Color foreground;
	
	/**
	 * @param hour - The lable's hour
	 * @param minutes - The label's minutes
	 */
	public HourPanel(int hour, int minutes) {
		String hourExcess = (hour < 10) ? "0" : "";
		String minuteExcess = (minutes < 10) ? "0" : "";
		this.hourStr = hourExcess + hour + ":" + minuteExcess + minutes;
		this.foreground = Color.BLACK;
	}
	
	@Override
	public void setForeground(Color c) {
		super.setForeground(c);
		foreground = c;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
	    g.setColor(foreground);
	    g.setFont(FONT);
	    
	    int midX = getPreferredSize().width / 2 - hourStr.length() * FONT.getSize() / 4;
	    int midY = getPreferredSize().height / 2 + FONT.getSize() / 2;
	    g.drawString(hourStr, midX, midY);
	    
	    revalidate();
	    repaint();
	}
}