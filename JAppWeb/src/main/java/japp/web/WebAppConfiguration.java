package japp.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.ServerApplicationConfig;

import japp.web.dispatcher.http.HttpDispatcher;

public interface WebAppConfiguration extends ServerApplicationConfig {
	
	public void init();
	
	public String getAppName();
	
	public String getAppVersion();
	
	public String getViewResolverPrefix();
	
	public String getViewResolverSuffix();
	
	public String getLayoutResolverPrefix();
	
	public String getLayoutResolverSuffix();
	
	public HttpDispatcher getHttpDispatcher();
	
	public String getNonViewDefaultContentType();
	
	public String getPersistenceUnitName(final HttpServletRequest httpServletRequest);
	
	public Map<?, ?> getPersistenceProperties(final HttpServletRequest httpServletRequest);
	
	public boolean isOpenSessionView();
}
