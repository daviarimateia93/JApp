package japp.model.service;

import javax.persistence.EntityManager;

public interface ServiceFactory {
	
	public <T extends Service> T getService(final Class<T> serviceClass);
	
	public <T extends Service> T getService(final Class<T> serviceClass, final EntityManager entityManager);
}
