package japp.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public abstract class JsonHelper {
	
	private static final String CHARSET_UTF_8 = "UTF-8";
	
	public static String DATE_TIME_FORMAT_PATTERN = DateHelper.DATE_TIME_FORMAT_PATTERN;
	
	protected JsonHelper() {
		
	}
	
	public static ObjectMapper getObjectMapper() {
		return new ObjectMapper().registerModule(new Hibernate5Module()).registerModule(new JavaTimeModule()).setDateFormat(new SimpleDateFormat(DATE_TIME_FORMAT_PATTERN)).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false).configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false);
	}
	
	public static String toString(final Object object) {
		try (final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
			getObjectMapper().writeValue(byteArrayOutputStream, object);
			
			return byteArrayOutputStream.toString(CHARSET_UTF_8);
		} catch (final IOException exception) {
			throw new JAppRuntimeException(exception);
		}
	}
	
	public static <T> T toObject(final String string, final Class<T> type) {
		if (StringHelper.isNullOrBlank(string)) {
			return null;
		}
		
		try {
			return getObjectMapper().readValue(string, type);
		} catch (final IOException exception) {
			throw new JAppRuntimeException(exception);
		}
	}
}
