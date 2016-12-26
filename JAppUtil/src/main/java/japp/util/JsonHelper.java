package japp.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;

public abstract class JsonHelper {
	
	private static final String CHARSET_UTF_8 = "UTF-8";
	
	public static String DATE_TIME_FORMAT_PATTERN = DateHelper.DATE_TIME_FORMAT_PATTERN;
	
	protected JsonHelper() {
		
	}
	
	public static ObjectMapper getObjectMapper() {
		return new ObjectMapper().setDateFormat(new SimpleDateFormat(DATE_TIME_FORMAT_PATTERN)).registerModule(new Hibernate5Module()).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
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
		try {
			return getObjectMapper().readValue(string, type);
		} catch (final IOException exception) {
			exception.printStackTrace();
			throw new JAppRuntimeException(exception);
		}
	}
}
