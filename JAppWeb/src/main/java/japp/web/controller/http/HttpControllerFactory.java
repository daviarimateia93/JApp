package japp.web.controller.http;

import japp.util.Reference;

public interface HttpControllerFactory {

    public <T extends HttpController> Reference<T> getHttpController(final Class<T> httpControllerClass);
}
