package com.util.math;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumeralHandler
{
	/**
	 * Round a decimal number to a fixed number of places after the point.
	 * 
	 * @param value - The number to round
	 * @param places - Amount of places after the point to show
	 * @return a rounded decimal number.
	 */
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();
	 
	    BigDecimal bigDec = new BigDecimal(Double.toString(value));
	    bigDec = bigDec.setScale(places, RoundingMode.HALF_UP);
	    return bigDec.doubleValue();
	}
	
	/**
	 * Round an integer to a fixed number of digits.
	 * 
	 * @param value - The number to round
	 * @param places - Amount of digits to show
	 * @return a rounded integer.
	 */
	public static int round(int value, int places) {
		return Integer.parseInt(("" + value).substring(0, places));
	}
	
	/**
	 * Count the amount of digits in a number.
	 * 
	 * @param num - The number to count
	 * @return the amount of digits in the number.
	 */
	public static int countDigits(long num) {
		if (num == 0) return 1;
		
		int counter = 0;
		
		while (num > 0) {
			counter++;
			num /= 10;
		}
		
		return counter;
	}
	
	/**
	 * Shift an integer number to the right, adding '0's to its left side.
	 * If the number has more digits than requested, digits from the right will be removed instead. 
	 * 
	 * @param number - Integer number to shift right
	 * @param spaces - The amount of digits it should possess
	 * @return a String object of the number, with the exact amount of digits specified.
	 */
	public static String shiftRight(int number, int spaces) {
		int digits = countDigits(number);
		String str = "" + number;
		
		if (digits == spaces) return str;
		else if (digits > spaces) return str.substring(0, spaces);
		else
			for (int i = 0; i < spaces - digits; i++)
				str = "0" + str;
		
		return str;
	}
	
	/**
	 * Compress a big number to a minimal sized String.
	 * Ex. input: 783,240 -> output: "0.7M"
	 *	   input: 19,500 -> output: "19K"
	 *	   input: 123 -> "123"
	 *
	 * @param value - The value to compress
	 * @return a compressed String representation of the value.
	 */
	public static String compress(long value) {
    	int digits = countDigits(value);
    	if (digits <= 3) return "" + value;
    	
    	boolean upgrade = false;
    	char compression;
    	switch (digits) {
    		case 4:
    		case 5: compression = 'K'; break;
    		case 6: upgrade = true; compression = 'M'; break;
    		case 7:
    		case 8: compression = 'M'; break;
    		case 9: upgrade = true; compression = 'B'; break;
    		case 10:
    		case 11: compression = 'B'; break;
    		case 12: upgrade = true; compression = 'T'; break;
    		case 13:
    		case 14: compression = 'T'; break;
    		default: return "E" + (digits - 1);
    	}
    	
		//display as 0.(number name + 1)
    	if (upgrade) {
    		double periodVal = value / Math.pow(10, digits);
    		periodVal = round(periodVal, 1);
    		return "" + periodVal + compression;
    	}
		//display as x.(number name)
    	else {
    		value /= Math.pow(10, digits - (digits % 3));
    		return "" + value + compression;
    	}
    }
}