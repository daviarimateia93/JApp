package japp.model;

import japp.model.business.BusinessFactory;
import japp.model.jpa.repository.RepositoryFactory;
import japp.model.jpa.repository.RepositoryManager;
import japp.model.service.ServiceFactory;

public interface ModelAppConfiguration {
	
	public BusinessFactory getBusinessFactory();
	
	public RepositoryFactory getRepositoryFactory();
	
	public RepositoryManager getRepositoryManager();
	
	public ServiceFactory getServiceFactory();
}
