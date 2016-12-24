package japp.util;

public abstract class DoubleHelper {
	
	public static Double safeValueOf(final String string) {
		try {
			return Double.valueOf(string);
		} catch (NumberFormatException exception) {
			return null;
		}
	}
}
