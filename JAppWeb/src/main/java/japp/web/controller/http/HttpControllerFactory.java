package japp.web.controller.http;

public interface HttpControllerFactory {
	
	public <T extends HttpController> T getHttpController(final Class<T> httpControllerClass);
}
