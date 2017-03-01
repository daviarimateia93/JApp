package japp.web.dispatcher.http;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class HttpDispatcherHelper {
	
	protected HttpDispatcherHelper() {
		
	}
	
	public static String getUriWithoutContextPath(final HttpServletRequest httpServletRequest) {
		return httpServletRequest.getRequestURI().substring(httpServletRequest.getContextPath().length());
	}
	
	public static boolean containsContentType(final String[] sourceContentTypes, final String... contentTypes) {
		if (contentTypes != null && contentTypes.length > 0) {
			for (String contentType : contentTypes) {
				contentType = contentType.split(";")[0];
				
				if (contentType.equals("*/*")) {
					return true;
				}
				
				if (containsContentType(sourceContentTypes, contentType)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public static boolean containsContentType(final String[] sourceContentTypes, final String contentType) {
		if (sourceContentTypes != null && sourceContentTypes.length > 0) {
			for (final String sourceContentType : sourceContentTypes) {
				if (sourceContentType.equalsIgnoreCase(contentType)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public static void httpServletResponseWrite(final HttpServletResponse httpServletResponse, final int httpStatusCode, final String contentType, final byte[] content) {
		try {
			httpServletResponse.setStatus(httpStatusCode);
			httpServletResponse.setContentType(contentType);
			httpServletResponse.setContentLength(content.length);
			httpServletResponse.getOutputStream().write(content);
		} catch (final IOException exception) {
			exception.printStackTrace();
		}
	}
}
