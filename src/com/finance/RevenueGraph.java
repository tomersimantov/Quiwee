package com.finance;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.YearMonth;
import javax.swing.JPanel;
import com.data.Pullable;
import com.data.tables.QueuesTable;
import com.util.data.MysqlModifier;
import com.util.math.NumeralHandler;
import com.util.math.Range;
import com.util.real_time.TimeStampConverter;
import com.util.threads.LazyThread;

public class RevenueGraph extends JPanel
{
	private static class Updater extends LazyThread
	{
		private RevenueGraph graph;
		
		public Updater(RevenueGraph graph) {
			this.graph = graph;
		}
		
		@Override
		protected void eventFunction() throws Exception {
			graph.init();
			graph.repaint();
		}
	}
	
	private static final long serialVersionUID = 4665950926585696035L;
	
	private int numberOfDays;
	private int[] values;
	private Color graphColor;
	private Updater updater;
	
	public RevenueGraph(Color graphColor) {
		setOpaque(false);
		this.updater = new Updater(this);
		this.graphColor = graphColor;
		init();
	}
	
	public void update() { updater.wakeUp(); }
	
	private void init() {
		LocalDateTime now = LocalDateTime.now();
		YearMonth yearMonth = YearMonth.of(now.getYear(), now.getMonthValue());
		numberOfDays = yearMonth.lengthOfMonth();
		values = getData(now);
	}
	
	private int[] getData(LocalDateTime thisMonth) {
		Pullable column = QueuesTable.PRICE;
		String query;
		String tableName = column.getTableName();
		char acronyms = tableName.charAt(0);
		int[] vals = new int[numberOfDays];
		thisMonth = thisMonth.withHour(0);
		thisMonth = thisMonth.withMinute(0);
		thisMonth = thisMonth.withSecond(0);
		thisMonth = thisMonth.withNano(0);
		LocalDateTime thisDay, nextDay;
		
		for (int i = 1; i <= numberOfDays; i++) {
			thisDay = thisMonth.withDayOfMonth(i);
			
			//end of month
			if (i == numberOfDays) {
				//end of year
				if (thisDay.getMonthValue() < 12)
					nextDay = thisDay.plusMonths(1);
				else {
					nextDay = thisDay.plusYears(1);
					nextDay = nextDay.withMonth(1);
				}
				
				nextDay = nextDay.withDayOfMonth(1);
			}
			else nextDay = thisDay.plusDays(1);
			
			query = "SELECT SUM(" + acronyms + "." + column.getColumn().getName() + " ) AS 'revenue' "
				  + "FROM " + tableName + " " + acronyms + " "
				  + "WHERE " + acronyms + "." + QueuesTable.START_TIME.getColumn().getName() + " BETWEEN "
				  + "'" + TimeStampConverter.toString(thisDay) + "' "
				  + "AND '" + TimeStampConverter.toString(nextDay) + "'";
			
			try { vals[i - 1] = MysqlModifier.readINT(query, "revenue"); }
			catch (SQLException ex) { vals[i - 1] = 0; }
		}
		
		return vals;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		paintComponent((Graphics2D) g);
	}
	
	private void paintComponent(Graphics2D g) {
		Dimension dim = getPreferredSize();
		
		//draw boundaries
		int paramDim = 28;
		g.setColor(Color.BLACK);
		g.drawLine(paramDim, 0, paramDim, dim.height - paramDim);
		g.drawLine(paramDim, dim.height - paramDim, dim.width, dim.height - paramDim);
		
		/* Parameters */
		Font font = new Font("Tahoma", Font.PLAIN, 12);
		g.setFont(font);
		
		//add bottom parameters
		int[] dayParams = { 1, 5, 10, 15, 20, 25, numberOfDays };
		int notchSpace = dim.width / numberOfDays;
		int bottomY = dim.height - paramDim;
		
		for (int i = 0, x = paramDim + 2; i < numberOfDays; i++, x += notchSpace) {
			//add notch
			g.drawLine(x, bottomY, x, bottomY + 3);
			
			//add parameter
			int paramIndex = indexOf(dayParams, i + 1);
			if (paramIndex != -1) {
				int paramX = x - font.getSize() / 4 * NumeralHandler.countDigits(dayParams[paramIndex]); 
				g.drawString("" + dayParams[paramIndex], paramX, dim.height - 2);
			}
		}
		
		//add left parameters
		int highestParam = roundToClosestDecimal(getHighest(values));
		Range<Integer> range = new Range<Integer>(0, 10);
		Range<Integer> effect = new Range<Integer>(getLowest(values), highestParam);
		
		for (int i = 10, y = 10; i >= 0; i--, y += (int) ((dim.height - paramDim) / 10.2)) {
			//add notch
			g.drawLine(paramDim - 3, y, paramDim, y);
			
			//add parameter
			int scale = (int) Range.significanceOf(i, range, effect);
			String scaleStr = NumeralHandler.compress(scale);
			int x = (3 - scaleStr.length()) * (font.getSize() / 2);
			g.drawString(NumeralHandler.compress(scale), x, y + font.getSize() / 4);
		}
		
		/* Values */
		int zeroX = paramDim + 2;
		int zeroY = dim.height - paramDim - 5;
		int valueSpace = dim.width / numberOfDays;
		Range<Integer> distance = new Range<Integer>(0, zeroY);
		Point a, b = null;
		g.setColor(graphColor);
		g.setStroke(new BasicStroke(2));
		
		for (int i = 0; i < values.length - 1; i++) {
			int compressedValue;
			
			if (b == null) {
				compressedValue = (int) Range.significanceOf(values[i], effect, distance);
				a = new Point(zeroX - i * valueSpace, zeroY - compressedValue);
			}
			else a = b;
			
			compressedValue = (int) Range.significanceOf(values[i + 1], effect, distance);
			b = new Point(zeroX + (i + 1) * valueSpace, zeroY - compressedValue);
			
			connectPoints(g, a, b);
		}
	}
	
	private int indexOf(int[] arr, int k) {
		for (int i = 0; i < arr.length; i++)
			if (arr[i] == k) return i;
		
		return -1;
	}
	
	private int getHighest(int[] values) {
		int max = Integer.MIN_VALUE;
		
		for (int i = 0; i < values.length; i++)
			if (values[i] > max) max = values[i];
		
		return max;
	}
	
	private int getLowest(int[] values) {
		int min = Integer.MAX_VALUE;
		
		for (int i = 0; i < values.length; i++)
			if (values[i] < min) min = values[i];
		
		return min;
	}
	
	private int roundToClosestDecimal(int num) {
		int digits = NumeralHandler.countDigits(num);
		int exessDecimalValue = (int) Math.pow(10, digits - 1);
		int msb = (int) (num / exessDecimalValue);
		return (msb + 1) * exessDecimalValue;
	}
	
	private void connectPoints(Graphics g, Point a, Point b) {
		g.drawLine((int) a.getX(), (int) a.getY(), (int) b.getX(), (int) b.getY());
	}
}