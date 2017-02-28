package japp.web.dispatcher.http.parser;

import japp.util.ByteHelper;
import japp.web.exception.HttpException;

public class TextHttpDispatcherParser implements HttpDispatcherParser {
	
	@Override
	public String[] getContentTypes() {
		return new String[] { "*/*", "text/plain", "text/html", "text/csv" };
	}
	
	@Override
	public Object parseIncoming(final byte[] bytes, final Class<?> objectClass) {
		throw new HttpException(500, "Method not allowed");
	}
	
	@Override
	public byte[] parseOutgoing(final Object object) {
		if (object == null) {
			return null;
		} else if (object instanceof byte[]) {
			return (byte[]) object;
		} else {
			return ByteHelper.toBytes(object.toString());
		}
	}
}
