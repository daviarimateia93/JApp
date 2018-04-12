package japp.util;

public abstract class BooleanHelper {

    protected BooleanHelper() {

    }

    public static Boolean safeValueOf(final String string) {
        if (StringHelper.isNullOrBlank(string)) {
            return null;
        } else {
            return Boolean.valueOf(string);
        }
    }
}
