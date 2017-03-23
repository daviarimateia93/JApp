package japp.web.controller.ws;

import javax.websocket.Session;

public interface SessionRunnable {
	
	public void run(final Session session);
}
