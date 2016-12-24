package japp.model.business;

import javax.persistence.EntityManager;

public interface BusinessFactory {
	
	public <T extends Business> T getBusiness(final Class<T> businessClass);
	
	public <T extends Business> T getBusiness(final Class<T> businessClass, final EntityManager entityManager);
}
