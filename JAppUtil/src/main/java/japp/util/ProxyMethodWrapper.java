package japp.util;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodProxy;

public class ProxyMethodWrapper {
	private final MethodProxy methodProxy;
	private final Object instance;
	private final Method method;
	private final Object[] parameters;
	
	public ProxyMethodWrapper(final MethodProxy methodProxy, final Object instance, final Method method, final Object... parameters) {
		this.methodProxy = methodProxy;
		this.instance = instance;
		this.method = method;
		this.parameters = parameters;
	}
	
	public MethodProxy getMethodProxy() {
		return methodProxy;
	}
	
	public Object getInstance() {
		return instance;
	}
	
	public Method getMethod() {
		return method;
	}
	
	public Object[] getParameters() {
		return parameters;
	}
	
	public Object invoke() {
		try {
			return methodProxy.invokeSuper(instance, parameters);
		} catch (final Throwable throwable) {
			throw new JAppRuntimeException(throwable);
		}
	}
}
