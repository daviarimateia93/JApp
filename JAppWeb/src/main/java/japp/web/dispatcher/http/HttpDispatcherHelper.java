package japp.web.dispatcher.http;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public abstract class HttpDispatcherHelper {

    private static final Logger logger = Logger.getLogger(HttpDispatcherHelper.class);

    protected HttpDispatcherHelper() {

    }

    public static String getUriWithoutContextPath(final HttpServletRequest httpServletRequest) {
        return httpServletRequest.getRequestURI().substring(httpServletRequest.getContextPath().length());
    }

    public static boolean containsContentType(final String[] sourceContentTypes, final String... contentTypes) {
        return Arrays.stream(contentTypes)
                .map(ct -> ct.split(";")[0])
                .anyMatch(ct -> ct.equals("*/*") || containsContentType(sourceContentTypes, ct));
    }

    public static boolean containsContentType(final String[] sourceContentTypes, final String contentType) {
        return Arrays.stream(sourceContentTypes)
                .anyMatch(sct -> sct.equalsIgnoreCase(contentType));
    }

    public static void httpServletResponseWrite(
            final HttpServletResponse httpServletResponse,
            final int httpStatusCode,
            final String contentType,
            final byte[] content) {

        try {
            httpServletResponse.setStatus(httpStatusCode);
            httpServletResponse.setContentType(contentType);
            httpServletResponse.setContentLength(content.length);
            httpServletResponse.getOutputStream().write(content);
        } catch (final IOException exception) {
            logger.error(exception);
        }
    }
}
