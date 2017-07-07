package japp.web.controller.http;

import japp.util.Reference;
import japp.util.SingletonFactory;
import japp.util.Singletonable;

public class HttpControllerFactoryImpl implements Singletonable, HttpControllerFactory {
	
	public static synchronized HttpControllerFactoryImpl getInstance() {
		return SingletonFactory.getInstance(HttpControllerFactoryImpl.class).get();
	}
	
	protected HttpControllerFactoryImpl() {
		
	}
	
	@Override
	public <T extends HttpController> Reference<T> getHttpController(final Class<T> httpControllerClass) {
		return SingletonFactory.getProxyInterceptableInstance(httpControllerClass);
	}
}
