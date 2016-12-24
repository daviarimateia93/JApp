package japp.web.dispatcher.http.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import japp.util.Setable;
import japp.util.SingletonFactory;
import japp.util.Singletonable;
import japp.web.dispatcher.http.HttpDispatcherHelper;
import japp.web.exception.HttpException;

public class HttpDispatcherParserManagerImpl implements Singletonable, HttpDispatcherParserManager {
	
	protected final Map<String, HttpDispatcherParser> httpDispatcherParsers;
	protected HttpDispatcherParser defaultOutgoingHttpDispatcherParser;
	
	public static synchronized HttpDispatcherParserManagerImpl getInstance() {
		return SingletonFactory.getInstance(HttpDispatcherParserManagerImpl.class);
	}
	
	protected HttpDispatcherParserManagerImpl() {
		this.httpDispatcherParsers = new HashMap<>();
	}
	
	@Override
	public void setDefaultOutgoingHttpDispatcherParser(final HttpDispatcherParser httpDispatcherParser) {
		this.defaultOutgoingHttpDispatcherParser = httpDispatcherParser;
	}
	
	@Override
	public void addHttpDispatcherParser(final HttpDispatcherParser httpDispatcherParser) {
		removeHttpDispatcherParser(httpDispatcherParser.getContentTypes());
		
		for (final String contentType : httpDispatcherParser.getContentTypes()) {
			httpDispatcherParsers.put(contentType, httpDispatcherParser);
		}
	}
	
	@Override
	public void removeHttpDispatcherParser(final String... contentTypes) {
		if (contentTypes != null) {
			for (final String contentType : contentTypes) {
				if (containsHttpDispatcherParser(contentType)) {
					httpDispatcherParsers.remove(contentType);
				}
			}
		}
	}
	
	@Override
	public void clearHttpDispatcherParsers() {
		httpDispatcherParsers.clear();
	}
	
	@Override
	public boolean containsHttpDispatcherParser(final String contentType) {
		return getHttpDispatcherParser(contentType) != null;
	}
	
	@Override
	public HttpDispatcherParser getHttpDispatcherParser(final String... contentTypes) {
		if (contentTypes != null && contentTypes.length > 0) {
			for (String contentType : contentTypes) {
				if (contentType != null) {
					final Matcher matcher = Pattern.compile("^(.*?)[; ].*$").matcher(contentType);
					contentType = (matcher.find() ? matcher.groupCount() == 1 ? matcher.group(0) : matcher.group(1) : contentType);
					
					for (final Map.Entry<String, HttpDispatcherParser> entry : httpDispatcherParsers.entrySet()) {
						if (entry.getKey().trim().equalsIgnoreCase(contentType.trim())) {
							return entry.getValue();
						}
					}
				}
			}
		}
		
		return null;
	}
	
	@Override
	public Object parseIncoming(final Setable<String> contentType, final byte[] bytes, final Class<?> objectClass) {
		final HttpDispatcherParser httpDispatcherParser = getHttpDispatcherParser(contentType.getValue());
		
		if (httpDispatcherParser == null) {
			throw new HttpException(500, String.format("No parser for %s", contentType));
		}
		
		return httpDispatcherParser.parseIncoming(bytes, objectClass);
	}
	
	@Override
	public byte[] parseOutgoing(final Setable<String> contentType, final boolean acceptContentType, final Object object) {
		final String[] contentTypes = contentType.getValue().split("\\,");
		final HttpDispatcherParser httpDispatcherParser = getHttpDispatcherParser(contentTypes) != null ? getHttpDispatcherParser(contentTypes) : defaultOutgoingHttpDispatcherParser;
		
		if (httpDispatcherParser == null) {
			throw new HttpException(404, String.format("No defaultOutgoingDispatcherParser setted"));
		}
		
		if (acceptContentType) {
			if (!HttpDispatcherHelper.containsContentType(httpDispatcherParser.getContentTypes(), contentTypes)) {
				throw new HttpException(404, String.format("No parser for: %s", contentType.getValue()));
			}
		}
		
		return httpDispatcherParser.parseOutgoing(object);
	}
}
