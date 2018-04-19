package japp.model.repository;

import javax.persistence.EntityManager;

import japp.util.Reference;
import japp.util.SingletonFactory;
import japp.util.Singletonable;

public class RepositoryFactoryImpl implements Singletonable, RepositoryFactory {

    public static synchronized RepositoryFactoryImpl getInstance() {
        return SingletonFactory.getInstance(RepositoryFactoryImpl.class).get();
    }

    protected RepositoryFactoryImpl() {

    }

    @Override
    public <T extends Repository<?, ?>> Reference<T> getRepository(
            final Class<T> repositoryClass,
            final EntityManager entityManager) {

        return SingletonFactory.getInstancePerThread(repositoryClass, new Class<?>[] { EntityManager.class },
                entityManager);
    }
}
