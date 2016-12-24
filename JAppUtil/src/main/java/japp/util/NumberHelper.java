package japp.util;

public abstract class NumberHelper {
	
	public static final String DOT = ".";
	
	public static Number valueOf(final String string) {
		return string.contains(DOT) ? Double.valueOf(string) : Long.valueOf(string);
	}
	
	public static Number safeValueOf(final String string) {
		try {
			return valueOf(string);
		} catch (NumberFormatException exception) {
			return null;
		}
	}
}
