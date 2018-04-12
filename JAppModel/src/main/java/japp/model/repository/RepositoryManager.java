package japp.model.repository;

import java.util.Map;
import java.util.concurrent.Callable;

import javax.persistence.EntityManager;

public interface RepositoryManager {

    public EntityManager getEntityManager(final String persistenceUnitName);

    public EntityManager getEntityManager(final String persistenceUnitName, final Map<?, ?> persistenceProperties);

    public void closeEntityManager();

    public void closeEntityManagerFactory();

    public <T> T executeInCurrentOrNewTransaction(final EntityManager entityManager, final Callable<T> callable);

    public void executeInCurrentOrNewTransaction(final EntityManager entityManager, final Runnable runnable);

    public <T> T executeInNewTransaction(final EntityManager entityManager, final Callable<T> callable);

    public void executeInNewTransaction(final EntityManager entityManager, final Runnable runnable);

    public <T> T executeInCurrentTransaction(final Callable<T> callable);

    public void executeInCurrentTransaction(final Runnable runnable);
}
