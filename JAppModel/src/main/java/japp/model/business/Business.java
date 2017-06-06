package japp.model.business;

import javax.persistence.EntityManager;

import japp.model.ModelApp;
import japp.model.jpa.repository.Repository;
import japp.model.jpa.repository.RepositoryFactory;

public abstract class Business {
	
	private final BusinessFactory businessFactory;
	private final RepositoryFactory repositoryFactory;
	private final EntityManager entityManager;
	
	protected Business() {
		this((EntityManager) null);
	}
	
	protected Business(final EntityManager entityManager) {
		this(ModelApp.getModelAppConfiguration().getBusinessFactory(), ModelApp.getModelAppConfiguration().getRepositoryFactory(), entityManager);
	}
	
	protected Business(final BusinessFactory businessFactory, final RepositoryFactory repositoryFactory, final EntityManager entityManager) {
		this.businessFactory = businessFactory;
		this.repositoryFactory = repositoryFactory;
		this.entityManager = entityManager;
	}
	
	protected BusinessFactory getBusinessFactory() {
		return businessFactory;
	}
	
	protected RepositoryFactory getRepositoryFactory() {
		return repositoryFactory;
	}
	
	protected EntityManager getEntityManager() {
		return entityManager;
	}
	
	protected <T extends Business> T getBusiness(final Class<T> businessClass) {
		return businessFactory.getBusiness(businessClass, entityManager);
	}
	
	protected <T extends Repository<?, ?>> T getRepository(final Class<T> repositoryClass) {
		return repositoryFactory.getRepository(repositoryClass, entityManager);
	}
}
