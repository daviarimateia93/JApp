package japp.model.repository;

import javax.persistence.EntityManager;

import japp.util.Reference;

public interface RepositoryFactory {
	
	public <T extends Repository<?, ?>> Reference<T> getRepository(final Class<T> repositoryClass, final EntityManager entityManager);
}
