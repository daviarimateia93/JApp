package japp.web.controller.ws;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;

import japp.util.ProxyMethodWrapper;
import japp.web.controller.Controller;

public abstract class WsController extends Endpoint implements Controller {
	
	protected static final Set<Session> sessions = Collections.synchronizedSet(new HashSet<Session>());
	
	protected WsController() {
		
	}
	
	public abstract String getEndpointPath();
	
	@Override
	public void onOpen(final Session session, final EndpointConfig config) {
		sessions.add(session);
	}
	
	@Override
	public void onClose(final Session session, final CloseReason closeReason) {
		sessions.remove(session);
	}
	
	@Override
	public void onError(final Session session, final Throwable throwable) {
		sessions.remove(session);
	}
	
	@Override
	public Object intercept(final ProxyMethodWrapper proxyMethodWrapper) {
		return proxyMethodWrapper.invoke();
	}
	
	public void broadcast(final SessionRunnable sessionRunnable) {
		for (final Session session : sessions) {
			sessionRunnable.run(session);
		}
	}
}
