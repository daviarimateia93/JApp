package japp.model.service.authorization;

import japp.util.ProxyMethodWrapper;

public class Authorization {

    private final Class<?> authorizationClass;
    private final Object authorizationObject;
    private final ProxyMethodWrapper proxyMethodWrapper;

    public Authorization(final Class<?> authorizationClass, final Object authorizationObject,
            final ProxyMethodWrapper proxyMethodWrapper) {
        this.authorizationClass = authorizationClass;
        this.authorizationObject = authorizationObject;
        this.proxyMethodWrapper = proxyMethodWrapper;
    }

    public Class<?> getAuthorizationClass() {
        return authorizationClass;
    }

    public Object getAuthorizationObject() {
        return authorizationObject;
    }

    public ProxyMethodWrapper getProxyMethodWrapper() {
        return proxyMethodWrapper;
    }
}
