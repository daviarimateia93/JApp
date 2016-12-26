package japp.util;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.MaskFormatter;

public abstract class StringHelper {
	
	private static final String CHARSET_UTF_8 = "UTF-8";
	private static final String ALGORITHM_SHA_512 = "SHA-512";
	private static final String PATTERN_NON_NUMERIC_ALL = "[^0-9]+";
	private static final String EMPTY = "";
	private static final String LT = "<";
	private static final String GT = ">";
	private static final String AMP = "&";
	private static final String QUOTE = "\"";
	private static final String SINGLE_QUOTE = "'";
	private static final String EQUALS = "=";
	private static final String HTML_CODE_LT = "&lt;";
	private static final String HTML_CODE_GT = "&gt;";
	private static final String HTML_CODE_AMP = "&amp;;";
	private static final String HTML_CODE_QUOTE = "&quot;";
	private static final String HTML_CODE_SINGLE_QUOTE = "&#039;";
	private static final String HTML_CODE_EQUALS = "&#61;";
	
	protected StringHelper() {
		
	}
	
	public static String join(final String[] strings, final String glue) {
		return join(strings, glue, null);
	}
	
	public static String join(final String[] strings, final String glue, final String endGlue) {
		final StringBuilder stringBuilder = new StringBuilder();
		
		for (int i = 0; i < strings.length; i++) {
			if (i > 0) {
				stringBuilder.append(glue);
			}
			
			stringBuilder.append(strings[i]);
		}
		
		if (endGlue != null) {
			stringBuilder.append(endGlue);
		}
		
		return stringBuilder.toString();
		
	}
	
	public static String toString(final byte[] bytes) {
		try {
			return new String(bytes, CHARSET_UTF_8);
		} catch (final UnsupportedEncodingException exception) {
			return null;
		}
	}
	
	public static String base64Encode(final byte[] bytes) {
		return toString(Base64Helper.encode(bytes));
	}
	
	public static String base64Decode(final byte[] bytes) {
		return toString(Base64Helper.encode(bytes));
	}
	
	public static String base64Encode(final String string) {
		return base64Encode(ByteHelper.toBytes(string));
	}
	
	public static String base64Decode(final String string) {
		return base64Decode(ByteHelper.toBytes(string));
	}
	
	public static String base64Encode(final InputStream inputStream) {
		return base64Encode(ByteHelper.toBytes(inputStream));
	}
	
	public static String base64Decode(final InputStream inputStream) {
		return base64Decode(ByteHelper.toBytes(inputStream));
	}
	
	public static String urlEncode(final String string) {
		try {
			return URLEncoder.encode(string, CHARSET_UTF_8);
		} catch (final UnsupportedEncodingException exception) {
			return null;
		}
	}
	
	public static String urlDecode(final String string) {
		try {
			return URLDecoder.decode(string, CHARSET_UTF_8);
		} catch (final UnsupportedEncodingException exception) {
			return null;
		}
	}
	
	public static String shuffle(final String string) {
		final List<Character> characters = new ArrayList<>();
		
		for (final char character : string.toCharArray()) {
			characters.add(character);
		}
		
		final StringBuilder output = new StringBuilder(string.length());
		
		while (!characters.isEmpty()) {
			output.append(characters.remove((int) (Math.random() * characters.size())));
		}
		
		return output.toString();
	}
	
	public static String hex(final byte[] stringBytes) {
		final StringBuilder output = new StringBuilder();
		
		for (int i = 0; i < stringBytes.length; i++) {
			output.append(Integer.toString((stringBytes[i] & 0xff) + 0x100, 16).substring(1));
		}
		
		return output.toString();
	}
	
	public static String encryptSHA512AndHex(final String string) {
		try {
			final MessageDigest sha512 = MessageDigest.getInstance(ALGORITHM_SHA_512);
			
			return StringHelper.hex(sha512.digest(string.getBytes()));
		} catch (final NoSuchAlgorithmException exception) {
			return null;
		}
	}
	
	public static String format(final String string, final String pattern) {
		try {
			final MaskFormatter mask = new MaskFormatter(pattern);
			mask.setValueContainsLiteralCharacters(false);
			
			return mask.valueToString(string);
		} catch (final Exception exception) {
			return string;
		}
	}
	
	public static String fixToHtml(final String string) {
		return string.trim().replace(LT, HTML_CODE_LT).replace(GT, HTML_CODE_GT).replace(AMP, HTML_CODE_AMP).replace(QUOTE, HTML_CODE_QUOTE).replace(SINGLE_QUOTE, HTML_CODE_SINGLE_QUOTE).replace(EQUALS, HTML_CODE_EQUALS);
	}
	
	public static boolean isBlank(final String string) {
		return string.trim().isEmpty();
	}
	
	public static boolean isEmpty(final String string) {
		return string.isEmpty();
	}
	
	public static boolean isNullOrBlank(final String string) {
		return string == null ? true : isBlank(string);
	}
	
	public static boolean isNullOrEmpty(final String string) {
		return string == null ? true : isEmpty(string);
	}
	
	public static String onlyNumbers(final String string) {
		return string.replaceAll(PATTERN_NON_NUMERIC_ALL, EMPTY);
	}
}
