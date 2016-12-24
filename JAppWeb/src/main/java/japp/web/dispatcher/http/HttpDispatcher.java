package japp.web.dispatcher.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import japp.web.controller.http.HttpController;

public interface HttpDispatcher {
	
	public void dispatch(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse);
	
	public void handleUncaughtException(final Exception uncaughtException, final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse);
	
	public void clearRequestMappings();
	
	public <T extends HttpController> void register(final Class<T> httpControllerClass);
}
