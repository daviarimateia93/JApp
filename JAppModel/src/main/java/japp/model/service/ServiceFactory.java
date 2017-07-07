package japp.model.service;

import javax.persistence.EntityManager;

import japp.util.Reference;

public interface ServiceFactory {
	
	public <T extends Service> Reference<T> getService(final Class<T> serviceClass);
	
	public <T extends Service> Reference<T> getService(final Class<T> serviceClass, final EntityManager entityManager);
}
