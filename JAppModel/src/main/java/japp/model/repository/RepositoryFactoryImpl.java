package japp.model.repository;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import japp.util.JAppRuntimeException;
import japp.util.ReflectionHelper;
import japp.util.SingletonFactory;
import japp.util.Singletonable;

public class RepositoryFactoryImpl implements Singletonable, RepositoryFactory {
	
	private static final Map<String, EntityManagerFactory> entityManagerFactories = new HashMap<>();
	private static final ThreadLocal<Map<String, EntityManager>> entityManagers = new ThreadLocal<>();
	
	public static synchronized RepositoryFactoryImpl getInstance() {
		return SingletonFactory.getInstance(RepositoryFactoryImpl.class);
	}
	
	protected RepositoryFactoryImpl() {
		
	}
	
	protected static Map<String, EntityManagerFactory> getEntityManagerFactories() {
		return entityManagerFactories;
	}
	
	protected static ThreadLocal<Map<String, EntityManager>> getEntityManagers() {
		return entityManagers;
	}
	
	@Override
	public synchronized EntityManager getEntityManager(final String persistenceUnitName) {
		return getEntityManager(persistenceUnitName, null);
	}
	
	@Override
	public synchronized EntityManager getEntityManager(final String persistenceUnitName, final Map<?, ?> persistenceProperties) {
		final String persistencePropertiesHash = getEntityManagerHash(persistenceUnitName, persistenceProperties);
		
		if (!entityManagerFactories.containsKey(persistencePropertiesHash)) {
			entityManagerFactories.put(persistencePropertiesHash, Persistence.createEntityManagerFactory(persistenceUnitName, persistenceProperties));
		}
		
		if (entityManagers.get() == null) {
			entityManagers.set(new HashMap<>());
		}
		
		if (!entityManagers.get().containsKey(persistencePropertiesHash)) {
			entityManagers.get().put(persistencePropertiesHash, entityManagerFactories.get(persistencePropertiesHash).createEntityManager());
		}
		
		final EntityManager entityManager = entityManagers.get().get(persistencePropertiesHash);
		
		return entityManager;
	}
	
	@Override
	public synchronized void closeEntityManager() {
		if (entityManagers.get() != null) {
			for (final Map.Entry<String, EntityManager> entry : entityManagers.get().entrySet()) {
				final EntityManager entityManager = entry.getValue();
				
				if (entityManager.isOpen()) {
					entityManager.close();
				}
			}
		}
	}
	
	@Override
	public synchronized void closeEntityManagerFactory() {
		for (Map.Entry<String, EntityManagerFactory> entry : entityManagerFactories.entrySet()) {
			final EntityManagerFactory entityManagerFactory = entry.getValue();
			
			if (entityManagerFactory.isOpen()) {
				entityManagerFactory.close();
			}
		}
	}
	
	@Override
	public <T> T executeInCurrentOrNewTransaction(final EntityManager entityManager, final Callable<T> callable) {
		if (entityManager.getTransaction().isActive()) {
			return executeInCurrentTransaction(callable);
		} else {
			return executeInNewTransaction(entityManager, callable);
		}
	}
	
	@Override
	public void executeInCurrentOrNewTransaction(final EntityManager entityManager, final Runnable runnable) {
		if (entityManager.getTransaction().isActive()) {
			executeInCurrentTransaction(runnable);
		} else {
			executeInNewTransaction(entityManager, runnable);
		}
	}
	
	@Override
	public <T> T executeInNewTransaction(final EntityManager entityManager, final Callable<T> callable) {
		try {
			entityManager.getTransaction().begin();
			
			final T value = callable.call();
			
			entityManager.getTransaction().commit();
			
			return value;
		} catch (final Exception exception) {
			entityManager.getTransaction().rollback();
			
			throw new JAppRuntimeException(exception);
		}
	}
	
	@Override
	public void executeInNewTransaction(final EntityManager entityManager, final Runnable runnable) {
		try {
			entityManager.getTransaction().begin();
			
			runnable.run();
			
			entityManager.getTransaction().commit();
		} catch (final Exception exception) {
			entityManager.getTransaction().rollback();
			
			throw new JAppRuntimeException(exception);
		}
	}
	
	@Override
	public <T> T executeInCurrentTransaction(final Callable<T> callable) {
		try {
			return callable.call();
		} catch (final Exception exception) {
			throw new JAppRuntimeException(exception);
		}
	}
	
	@Override
	public void executeInCurrentTransaction(final Runnable runnable) {
		runnable.run();
	}
	
	@Override
	public <T extends Repository<?, ?>> T getRepository(final Class<T> repositoryClass, final String persistenceUnitName, final Map<?, ?> persistenceProperties) {
		return getRepository(repositoryClass, getEntityManager(persistenceUnitName, persistenceProperties));
	}
	
	@Override
	public <T extends Repository<?, ?>> T getRepository(final Class<T> repositoryClass, final EntityManager entityManager) {
		try {
			return ReflectionHelper.newInstance(repositoryClass, new Class<?>[] { EntityManager.class }, entityManager);
		} catch (final NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
			throw new JAppRuntimeException(exception);
		}
	}
	
	private String getEntityManagerHash(final String persistenceUnitName, final Map<?, ?> persistenceProperties) {
		final StringBuilder stringBuilder = new StringBuilder("persistenceUnitName=" + persistenceUnitName + ";");
		
		if (persistenceProperties != null) {
			for (final Map.Entry<?, ?> entry : persistenceProperties.entrySet()) {
				stringBuilder.append(entry.getKey());
				stringBuilder.append("=");
				stringBuilder.append(entry.getValue());
				stringBuilder.append(";");
			}
		}
		
		return stringBuilder.toString();
	}
}
