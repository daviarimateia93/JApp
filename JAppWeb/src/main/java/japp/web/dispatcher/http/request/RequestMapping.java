package japp.web.dispatcher.http.request;

import java.lang.reflect.Method;

import japp.web.controller.http.HttpController;

public class RequestMapping {

    private final HttpController httpController;
    private final Method method;
    private final RequestMethod requestMethod;
    private final String uriPattern;
    private final String[] produces;
    private final String[] consumes;

    public RequestMapping(final HttpController httpController, final Method method, final RequestMethod requestMethod,
            final String uriPattern, final String[] produces, final String[] consumes) {
        this.httpController = httpController;
        this.method = method;
        this.requestMethod = requestMethod;
        this.uriPattern = uriPattern;
        this.produces = produces;
        this.consumes = consumes;
    }

    public HttpController getHttpController() {
        return httpController;
    }

    public Method getMethod() {
        return method;
    }

    public RequestMethod getRequestMethod() {
        return requestMethod;
    }

    public String getUriPattern() {
        return uriPattern;
    }

    public String[] getProduces() {
        return produces;
    }

    public String[] getConsumes() {
        return consumes;
    }
}
