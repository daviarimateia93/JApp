package japp.model.util;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import japp.model.entity.Entity;
import japp.util.ReflectionHelper;

public abstract class JPAHelper {
	
	protected JPAHelper() {
		
	}
	
	public static <T> T initialize(final T object) {
		return initialize(object, new LinkedHashSet<>());
	}
	
	protected static <T> T initialize(final T object, final Set<Object> visitedObjects) {
		if (object != null && !visitedObjects.contains(object)) {
			visitedObjects.add(object);
			
			if (ReflectionHelper.isCollection(object)) {
				for (final Object currentObject : (Collection<?>) object) {
					initialize(currentObject, visitedObjects);
				}
			} else if (ReflectionHelper.isArray(object)) {
				for (final Object currentObject : (Object[]) object) {
					initialize(currentObject, visitedObjects);
				}
			} else if (object instanceof Entity) {
				try {
					for (final PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(object.getClass()).getPropertyDescriptors()) {
						final Method readMethod = propertyDescriptor.getReadMethod();
						
						if (readMethod != null) {
							readMethod.setAccessible(true);
							
							final Object fieldValue = readMethod.invoke(object);
							
							if (object instanceof Entity || ReflectionHelper.isCollection(fieldValue) || ReflectionHelper.isArray(fieldValue)) {
								initialize(fieldValue, visitedObjects);
							}
						}
					}
				} catch (final IntrospectionException | IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
					
				}
			}
		}
		
		return object;
	}
	
	public static <T> void merge(final T provider, final T receiver) {
		merge(provider, receiver, new LinkedHashSet<>());
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected static <T> void merge(final T provider, final T receiver, final Set<Object> visitedObjects) {
		if (!visitedObjects.contains(receiver)) {
			visitedObjects.add(receiver);
			
			if (Map.class.isAssignableFrom(provider.getClass())) {
				mergeMap((Map) provider, (Map) receiver, visitedObjects);
			} else if (Collection.class.isAssignableFrom(provider.getClass())) {
				mergeCollection((Collection) provider, (Collection) receiver, visitedObjects);
			} else {
				try {
					for (final PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(provider.getClass()).getPropertyDescriptors()) {
						final Method writeMethod = propertyDescriptor.getWriteMethod();
						final Method readMethod = propertyDescriptor.getReadMethod();
						
						if (writeMethod != null && readMethod != null) {
							writeMethod.setAccessible(true);
							readMethod.setAccessible(true);
							
							final Object providerValue = readMethod.invoke(provider);
							final Object receiverValue = readMethod.invoke(receiver);
							
							if (providerValue == null || ReflectionHelper.isPrimitive(providerValue) || ReflectionHelper.isEnum(providerValue) || ReflectionHelper.isDate(providerValue) || ((!providerValue.equals(receiverValue) && providerValue instanceof Entity))) {
								writeMethod.invoke(receiver, providerValue);
							} else if (ReflectionHelper.isMap(providerValue)) {
								if (receiverValue == null) {
									writeMethod.invoke(receiver, providerValue);
								} else {
									mergeMap((Map) providerValue, (Map) receiverValue, visitedObjects);
								}
							} else if (ReflectionHelper.isCollection(providerValue)) {
								if (receiverValue == null) {
									writeMethod.invoke(receiver, providerValue);
								} else {
									mergeCollection((Collection) providerValue, (Collection) receiverValue, visitedObjects);
								}
							} else {
								merge(providerValue, receiverValue, visitedObjects);
							}
						}
					}
				} catch (final IntrospectionException | IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
					
				}
			}
		}
	}
	
	protected static <T, U> void mergeMap(final Map<T, U> providers, final Map<T, U> receivers, final Set<Object> visitedObjects) {
		for (final Map.Entry<T, U> providerEntry : providers.entrySet()) {
			if (receivers.containsKey(providerEntry.getKey())) {
				merge(providerEntry.getValue(), receivers.get(providerEntry.getKey()), visitedObjects);
			} else {
				receivers.put(providerEntry.getKey(), providerEntry.getValue());
			}
		}
		
		final Iterator<Map.Entry<T, U>> receiversIterator = receivers.entrySet().iterator();
		
		while (receiversIterator.hasNext()) {
			final Map.Entry<T, U> receiverEntry = receiversIterator.next();
			
			if (!providers.containsKey(receiverEntry.getKey())) {
				receiversIterator.remove();
			}
		}
	}
	
	protected static <T> void mergeCollection(final Collection<T> providers, final Collection<T> receivers, final Set<Object> visitedObjects) {
		for (final T provider : providers) {
			if (receivers.contains(provider)) {
				for (final T receiver : receivers) {
					if (provider.equals(receiver)) {
						merge(provider, receiver, visitedObjects);
					}
				}
			} else {
				receivers.add(provider);
			}
		}
		
		final Iterator<T> receiversIterator = receivers.iterator();
		
		while (receiversIterator.hasNext()) {
			final T receiver = receiversIterator.next();
			
			if (!providers.contains(receiver)) {
				receiversIterator.remove();
			}
		}
	}
}
