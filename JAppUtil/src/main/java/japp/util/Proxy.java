package japp.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class Proxy {
	
	@SuppressWarnings("unchecked")
	public static <T extends ProxyInterceptable> T intercept(final Class<T> superclass, final Class<?>[] parameterClasses, final Object... parameters) {
		final ProxyEnhancer proxyEnhancer = new ProxyEnhancer();
		proxyEnhancer.setSuperclass(superclass);
		proxyEnhancer.setCallback(newMethodInterceptor());
		
		final T instance;
		
		if (parameterClasses == null || parameters == null || parameterClasses.length == 0 || parameters.length == 0) {
			instance = (T) proxyEnhancer.create();
		} else {
			instance = (T) proxyEnhancer.create(parameterClasses, parameters);
		}
		
		return instance;
	}
	
	public static <T extends ProxyInterceptable> T intercept(final Class<T> superclass) {
		return intercept(superclass, new Class<?>[] {}, new Object[] {});
	}
	
	protected static MethodInterceptor newMethodInterceptor() {
		return new MethodInterceptor() {
			@Override
			public Object intercept(final Object instance, final Method method, final Object[] parameters, final MethodProxy methodProxy) throws Throwable {
				final Class<?> type = method.getDeclaringClass();
				
				if (ProxyInterceptable.class.isAssignableFrom(type) && isInterceptable(method)) {
					final Method interceptMethod = ReflectionHelper.getMethod(type, "intercept", new Class<?>[] { ProxyMethodWrapper.class });
					
					return interceptMethod.invoke(instance, new ProxyMethodWrapper(methodProxy, instance, method, parameters));
				} else {
					return methodProxy.invokeSuper(instance, parameters);
				}
			}
		};
	}
	
	protected static boolean isInterceptable(final Method method) {
		return isInterceptable(method.getAnnotations());
	}
	
	protected static boolean isInterceptable(final Annotation[] annotations) {
		for (final Annotation annotation : annotations) {
			if (annotation.annotationType().equals(ProxyMethodInterceptable.class)) {
				return true;
			} else if (annotation.annotationType().isAnnotationPresent(ProxyMethodInterceptable.class)) {
				return true;
			}
		}
		
		return false;
	}
}
