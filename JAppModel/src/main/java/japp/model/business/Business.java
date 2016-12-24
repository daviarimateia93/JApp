package japp.model.business;

import javax.persistence.EntityManager;

import japp.model.ModelApp;
import japp.model.repository.Repository;
import japp.model.repository.RepositoryFactory;

public abstract class Business {
	
	protected final BusinessFactory businessFactory;
	protected final RepositoryFactory repositoryFactory;
	protected final EntityManager entityManager;
	
	public Business() {
		this((EntityManager) null);
	}
	
	public Business(final EntityManager entityManager) {
		this(ModelApp.getModelAppConfiguration().getBusinessFactory(), ModelApp.getModelAppConfiguration().getRepositoryFactory(), entityManager);
	}
	
	protected Business(final BusinessFactory businessFactory, final RepositoryFactory repositoryFactory, final EntityManager entityManager) {
		this.businessFactory = businessFactory;
		this.repositoryFactory = repositoryFactory;
		this.entityManager = entityManager;
	}
	
	protected <T extends Business> T getBusiness(final Class<T> businessClass) {
		return businessFactory.getBusiness(businessClass, entityManager);
	}
	
	protected <T extends Repository<?, ?>> T getRepository(final Class<T> repositoryClass) {
		return repositoryFactory.getRepository(repositoryClass, entityManager);
	}
}
