package japp.model.service;

import java.lang.reflect.Parameter;
import java.util.concurrent.Callable;

import javax.persistence.EntityManager;

import japp.model.ModelApp;
import japp.model.business.Business;
import japp.model.business.BusinessFactory;
import japp.model.repository.RepositoryFactory;
import japp.model.repository.RepositoryManager;
import japp.model.service.authorization.Authorizable;
import japp.model.service.authorization.Authorization;
import japp.model.service.authorization.Authorizer;
import japp.model.service.authorization.ForbiddenException;
import japp.model.service.authorization.Rule;
import japp.model.service.transaction.Transactionable;
import japp.util.ProxyInterceptable;
import japp.util.ProxyMethodWrapper;
import japp.util.Singletonable;
import japp.util.StringHelper;

public abstract class Service implements Singletonable, ProxyInterceptable {
	
	private final BusinessFactory businessFactory;
	private final RepositoryFactory repositoryFactory;
	private final RepositoryManager repositoryManager;
	private final EntityManager entityManager;
	
	protected Service() {
		this((EntityManager) null);
	}
	
	protected Service(final EntityManager entityManager) {
		this(ModelApp.getModelAppConfiguration().getBusinessFactory(), ModelApp.getModelAppConfiguration().getRepositoryFactory(), ModelApp.getModelAppConfiguration().getRepositoryManager(), entityManager);
	}
	
	protected Service(final BusinessFactory businessFactory, final RepositoryFactory repositoryFactory, final RepositoryManager repositoryManager, final EntityManager entityManager) {
		this.businessFactory = businessFactory;
		this.repositoryFactory = repositoryFactory;
		this.repositoryManager = repositoryManager;
		this.entityManager = entityManager;
	}
	
	protected BusinessFactory getBusinessFactory() {
		return businessFactory;
	}
	
	protected RepositoryFactory getRepositoryFactory() {
		return repositoryFactory;
	}
	
	protected RepositoryManager getRepositoryManager() {
		return repositoryManager;
	}
	
	protected EntityManager getEntityManager() {
		return entityManager;
	}
	
	protected <T extends Business> T getBusiness(final Class<T> businessClass) {
		return entityManager == null ? businessFactory.getBusiness(businessClass) : businessFactory.getBusiness(businessClass, entityManager);
	}
	
	protected <T> T executeInCurrentOrNewTransaction(final Callable<T> callable) {
		return repositoryManager.executeInCurrentOrNewTransaction(entityManager, callable);
	}
	
	protected void executeInCurrentOrNewTransaction(final Runnable runnable) {
		repositoryManager.executeInCurrentOrNewTransaction(entityManager, runnable);
	}
	
	protected <T> T executeInNewTransaction(final Callable<T> callable) {
		return repositoryManager.executeInNewTransaction(entityManager, callable);
	}
	
	protected void executeInNewTransaction(final Runnable runnable) {
		repositoryManager.executeInNewTransaction(entityManager, runnable);
	}
	
	protected <T> T executeInCurrentTransaction(final Callable<T> callable) {
		return repositoryManager.executeInCurrentTransaction(callable);
	}
	
	protected void executeInCurrentTransaction(final Runnable runnable) {
		repositoryManager.executeInCurrentTransaction(runnable);
	}
	
	protected boolean authorize(final Authorization authorization, final Rule... rules) {
		return true;
	}
	
	protected Object interceptTransactionable(final Transactionable transactionable, final Authorizable authorizable, final ProxyMethodWrapper proxyMethodWrapper) {
		final Callable<Object> callable = new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				if (authorizable != null) {
					return interceptAuthorizable(authorizable, proxyMethodWrapper);
				} else {
					return proxyMethodWrapper.invoke();
				}
			}
		};
		
		Object value = null;
		
		switch (transactionable.value()) {
			case CURRENT:
				value = executeInCurrentTransaction(callable);
				break;
			
			case CURRENT_OR_NEW:
				value = executeInCurrentOrNewTransaction(callable);
				break;
			
			case NEW: {
				value = executeInNewTransaction(callable);
				break;
			}
		}
		
		return value;
	}
	
	protected Object interceptAuthorizable(final Authorizable authorizable, final ProxyMethodWrapper proxyMethodWrapper) {
		Authorization authorization = null;
		
		for (int i = 0; i < proxyMethodWrapper.getMethod().getParameters().length; i++) {
			final Parameter parameter = proxyMethodWrapper.getMethod().getParameters()[i];
			
			if (parameter.isAnnotationPresent(Authorizer.class)) {
				if (proxyMethodWrapper.getParameters() != null && proxyMethodWrapper.getParameters().length >= i) {
					authorization = new Authorization(parameter.getType(), proxyMethodWrapper.getParameters()[i], proxyMethodWrapper);
				}
			}
		}
		
		if (authorization != null && authorize(authorization, authorizable.value())) {
			return proxyMethodWrapper.invoke();
		} else {
			final Rule[] rules = authorizable.value();
			final StringBuilder stringBuilder = new StringBuilder();
			
			for (final Rule rule : rules) {
				stringBuilder.append(StringHelper.join(rule.value(), " ,", ". "));
			}
			
			throw new ForbiddenException(String.format("Access denied for method \"%s\" and rules \"%s\"", proxyMethodWrapper.getMethod().getName(), stringBuilder.toString()));
		}
	}
	
	@Override
	public Object intercept(final ProxyMethodWrapper proxyMethodWrapper) {
		if (proxyMethodWrapper.getMethod().isAnnotationPresent(Transactionable.class) && proxyMethodWrapper.getMethod().isAnnotationPresent(Authorizable.class)) {
			return interceptTransactionable(proxyMethodWrapper.getMethod().getAnnotation(Transactionable.class), proxyMethodWrapper.getMethod().getAnnotation(Authorizable.class), proxyMethodWrapper);
		} else if (proxyMethodWrapper.getMethod().isAnnotationPresent(Transactionable.class)) {
			return interceptTransactionable(proxyMethodWrapper.getMethod().getAnnotation(Transactionable.class), null, proxyMethodWrapper);
		} else if (proxyMethodWrapper.getMethod().isAnnotationPresent(Authorizable.class)) {
			return interceptAuthorizable(proxyMethodWrapper.getMethod().getAnnotation(Authorizable.class), proxyMethodWrapper);
		} else {
			return proxyMethodWrapper.invoke();
		}
	}
}
