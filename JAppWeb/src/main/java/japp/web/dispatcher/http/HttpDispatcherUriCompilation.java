package japp.web.dispatcher.http;

import java.util.Map;

import japp.web.dispatcher.http.request.RequestMapping;
import japp.web.uri.UriCompilation;

public class HttpDispatcherUriCompilation extends UriCompilation {

    private final RequestMapping requestMapping;

    public HttpDispatcherUriCompilation(final RequestMapping requestMapping, final Float score, final Boolean valid,
            final String pattern, final String compiledPattern, final Map<String, String> variables) {
        super(score, valid, pattern, compiledPattern, variables);

        this.requestMapping = requestMapping;
    }

    public HttpDispatcherUriCompilation(final RequestMapping requestMapping, final UriCompilation uriCompilation) {
        super(uriCompilation);

        this.requestMapping = requestMapping;
    }

    public RequestMapping getRequestMapping() {
        return requestMapping;
    }
}
