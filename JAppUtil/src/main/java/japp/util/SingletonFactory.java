package japp.util;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class SingletonFactory {
	
	private static final Map<Class<? extends Singletonable>, Singletonable> instances = new HashMap<>();
	private static final ThreadLocal<Map<Class<?>, Object>> instancesPerThread = new ThreadLocal<>();
	
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
	
	public static <T extends Singletonable> T getInstancePerThread(final Class<T> instanceClass, final Class<?>[] parameterClasses, final Object... parameters) {
		return getInstancePerThread(instanceClass, new Callable<T>() {
			@Override
			public T call() throws Exception {
				return ReflectionHelper.newInstance(instanceClass, parameterClasses, parameters);
			}
		});
	}
	
	public static <T extends Singletonable> T getInstancePerThread(final Class<T> instanceClass) {
		return getInstancePerThread(instanceClass, new Callable<T>() {
			@Override
			public T call() throws Exception {
				return ReflectionHelper.newInstance(instanceClass);
			}
		});
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
	public static <T extends Singletonable & ProxyInterceptable> T getProxyInterceptableInstancePerThread(final Class<T> instanceClass, final Class<?>[] parameterClasses, final Object... parameters) {
		if (instancesPerThread.get().containsKey(instanceClass)) {
			return (T) instancesPerThread.get().get(instanceClass);
		} else {
			return getInstancePerThread(instanceClass, new Callable<T>() {
				@Override
				public T call() throws Exception {
					return Proxy.intercept(instanceClass, parameterClasses, parameters);
				}
			});
		}
	}
	
	public static <T extends Singletonable & ProxyInterceptable> T getProxyInterceptableInstancePerThread(final Class<T> instanceClass) {
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
	
	@SuppressWarnings("unchecked")
	protected static <T extends Singletonable> T getInstancePerThread(final Class<T> instanceClass, final Callable<T> callable) {
		try {
			if (instancesPerThread.get() == null) {
				instancesPerThread.set(new HashMap<>());
			}
			
			if (instancesPerThread.get().containsKey(instanceClass)) {
				return (T) instancesPerThread.get().get(instanceClass);
			} else {
				instancesPerThread.get().put(instanceClass, null);
				T instance = callable.call();
				instancesPerThread.get().put(instanceClass, instance);
				
				return instance;
			}
		} catch (final Exception exception) {
			throw new JAppRuntimeException(exception);
		}
	}
}
