package japp.web.dispatcher.http.parser;

import java.util.Map;

import japp.util.FormDataHelper;
import japp.util.JsonHelper;
import japp.util.StringHelper;
import japp.web.exception.HttpException;

public class FormDataDispatcherParser implements HttpDispatcherParser {

    @Override
    public String[] getContentTypes() {
        return new String[] { "application/x-www-form-urlencoded" };
    }

    @Override
    public Object parseIncoming(final byte[] bytes, final Class<?> objectClass) {
        final Map<String, Object> formData = FormDataHelper.parse(StringHelper.toString(bytes));

        return JsonHelper.toObject(JsonHelper.toString(formData), objectClass);
    }

    @Override
    public byte[] parseOutgoing(final Object object) {
        throw new HttpException(500, "Method not allowed");
    }
}
