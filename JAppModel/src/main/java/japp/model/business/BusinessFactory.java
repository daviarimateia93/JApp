package japp.model.business;

import javax.persistence.EntityManager;

import japp.util.Reference;

public interface BusinessFactory {

    public <T extends Business> Reference<T> getBusiness(final Class<T> businessClass);

    public <T extends Business> Reference<T> getBusiness(final Class<T> businessClass,
            final EntityManager entityManager);
}
