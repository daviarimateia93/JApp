package japp.web.controller.ws;

import javax.websocket.server.ServerEndpointConfig;

public interface WsControllerFactory {
	
	public <T extends WsController> T getWsController(final Class<T> wsControllerClass);
	
	public <T extends WsController> ServerEndpointConfig getServerEndpointConfig(final Class<T> wsControllerClass);
}
