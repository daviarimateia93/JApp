package japp.web.dispatcher.http.parser;

import japp.util.Setable;

public interface HttpDispatcherParserManager {
	
	public void setDefaultOutgoingHttpDispatcherParser(final HttpDispatcherParser httpDispatcherParser);
	
	public void addHttpDispatcherParser(final HttpDispatcherParser httpDispatcherParser);
	
	public void removeHttpDispatcherParser(final String... contentTypes);
	
	public void clearHttpDispatcherParsers();
	
	public boolean containsHttpDispatcherParser(final String contentType);
	
	public HttpDispatcherParser getHttpDispatcherParser(final String... contentTypes);
	
	public Object parseIncoming(final Setable<String> contentType, final byte[] bytes, final Class<?> objectClass);
	
	public byte[] parseOutgoing(final Setable<String> contentType, final boolean acceptContentType, final Object object);
}
