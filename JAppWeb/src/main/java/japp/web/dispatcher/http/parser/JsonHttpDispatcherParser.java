package japp.web.dispatcher.http.parser;

import japp.util.ByteHelper;
import japp.util.JsonHelper;
import japp.util.StringHelper;

public class JsonHttpDispatcherParser implements HttpDispatcherParser {

    @Override
    public String[] getContentTypes() {
        return new String[] { "application/json", "text/json" };
    }

    @Override
    public Object parseIncoming(final byte[] bytes, final Class<?> objectClass) {
        return JsonHelper.toObject(StringHelper.toString(bytes), objectClass);
    }

    @Override
    public byte[] parseOutgoing(final Object object) {
        return ByteHelper.toBytes(JsonHelper.toString(object));
    }
}
