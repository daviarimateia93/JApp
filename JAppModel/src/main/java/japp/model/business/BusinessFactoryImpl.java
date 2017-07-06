package japp.model.business;

import javax.persistence.EntityManager;

import japp.util.SingletonFactory;
import japp.util.Singletonable;

public class BusinessFactoryImpl implements Singletonable, BusinessFactory {
	
	public static synchronized BusinessFactoryImpl getInstance() {
		return SingletonFactory.getInstance(BusinessFactoryImpl.class);
	}
	
	protected BusinessFactoryImpl() {
		
	}
	
	@Override
	public <T extends Business> T getBusiness(final Class<T> businessClass) {
		return SingletonFactory.getInstancePerThread(businessClass);
	}
	
	@Override
	public <T extends Business> T getBusiness(final Class<T> businessClass, final EntityManager entityManager) {
		return SingletonFactory.getInstancePerThread(businessClass, new Class<?>[] { EntityManager.class }, entityManager);
	}
}
