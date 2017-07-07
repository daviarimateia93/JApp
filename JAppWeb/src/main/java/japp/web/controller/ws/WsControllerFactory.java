package japp.web.controller.ws;

import javax.websocket.server.ServerEndpointConfig;

import japp.util.Reference;

public interface WsControllerFactory {
	
	public <T extends WsController> Reference<T> getWsController(final Class<T> wsControllerClass);
	
	public <T extends WsController> ServerEndpointConfig getServerEndpointConfig(final Class<T> wsControllerClass);
}
