package japp.util;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class ReflectionHelper {
	
	protected ReflectionHelper() {
		
	}
	
	public static Class<?> getGenericClass(final Field field) {
		try {
			return (Class<?>) ((ParameterizedType) field.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		} catch (final Exception exception) {
			return null;
		}
	}
	
	public static Field getField(final String fieldName, final Class<?> type) {
		for (final Field field : getFields(type)) {
			if (field.getName().equals(fieldName)) {
				return field;
			}
		}
		
		return null;
	}
	
	public static void setValue(final Field field, final Object instance, final Object value) {
		try {
			field.setAccessible(true);
			field.set(instance, value);
		} catch (final IllegalArgumentException | IllegalAccessException exception) {
			
		}
	}
	
	public static void setValue(final String fieldName, final Object instance, final Object value) {
		setValue(ReflectionHelper.getField(fieldName, instance.getClass()), instance, value);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getValue(final Field field, final Object instance) {
		try {
			field.setAccessible(true);
			
			return field != null ? (T) field.get(instance) : null;
		} catch (final IllegalArgumentException | IllegalAccessException | SecurityException exception) {
			return null;
		}
	}
	
	public static <T> T getValue(final String fieldName, final Object instance) {
		return getValue(getField(fieldName, instance.getClass()), instance);
	}
	
	public static List<Field> getFields(final Object instance) {
		final List<Field> fields = new ArrayList<>();
		final Class<?> type = instance.getClass();
		
		getFields(fields, type);
		
		return fields;
	}
	
	public static List<Field> getFields(final Class<?> type) {
		final List<Field> fields = new ArrayList<>();
		
		getFields(fields, type);
		
		return fields;
	}
	
	protected static void getFields(final List<Field> fields, final Class<?> type) {
		for (final Field field : type.getDeclaredFields()) {
			fields.add(field);
		}
		
		if (type.getSuperclass() != null) {
			getFields(fields, type.getSuperclass());
		}
	}
	
	public static List<Field> getEnumFieldsConstants(final Class<?> type) {
		final List<Field> enumFieldsConstants = new ArrayList<>();
		final List<Field> fields = getFields(type);
		
		for (final Field field : fields) {
			if (field.isEnumConstant()) {
				enumFieldsConstants.add(field);
			}
		}
		
		return enumFieldsConstants;
	}
	
	public static String fieldToName(final Field field) {
		return field.getName();
	}
	
	public static List<String> fieldsToNames(final Collection<Field> fields) {
		final List<String> names = new ArrayList<>();
		
		for (final Field field : fields) {
			names.add(fieldToName(field));
		}
		
		return names;
	}
	
	public static boolean isNull(final Field field, final Object instance) throws IllegalArgumentException, IllegalAccessException {
		return field.get(instance) == null;
	}
	
	public static boolean isNull(final Object object) {
		return object == null;
	}
	
	public static boolean isArray(final Field field) {
		return field.getType().isArray();
	}
	
	public static boolean isArray(final Object object) {
		return object.getClass().isArray();
	}
	
	public static boolean isNumeric(final Field field, final Object instance) throws IllegalArgumentException, IllegalAccessException {
		return (Short.class.isAssignableFrom(field.getType()) || Integer.class.isAssignableFrom(field.getType()) || Long.class.isAssignableFrom(field.getType()) || Float.class.isAssignableFrom(field.getType()) || Double.class.isAssignableFrom(field.getType())) || isNumeric(field.get(instance));
	}
	
	public static boolean isNumeric(final Object object) {
		return object instanceof Short || object instanceof Integer || object instanceof Long || object instanceof Float || object instanceof Double || object instanceof BigDecimal || object instanceof BigInteger;
	}
	
	public static boolean isBoolean(final Field field, final Object instance) throws IllegalArgumentException, IllegalAccessException {
		return Boolean.class.isAssignableFrom(field.getType()) || isBoolean(field.get(instance));
	}
	
	public static boolean isBoolean(final Object object) {
		return object instanceof Boolean;
	}
	
	public static boolean isCharacter(final Field field, final Object instance) throws IllegalArgumentException, IllegalAccessException {
		return Character.class.isAssignableFrom(field.getType()) || isCharacter(field.get(instance));
	}
	
	public static boolean isCharacter(final Object object) {
		return object instanceof Character;
	}
	
	public static boolean isString(final Field field, final Object instance) throws IllegalArgumentException, IllegalAccessException {
		return String.class.isAssignableFrom(field.getType()) || isString(field.get(instance));
	}
	
	public static boolean isString(final Object object) {
		return object instanceof String;
	}
	
	public static boolean isList(final Field field, final Object instance) throws IllegalArgumentException, IllegalAccessException {
		return List.class.isAssignableFrom(field.getType()) || isList(field.get(instance));
	}
	
	public static boolean isList(final Object object) {
		return object instanceof List;
	}
	
	public static boolean isSet(final Field field, final Object instance) throws IllegalArgumentException, IllegalAccessException {
		return Set.class.isAssignableFrom(field.getType()) || isSet(field.get(instance));
	}
	
	public static boolean isSet(final Object object) {
		return object instanceof Set;
	}
	
	public static boolean isMap(final Field field, final Object instance) throws IllegalArgumentException, IllegalAccessException {
		return Map.class.isAssignableFrom(field.getType()) || isMap(field.get(instance));
	}
	
	public static boolean isMap(final Object object) {
		return object instanceof Map;
	}
	
	public static boolean isEnum(final Field field, final Object instance) throws IllegalArgumentException, IllegalAccessException {
		return Enum.class.isAssignableFrom(field.getType()) || isEnum(field.get(instance));
	}
	
	public static boolean isEnum(final Object object) {
		return object instanceof Enum;
	}
	
	public static boolean isDate(final Field field, final Object instance) throws IllegalArgumentException, IllegalAccessException {
		return Date.class.isAssignableFrom(field.getType()) || isDate(field.get(instance));
	}
	
	public static boolean isDate(final Object object) {
		return object instanceof Date;
	}
	
	public static boolean isBigDecimal(final Field field, final Object instance) throws IllegalArgumentException, IllegalAccessException {
		return BigDecimal.class.isAssignableFrom(field.getType()) || isBigDecimal(field.get(instance));
	}
	
	public static boolean isBigDecimal(final Object object) {
		return object instanceof BigDecimal;
	}
	
	public static boolean isCollection(final Field field, final Object instance) throws IllegalArgumentException, IllegalAccessException {
		return Collection.class.isAssignableFrom(field.getType()) || isCollection(field.get(instance));
	}
	
	public static boolean isCollection(final Object object) {
		return object instanceof Collection;
	}
	
	public static boolean isPrimitive(final Field field, final Object instance) throws IllegalArgumentException, IllegalAccessException {
		return (isNumeric(field, instance) || isString(field, instance) || isCharacter(field, instance) || isBoolean(field, instance)) && !isArray(field);
	}
	
	public static boolean isPrimitive(final Object object) {
		return (isNumeric(object) || isString(object) || isCharacter(object) || isBoolean(object)) && !isArray(object);
	}
	
	public static Object generateDefaultValue(final Class<?> type) {
		try {
			if (type.isAssignableFrom(Integer.class) || type.isAssignableFrom(int.class)) {
				return 0;
			} else if (type.isAssignableFrom(Short.class) || type.isAssignableFrom(short.class)) {
				return 0;
			} else if (type.isAssignableFrom(Long.class) || type.isAssignableFrom(long.class)) {
				return 0;
			} else if (type.isAssignableFrom(Float.class) || type.isAssignableFrom(float.class)) {
				return 0;
			} else if (type.isAssignableFrom(Double.class) || type.isAssignableFrom(double.class)) {
				return 0;
			} else if (type.isAssignableFrom(Boolean.class) || type.isAssignableFrom(boolean.class)) {
				return false;
			} else if (type.isAssignableFrom(Character.class) || type.isAssignableFrom(char.class)) {
				return '\u0000';
			} else if (type.isAssignableFrom(Byte.class) || type.isAssignableFrom(byte.class)) {
				return 0;
			} else {
				return type.newInstance();
			}
		} catch (final InstantiationException | IllegalAccessException | IllegalArgumentException | SecurityException exception) {
			return null;
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object generateBasicValue(final String value, final Class<?> type) {
		if (value != null) {
			if (type.isAssignableFrom(String.class)) {
				return value;
			} else if (type.isAssignableFrom(Boolean.class) || type.isAssignableFrom(boolean.class)) {
				return Boolean.valueOf(value);
			} else if (type.isAssignableFrom(Character.class) || type.isAssignableFrom(char.class)) {
				return value.charAt(0);
			} else if (type.isAssignableFrom(Integer.class) || type.isAssignableFrom(int.class)) {
				return Integer.valueOf(value);
			} else if (type.isAssignableFrom(Short.class) || type.isAssignableFrom(short.class)) {
				return Short.valueOf(value);
			} else if (type.isAssignableFrom(Long.class) || type.isAssignableFrom(long.class)) {
				return Long.valueOf(value);
			} else if (type.isAssignableFrom(Float.class) || type.isAssignableFrom(float.class)) {
				return Float.valueOf(value);
			} else if (type.isAssignableFrom(Double.class) || type.isAssignableFrom(double.class)) {
				return Double.valueOf(value);
			} else if (type.isAssignableFrom(Byte.class) || type.isAssignableFrom(byte.class)) {
				return Byte.valueOf(value);
			} else if (type.isEnum()) {
				for (final Field field : getEnumFieldsConstants(type)) {
					field.setAccessible(true);
					
					if (field.getName().equals(value)) {
						return Enum.valueOf((Class<? extends Enum>) type, value);
					}
				}
				
				return null;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
	
	public static Object generateBasicValue(final String value, final Field field) {
		return generateBasicValue(value, field.getType());
	}
	
	public static Object generateBasicValue(final String value, final Object defaultValue) {
		if (defaultValue != null) {
			if (defaultValue instanceof String) {
				return value;
			} else if (defaultValue instanceof Boolean) {
				return Boolean.valueOf(value);
			} else if (defaultValue instanceof Character) {
				return value.charAt(0);
			} else if (defaultValue instanceof Integer) {
				return Integer.valueOf(value);
			} else if (defaultValue instanceof Short) {
				return Short.valueOf(value);
			} else if (defaultValue instanceof Long) {
				return Long.valueOf(value);
			} else if (defaultValue instanceof Float) {
				return Float.valueOf(value);
			} else if (defaultValue instanceof Double) {
				return Double.valueOf(value);
			} else if (defaultValue instanceof Byte) {
				return Byte.valueOf(value);
			} else {
				return defaultValue;
			}
		} else {
			return null;
		}
	}
	
	public static List<Method> getMethods(final Class<?> type, final String methodName) {
		return getMethods(type, methodName, new ArrayList<>());
	}
	
	private static List<Method> getMethods(final Class<?> type, final String methodName, final List<Method> methods) {
		final boolean hasSuperclass = type.getSuperclass() != null;
		
		methods.addAll(Arrays.asList(type.getDeclaredMethods()));
		
		if (hasSuperclass) {
			getMethods(type, methodName, methods);
		}
		
		return methods;
	}
	
	public static Method getMethod(final Class<?> type, final String methodName, final Class<?>[] parameterTypes) throws NoSuchMethodException {
		final boolean hasSuperclass = type.getSuperclass() != null;
		
		try {
			return type.getDeclaredMethod(methodName, parameterTypes);
		} catch (final NoSuchMethodException exception) {
			if (hasSuperclass) {
				return getMethod(type.getSuperclass(), methodName, parameterTypes);
			} else {
				throw exception;
			}
		}
	}
	
	public static <T> Constructor<T> getConstructor(final Class<T> type, final Class<?>... parameterClasses) throws NoSuchMethodException, SecurityException {
		return type.getDeclaredConstructor(parameterClasses);
	}
	
	public static <T> T newInstance(final Class<T> type, final Class<?>[] parameterClasses, final Object... parameters) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		final Constructor<T> constructor = ReflectionHelper.getConstructor(type, parameterClasses);
		
		return constructor.newInstance(parameters);
	}
	
	public static <T> T newInstance(final Class<T> type) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return newInstance(type, new Class<?>[] {}, new Object[] {});
	}
	
	public static <T> T forceNewInstance(final Class<T> type, final Class<?>[] parameterClasses, final Object... parameters) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		final Constructor<T> constructor = ReflectionHelper.getConstructor(type, parameterClasses);
		constructor.setAccessible(true);
		
		return constructor.newInstance(parameters);
	}
	
	public static <T> T forceNewInstance(final Class<T> type) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return forceNewInstance(type, new Class<?>[] {}, new Object[] {});
	}
	
	public static Object[] getObjectArray(Object object) {
		if (object instanceof Object[])
			return (Object[]) object;
		
		final int length = Array.getLength(object);
		final Object[] objects = new Object[length];
		
		for (int i = 0; i < length; ++i) {
			objects[i] = Array.get(object, i);
		}
		
		return objects;
	}
}
