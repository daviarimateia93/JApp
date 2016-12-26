package japp.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public abstract class ByteHelper {
	
	private static final String CHARSET_UTF_8 = "UTF-8";
	
	protected ByteHelper() {
		
	}
	
	public static byte[] toBytes(final String string) {
		try {
			return string.getBytes(CHARSET_UTF_8);
		} catch (final UnsupportedEncodingException exception) {
			return null;
		}
	}
	
	public static byte[] toBytes(final InputStream inputStream) {
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		
		try {
			int read;
			
			while ((read = inputStream.read()) != -1) {
				byteArrayOutputStream.write(read);
			}
			
			return byteArrayOutputStream.toByteArray();
		} catch (final IOException exception) {
			return null;
		} finally {
			try {
				byteArrayOutputStream.close();
			} catch (final IOException exception) {
				
			}
		}
	}
}
