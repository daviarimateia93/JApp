package japp.util;

import java.io.IOException;
import java.util.Properties;

public abstract class PropertiesHelper {
	
	protected PropertiesHelper() {
		
	}
	
	public static Properties load(final String path) {
		Properties properties = new Properties();
		
		try {
			properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(path));
		} catch (final IOException exception) {
			throw new JAppRuntimeException(exception);
		}
		
		return properties;
	}
	
	public static <T> T get(final Properties properties, final String key, final Class<T> type) {
		return get(properties, key, type);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T get(final Properties properties, final String key, final String defaultValue, final Class<T> type) {
		return (T) ReflectionHelper.generateBasicValue(properties.getProperty(key, defaultValue), type);
	}
}
