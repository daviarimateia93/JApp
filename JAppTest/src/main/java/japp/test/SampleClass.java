package japp.test;

import japp.util.ProxyInterceptable;
import japp.util.ProxyMethodWrapper;
import japp.web.controller.http.annotation.UriVariable;

public class SampleClass implements ProxyInterceptable {

    private String name;

    public SampleClass(String name) {
        this.name = name;
    }

    @MetaAnnotation
    public void hello() {
        System.out.println("hello, " + name);
    }

    public void hello(String str1, @UriVariable String variable) {

    }

    @Override
    public Object intercept(final ProxyMethodWrapper proxyMethodWrapper) {
        System.out.println("intercepted");
        return proxyMethodWrapper.invoke();
    }
}
