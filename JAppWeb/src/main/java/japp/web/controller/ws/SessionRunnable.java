package japp.web.controller.ws;

import javax.websocket.Session;

public interface SessionRunnable <T extends WsController> {
	
	public void run(final T wsController, final Session session);
}
