package japp.web.dispatcher.http.parser;

import japp.util.ByteHelper;
import japp.web.exception.HttpException;

public class TextPlainHttpDispatcherParser implements HttpDispatcherParser {
	
	@Override
	public String[] getContentTypes() {
		return new String[] { "*/*", "text/plain", "text/html" };
	}
	
	@Override
	public Object parseIncoming(final byte[] bytes, final Class<?> objectClass) {
		throw new HttpException(500, "Method not allowed");
	}
	
	@Override
	public byte[] parseOutgoing(final Object object) {
		return object != null ? ByteHelper.toBytes(object.toString()) : null;
	}
}
