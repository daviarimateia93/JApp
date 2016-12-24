package japp.web.controller.ws;

import javax.websocket.Endpoint;

import japp.util.ProxyMethodWrapper;
import japp.web.controller.Controller;

public abstract class WsController extends Endpoint implements Controller {
	
	public WsController() {
		
	}
	
	public abstract String getEndpointPath();
	
	@Override
	public Object intercept(final ProxyMethodWrapper proxyMethodWrapper) {
		return proxyMethodWrapper.invoke();
	}
}
