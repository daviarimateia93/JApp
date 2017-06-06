package japp.model.jpa.repository;

import java.lang.reflect.InvocationTargetException;

import javax.persistence.EntityManager;

import japp.util.JAppRuntimeException;
import japp.util.ReflectionHelper;
import japp.util.SingletonFactory;
import japp.util.Singletonable;

public class RepositoryFactoryImpl implements Singletonable, RepositoryFactory {
	
	public static synchronized RepositoryFactoryImpl getInstance() {
		return SingletonFactory.getInstance(RepositoryFactoryImpl.class);
	}
	
	protected RepositoryFactoryImpl() {
		
	}
	
	@Override
	public <T extends Repository<?, ?>> T getRepository(final Class<T> repositoryClass, final EntityManager entityManager) {
		try {
			return ReflectionHelper.newInstance(repositoryClass, new Class<?>[] { EntityManager.class }, entityManager);
		} catch (final NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
			throw new JAppRuntimeException(exception);
		}
	}
}
