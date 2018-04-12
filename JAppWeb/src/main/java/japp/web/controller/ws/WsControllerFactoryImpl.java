package japp.web.controller.ws;

import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

import japp.util.Reference;
import japp.util.SingletonFactory;
import japp.util.Singletonable;

public class WsControllerFactoryImpl implements Singletonable, WsControllerFactory {

    public static synchronized WsControllerFactoryImpl getInstance() {
        return SingletonFactory.getInstance(WsControllerFactoryImpl.class).get();
    }

    protected WsControllerFactoryImpl() {

    }

    @Override
    public <T extends WsController> Reference<T> getWsController(final Class<T> wsControllerClass) {
        return SingletonFactory.getProxyInterceptableInstance(wsControllerClass);
    }

    @Override
    public <T extends WsController> ServerEndpointConfig getServerEndpointConfig(final Class<T> wsControllerClass) {
        final T instance = getWsController(wsControllerClass).get();

        return ServerEndpointConfig.Builder.create(wsControllerClass, instance.getEndpointPath())
                .configurator(new ServerEndpointConfig.Configurator() {

                    @SuppressWarnings("unchecked")
                    @Override
                    public <U> U getEndpointInstance(final Class<U> endpointClass) throws InstantiationException {
                        return (U) instance;
                    }

                    @Override
                    public void modifyHandshake(final ServerEndpointConfig serverEndpointConfig,
                            final HandshakeRequest handshakeRequest, final HandshakeResponse handshakeResponse) {
                        super.modifyHandshake(serverEndpointConfig, handshakeRequest, handshakeResponse);

                        serverEndpointConfig.getUserProperties().put(handshakeRequest.getClass().getName(),
                                handshakeRequest);
                        serverEndpointConfig.getUserProperties().put(handshakeResponse.getClass().getName(),
                                handshakeResponse);
                    }
                }).build();
    }
}
