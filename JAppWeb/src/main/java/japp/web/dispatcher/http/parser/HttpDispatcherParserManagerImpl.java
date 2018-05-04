package japp.web.dispatcher.http.parser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import japp.util.SingletonFactory;
import japp.util.Singletonable;
import japp.web.dispatcher.http.HttpDispatcherHelper;
import japp.web.exception.HttpException;

public class HttpDispatcherParserManagerImpl implements Singletonable, HttpDispatcherParserManager {

    protected final Map<String, HttpDispatcherParser> httpDispatcherParsers;
    protected HttpDispatcherParser defaultOutgoingHttpDispatcherParser;

    public static synchronized HttpDispatcherParserManagerImpl getInstance() {
        return SingletonFactory.getInstance(HttpDispatcherParserManagerImpl.class).get();
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

        Arrays.stream(httpDispatcherParser.getContentTypes())
                .forEach(ct -> httpDispatcherParsers.put(ct, httpDispatcherParser));
    }

    @Override
    public void removeHttpDispatcherParser(final String... contentTypes) {
        Arrays.stream(contentTypes)
                .filter(this::containsHttpDispatcherParser)
                .forEach(ct -> httpDispatcherParsers.remove(ct));
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
    public Optional<HttpDispatcherParser> getHttpDispatcherParser(final String... contentTypes) {
        return Arrays.stream(contentTypes)
                .map(ct -> ct.split(";")[0])
                .map(ct -> {
                    final Matcher matcher = Pattern.compile("^(.*?)[; ].*$").matcher(ct);

                    final String contentType = matcher.find()
                            ? matcher.groupCount() == 1
                                    ? matcher.group(0)
                                    : matcher.group(1)
                            : ct;

                    return httpDispatcherParsers
                            .entrySet()
                            .stream()
                            .filter(e -> e.getKey().trim().equalsIgnoreCase(contentType.trim()))
                            .findAny()
                            .map(Map.Entry::getValue);
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findAny();
    }

    @Override
    public Object parseIncoming(final String contentType, final byte[] bytes, final Class<?> objectClass) {
        return getHttpDispatcherParser(contentType)
                .orElseThrow(() -> new HttpException(500, String.format("No parser for %s", contentType)))
                .parseIncoming(bytes, objectClass);
    }

    @Override
    public byte[] parseOutgoing(final String contentType, final boolean acceptContentType, final Object object) {
        final String[] contentTypes = contentType.split("\\,");

        final HttpDispatcherParser httpDispatcherParser = Optional.ofNullable(getHttpDispatcherParser(contentTypes)
                .orElse(defaultOutgoingHttpDispatcherParser))
                .orElseThrow(() -> new HttpException(404, "No defaultOutgoingDispatcherParser setted"));

        if (acceptContentType
                && !HttpDispatcherHelper.containsContentType(httpDispatcherParser.getContentTypes(), contentTypes)) {
            throw new HttpException(404, String.format("No parser for: %s", contentType));
        }

        return httpDispatcherParser.parseOutgoing(object);
    }
}
