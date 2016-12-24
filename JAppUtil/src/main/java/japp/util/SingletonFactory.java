package japp.util;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class SingletonFactory {
	
	private static final Map<Class<? extends Singletonable>, Singletonable> instances = new HashMap<>();
	
	@SuppressWarnings("unchecked")
	public static <T extends Singletonable> T getInstance(final Class<T> instanceClass, final Class<?>[] parameterClasses, final Object... parameters) {
		if (instances.containsKey(instanceClass)) {
			return (T) instances.get(instanceClass);
		} else {
			try {
				return getInstance(ReflectionHelper.forceNewInstance(instanceClass, parameterClasses, parameters));
			} catch (final InstantiationException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
				throw new JAppRuntimeException(exception);
			}
		}
	}
	
	public static <T extends Singletonable> T getInstance(final Class<T> instanceClass) {
		return getInstance(instanceClass, new Class<?>[] {}, new Object[] {});
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Singletonable & ProxyInterceptable> T getProxyInterceptableInstance(final Class<T> instanceClass, final Class<?>[] parameterClasses, final Object... parameters) {
		if (instances.containsKey(instanceClass)) {
			return (T) instances.get(instanceClass);
		} else {
			return getInstance(Proxy.intercept(instanceClass, parameterClasses, parameters));
		}
	}
	
	public static <T extends Singletonable & ProxyInterceptable> T getProxyInterceptableInstance(final Class<T> instanceClass) {
		return getProxyInterceptableInstance(instanceClass, new Class<?>[] {}, new Object[] {});
	}
	
	@SuppressWarnings("unchecked")
	protected static <T extends Singletonable> T getInstance(final T instance) {
		if (instances.containsKey(instance.getClass())) {
			return (T) instances.get(instance.getClass());
		} else {
			instances.put(instance.getClass(), instance);
			
			return instance;
		}
	}
}
