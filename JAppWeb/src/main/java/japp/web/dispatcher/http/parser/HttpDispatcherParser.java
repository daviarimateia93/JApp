package japp.web.dispatcher.http.parser;

public interface HttpDispatcherParser {
	
	public String[] getContentTypes();
	
	public Object parseIncoming(final byte[] bytes, final Class<?> objectClass);
	
	public byte[] parseOutgoing(final Object object);
}
