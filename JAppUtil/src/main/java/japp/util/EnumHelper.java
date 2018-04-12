package japp.util;

public abstract class EnumHelper {

    protected EnumHelper() {

    }

    public static <T extends Enum<T>> T safeValueOf(final String string, final Class<T> enumClass) {
        for (final T enumConstant : enumClass.getEnumConstants()) {
            if (enumConstant.name().trim().equalsIgnoreCase(string)) {
                return enumConstant;
            }
        }

        return null;
    }
}
