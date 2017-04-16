package japp.model.repository;

import javax.persistence.EntityManager;

public interface RepositoryFactory {
	
	public <T extends Repository<?, ?>> T getRepository(final Class<T> repositoryClass, final EntityManager entityManager);
}
