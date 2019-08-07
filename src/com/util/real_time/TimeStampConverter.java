package com.util.real_time;
import java.time.LocalDateTime;
import com.util.math.NumeralHandler;

public class TimeStampConverter
{
	/**
	 * Convert a formatted String to a LocalDateTime object.
	 * 
	 * @param str - Formatted String of format 'yyyy-MM-dd hh:mm:ss'
	 * @return a date object, or null if the String is not formatted correctly.
	 */
	public static LocalDateTime toLocalDateTime(String str) {
		try { 
			LocalDateTime date = LocalDateTime.now();
			
			date = date.withYear(Integer.parseInt(str.substring(0, 4)));
			date = date.withMonth(Integer.parseInt(str.substring(5, 7)));
			date = date.withDayOfMonth(Integer.parseInt(str.substring(8, 10)));
			date = date.withHour(Integer.parseInt(str.substring(11, 13)));
			date = date.withMinute(Integer.parseInt(str.substring(14, 16)));
			date = date.withSecond(Integer.parseInt(str.substring(17, 19)));
			
			return date;
		}
		catch (NumberFormatException | IndexOutOfBoundsException ex) { return null; }
	}
	
	/**
	 * Convert a LocalDateTime object to a formatted String, compatible with databases.
	 * 
	 * @param date - LocalDateTime object to covert
	 * @return a String of format 'yyyy-MM-dd hh:mm:ss'.
	 */
	public static String toString(LocalDateTime date) {
		//2019-07-11 02:00:43
		
		String yyyy = "" + NumeralHandler.shiftRight(date.getYear(), 4);
		String MM = "" + NumeralHandler.shiftRight(date.getMonthValue(), 2);
		String dd = "" + NumeralHandler.shiftRight(date.getDayOfMonth(), 2);
		String hh = "" + NumeralHandler.shiftRight(date.getHour(), 2);
		String mm = "" + NumeralHandler.shiftRight(date.getMinute(), 2);
		String ss = "" + NumeralHandler.shiftRight(date.getSecond(), 2);
		
		return yyyy + "-" + MM + "-" + dd + " " + hh + ":" + mm + ":" + ss;
	}
}