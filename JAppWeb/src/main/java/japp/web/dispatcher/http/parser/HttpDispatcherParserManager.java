package japp.web.dispatcher.http.parser;

public interface HttpDispatcherParserManager {

    public void setDefaultOutgoingHttpDispatcherParser(final HttpDispatcherParser httpDispatcherParser);

    public void addHttpDispatcherParser(final HttpDispatcherParser httpDispatcherParser);

    public void removeHttpDispatcherParser(final String... contentTypes);

    public void clearHttpDispatcherParsers();

    public boolean containsHttpDispatcherParser(final String contentType);

    public HttpDispatcherParser getHttpDispatcherParser(final String... contentTypes);

    public Object parseIncoming(final String contentType, final byte[] bytes, final Class<?> objectClass);

    public byte[] parseOutgoing(final String contentType, final boolean acceptContentType, final Object object);
}
