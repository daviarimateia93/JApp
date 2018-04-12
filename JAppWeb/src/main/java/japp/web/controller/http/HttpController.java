package japp.web.controller.http;

import japp.util.ProxyMethodWrapper;
import japp.web.controller.Controller;

public abstract class HttpController implements Controller {

    protected HttpController() {

    }

    @Override
    public Object intercept(final ProxyMethodWrapper proxyMethodWrapper) {
        return proxyMethodWrapper.invoke();
    }
}
