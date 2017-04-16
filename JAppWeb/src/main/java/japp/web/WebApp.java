package japp.web;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import japp.model.ModelApp;
import japp.model.service.Service;
import japp.util.JAppRuntimeException;

public abstract class WebApp {
	
	private static WebAppConfiguration webAppConfiguration;
	
	public static WebAppConfiguration getWebAppConfiguration() {
		if (webAppConfiguration == null) {
			throw new JAppRuntimeException("WebApp.webAppConfiguration is not setted yet");
		}
		
		return webAppConfiguration;
	}
	
	public static void setWebAppConfiguration(final WebAppConfiguration webAppConfiguration) {
		if (WebApp.webAppConfiguration != null) {
			throw new JAppRuntimeException("WebApp.webAppConfiguration is setted already");
		}
		
		WebApp.webAppConfiguration = webAppConfiguration;
	}
	
	public static <T extends Service> T getService(final Class<T> serviceClass) {
		return ModelApp.getModelAppConfiguration().getServiceFactory().getService(serviceClass);
	}
	
	public static <T extends Service> T getPersistenceService(final Class<T> serviceClass, final EntityManager entityManager) {
		return ModelApp.getModelAppConfiguration().getServiceFactory().getService(serviceClass, entityManager);
	}
	
	public static <T extends Service> T getPersistenceService(final Class<T> serviceClass) {
		return getPersistenceService(serviceClass, (HttpServletRequest) null);
	}
	
	public static <T extends Service> T getPersistenceService(final Class<T> serviceClass, final HttpServletRequest httpServletRequest) {
		return getPersistenceService(serviceClass, ModelApp.getModelAppConfiguration().getRepositoryManager().getEntityManager(WebApp.getWebAppConfiguration().getPersistenceUnitName(httpServletRequest), WebApp.getWebAppConfiguration().getPersistenceProperties(httpServletRequest)));
	}
}
