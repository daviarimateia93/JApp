package japp.model.service;

import javax.persistence.EntityManager;

import japp.util.Reference;
import japp.util.SingletonFactory;
import japp.util.Singletonable;

public class ServiceFactoryImpl implements Singletonable, ServiceFactory {

    public static synchronized ServiceFactoryImpl getInstance() {
        return SingletonFactory.getInstance(ServiceFactoryImpl.class).get();
    }

    protected ServiceFactoryImpl() {

    }

    @Override
    public <T extends Service> Reference<T> getService(final Class<T> serviceClass) {
        return SingletonFactory.getProxyInterceptableInstancePerThread(serviceClass);
    }

    @Override
    public <T extends Service> Reference<T> getService(final Class<T> serviceClass, final EntityManager entityManager) {
        return SingletonFactory.getProxyInterceptableInstancePerThread(
                serviceClass,
                new Class<?>[] { EntityManager.class },
                entityManager);
    }
}
