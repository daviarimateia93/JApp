package japp.web.dispatcher.http.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import japp.web.dispatcher.http.HttpDispatcherUriCompilation;

public interface HttpDispatcherHandler {

    public void handle(final HttpDispatcherUriCompilation httpDispatcherUriCompilation,
            final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse);
}
