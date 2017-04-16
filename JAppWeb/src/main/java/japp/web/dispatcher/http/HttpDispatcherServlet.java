package japp.web.dispatcher.http;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import japp.model.ModelApp;
import japp.web.WebApp;

public class HttpDispatcherServlet extends HttpServlet implements ServletContextListener {
	
	private static final long serialVersionUID = 8075347103472490046L;
	
	protected static final Map<String, String> configurations = new HashMap<>();
	
	@Override
	public void init() throws ServletException {
		setupConfigurations();
		
		WebApp.getWebAppConfiguration().init();
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		if (ModelApp.getModelAppConfiguration().getRepositoryManager() != null) {
			ModelApp.getModelAppConfiguration().getRepositoryManager().closeEntityManagerFactory();
		}
	}
	
	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
	}
	
	@Override
	protected void doGet(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse) throws ServletException, IOException {
		handle(httpServletRequest, httpServletResponse);
	}
	
	@Override
	protected void doPost(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse) throws ServletException, IOException {
		handle(httpServletRequest, httpServletResponse);
	}
	
	@Override
	protected void doPut(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse) throws ServletException, IOException {
		handle(httpServletRequest, httpServletResponse);
	}
	
	@Override
	protected void doDelete(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse) throws ServletException, IOException {
		handle(httpServletRequest, httpServletResponse);
	}
	
	@Override
	protected void doHead(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse) throws ServletException, IOException {
		handle(httpServletRequest, httpServletResponse);
	}
	
	@Override
	protected void doOptions(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse) throws ServletException, IOException {
		handle(httpServletRequest, httpServletResponse);
	}
	
	@Override
	protected void doTrace(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse) throws ServletException, IOException {
		handle(httpServletRequest, httpServletResponse);
	}
	
	protected void handle(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse) throws ServletException, IOException {
		if (WebApp.getWebAppConfiguration().getHttpDispatcher() != null) {
			WebApp.getWebAppConfiguration().getHttpDispatcher().dispatch(httpServletRequest, httpServletResponse);
		}
	}
	
	protected void setupConfigurations() {
		final Enumeration<String> initParameterNames = getServletConfig().getInitParameterNames();
		
		while (initParameterNames.hasMoreElements()) {
			final String parameterName = (String) initParameterNames.nextElement();
			
			configurations.put(parameterName, getServletConfig().getInitParameter(parameterName));
		}
	}
	
	public static String getConfiguration(final String configuration) {
		return configurations.get(configuration);
	}
}
