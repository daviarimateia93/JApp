package japp.util;

import java.io.IOException;
import java.util.Properties;

public abstract class PropertiesHelper {

    protected PropertiesHelper() {

    }

    public static Properties load(final String path) {
        return load(path, System.getProperty("profile"));
    }

    public static Properties load(final String path, final String profile) {
        final String suffix = StringHelper.isNullOrBlank(profile) ? "" : "-" + profile;
        Properties properties = new Properties();

        try {
            properties.load(
                    Thread.currentThread().getContextClassLoader().getResourceAsStream(path + suffix + ".properties"));
        } catch (final IOException exception) {
            throw new JAppRuntimeException(exception);
        }

        return properties;
    }

    public static String get(final Properties properties, final String key) {
        return get(properties, key, String.class);
    }

    public static String get(final Properties properties, final String key, final String defaultValue) {
        return get(properties, key, defaultValue, String.class);
    }

    public static <T> T get(final Properties properties, final String key, final Class<T> type) {
        return get(properties, key, null, type);
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(final Properties properties, final String key, final String defaultValue,
            final Class<T> type) {
        return (T) ReflectionHelper.generateBasicValue(properties.getProperty(key, defaultValue), type);
    }
}
