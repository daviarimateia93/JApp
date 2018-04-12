package japp.util;

public abstract class DoubleHelper {

    protected DoubleHelper() {

    }

    public static Double safeValueOf(final String string) {
        try {
            return Double.valueOf(string);
        } catch (final NumberFormatException exception) {
            return null;
        }
    }
}
