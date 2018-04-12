package japp.web.controller.ws;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;

import japp.model.util.JpaHelper;
import japp.util.JAppRuntimeException;
import japp.util.JsonHelper;
import japp.util.ProxyMethodWrapper;
import japp.web.controller.Controller;

public abstract class WsController extends Endpoint implements Controller {

    private static final Set<Session> sessions = Collections.synchronizedSet(new HashSet<Session>());

    protected WsController() {

    }

    protected static Set<Session> getSessions() {
        return sessions;
    }

    public abstract String getEndpointPath();

    @Override
    public void onOpen(final Session session, final EndpointConfig endpointConfig) {
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

    @SuppressWarnings("unchecked")
    public <T extends WsController> void broadcast(final SessionRunnable<T> sessionRunnable) {
        for (final Session session : sessions) {
            try {
                sessionRunnable.run((T) this, session);
            } catch (final ClassCastException exception) {
                sessionRunnable.run(null, session);
            }
        }
    }

    public void sendSyncAsJson(final Session session, final Object object) {
        try {
            session.getBasicRemote().sendText(convertToJson(jpaInitialize(object)));
        } catch (IOException exception) {
            throw new JAppRuntimeException(exception);
        }
    }

    public void sendAsyncAsJson(final Session session, final Object object) {
        session.getAsyncRemote().sendText(convertToJson(jpaInitialize(object)));
    }

    protected String convertToJson(final Object object) {
        return JsonHelper.toString(object);
    }

    protected Object jpaInitialize(final Object object) {
        return JpaHelper.initialize(object);
    }
}
