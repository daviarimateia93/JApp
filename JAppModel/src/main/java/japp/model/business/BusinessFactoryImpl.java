package japp.model.business;

import java.lang.reflect.InvocationTargetException;

import javax.persistence.EntityManager;

import japp.util.JAppRuntimeException;
import japp.util.ReflectionHelper;
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
		try {
			return ReflectionHelper.newInstance(businessClass);
		} catch (final NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
			throw new JAppRuntimeException(exception);
		}
	}
	
	@Override
	public <T extends Business> T getBusiness(final Class<T> businessClass, final EntityManager entityManager) {
		try {
			return ReflectionHelper.newInstance(businessClass, new Class<?>[] { EntityManager.class }, entityManager);
		} catch (final NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
			throw new JAppRuntimeException(exception);
		}
	}
}
