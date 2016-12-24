package japp.util;

public abstract class LongHelper {
	
	public static Long safeValueOf(final String string) {
		try {
			return Long.valueOf(string);
		} catch (NumberFormatException exception) {
			return null;
		}
	}
}
