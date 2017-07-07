package japp.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class SingletonFactory {
	
	private static final Map<Class<? extends Singletonable>, Reference<? extends Singletonable>> instances = new HashMap<>();
	private static final ThreadLocal<Map<Class<? extends Singletonable>, Reference<? extends Singletonable>>> instancesPerThread = new ThreadLocal<>();
	
	public static <T extends Singletonable> Reference<T> getInstance(final Class<T> instanceClass, final Class<?>[] parameterClasses, final Object... parameters) {
		return getInstance(instanceClass, new Callable<T>() {
			@Override
			public T call() throws Exception {
				return ReflectionHelper.forceNewInstance(instanceClass, parameterClasses, parameters);
			}
		});
	}
	
	public static <T extends Singletonable> Reference<T> getInstance(final Class<T> instanceClass) {
		return getInstance(instanceClass, new Class<?>[] {}, new Object[] {});
	}
	
	public static <T extends Singletonable> Reference<T> getInstancePerThread(final Class<T> instanceClass, final Class<?>[] parameterClasses, final Object... parameters) {
		return getInstancePerThread(instanceClass, new Callable<T>() {
			@Override
			public T call() throws Exception {
				return ReflectionHelper.forceNewInstance(instanceClass, parameterClasses, parameters);
			}
		});
	}
	
	public static <T extends Singletonable> Reference<T> getInstancePerThread(final Class<T> instanceClass) {
		return getInstancePerThread(instanceClass, new Callable<T>() {
			@Override
			public T call() throws Exception {
				return ReflectionHelper.forceNewInstance(instanceClass);
			}
		});
	}
	
	public static <T extends Singletonable & ProxyInterceptable> Reference<T> getProxyInterceptableInstance(final Class<T> instanceClass, final Class<?>[] parameterClasses, final Object... parameters) {
		return getInstance(instanceClass, new Callable<T>() {
			@Override
			public T call() throws Exception {
				return Proxy.intercept(instanceClass, parameterClasses, parameters);
			}
		});
	}
	
	public static <T extends Singletonable & ProxyInterceptable> Reference<T> getProxyInterceptableInstance(final Class<T> instanceClass) {
		return getProxyInterceptableInstance(instanceClass, new Class<?>[] {}, new Object[] {});
	}
	
	public static <T extends Singletonable & ProxyInterceptable> Reference<T> getProxyInterceptableInstancePerThread(final Class<T> instanceClass, final Class<?>[] parameterClasses, final Object... parameters) {
		return getInstancePerThread(instanceClass, new Callable<T>() {
			@Override
			public T call() throws Exception {
				return Proxy.intercept(instanceClass, parameterClasses, parameters);
			}
		});
	}
	
	public static <T extends Singletonable & ProxyInterceptable> Reference<T> getProxyInterceptableInstancePerThread(final Class<T> instanceClass) {
		return getProxyInterceptableInstance(instanceClass, new Class<?>[] {}, new Object[] {});
	}
	
	protected static <T extends Singletonable> Reference<T> getInstance(final Class<T> instanceClass, final Callable<T> callable) {
		return getInstance(instances, instanceClass, callable);
	}
	
	protected static <T extends Singletonable> Reference<T> getInstancePerThread(final Class<T> instanceClass, final Callable<T> callable) {
		if (instancesPerThread.get() == null) {
			instancesPerThread.set(new HashMap<>());
		}
		
		return getInstance(instancesPerThread.get(), instanceClass, callable);
	}
	
	@SuppressWarnings("unchecked")
	protected static <T extends Singletonable> Reference<T> getInstance(final Map<Class<? extends Singletonable>, Reference<? extends Singletonable>> map, final Class<T> instanceClass, final Callable<T> callable) {
		try {
			if (map.containsKey(instanceClass)) {
				return (Reference<T>) map.get(instanceClass);
			} else {
				final Reference<T> reference = new Reference<>();
				
				map.put(instanceClass, reference);
				
				reference.set(callable.call());
				
				return reference;
			}
		} catch (final Exception exception) {
			throw new JAppRuntimeException(exception);
		}
	}
}
