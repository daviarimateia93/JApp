package japp.model.entity;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;

import japp.model.util.JpaHelper;
import japp.util.CloneHelper;
import japp.util.JsonHelper;
import japp.util.ReflectionHelper;

public abstract class Entity implements Serializable, Cloneable {
	
	private static final long serialVersionUID = 1272141855986763067L;
	
	protected Entity() {
		
	}
	
	public List<Field> getFields() {
		return getFields(null);
	}
	
	public List<Field> getNonJsonIgnoreFields() {
		return getFields(f -> !f.isAnnotationPresent(JsonIgnore.class));
	}
	
	public List<Field> getIdFields() {
		return getFields(f -> f.isAnnotationPresent(Id.class));
	}
	
	public List<Field> getFields(final Predicate<Field> predicate) {
		final List<Field> idFields = new ArrayList<>();
		final List<Field> fields = ReflectionHelper.getFields(this);
		
		for (final Field field : fields) {
			if (predicate == null || predicate.test(field)) {
				idFields.add(field);
			}
		}
		
		return idFields;
	}
	
	public Object getFieldValue(final Field field) {
		field.setAccessible(true);
		
		try {
			return field.get(this);
		} catch (final IllegalArgumentException | IllegalAccessException exception) {
			return null;
		}
	}
	
	public List<Object> getFieldsValues() {
		return getFieldsValues(getFields());
	}
	
	public List<Object> getNonJsonIgnoreFieldsValues() {
		return getFieldsValues(getNonJsonIgnoreFields());
	}
	
	public List<Object> getIdFieldsValues() {
		return getFieldsValues(getIdFields());
	}
	
	public List<Object> getFieldsValues(final List<Field> fields) {
		return fields.stream().map(this::getFieldValue).collect(Collectors.toList());
	}
	
	public <T extends Entity> void merge(final T entity) {
		JpaHelper.merge(entity, this);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Entity> T copy() {
		return (T) CloneHelper.clone(this);
	}
	
	@Override
	public String toString() {
		final List<Object> idFieldsValues = getIdFieldsValues();
		final List<Object> values = idFieldsValues.isEmpty() || idFieldsValues.stream().anyMatch(object -> object == null) ? getNonJsonIgnoreFieldsValues() : idFieldsValues;
		
		final StringBuilder stringBuilder = new StringBuilder();
		
		for (int i = 0; i < values.size(); i++) {
			if (i > 0) {
				stringBuilder.append(";");
			}
			
			stringBuilder.append(values.get(i));
		}
		
		return stringBuilder.toString();
	}
	
	@Override
	public boolean equals(final Object object) {
		if (object != null && getClass().isAssignableFrom(object.getClass())) {
			return toString().equals(object.toString());
		} else {
			return false;
		}
	}
	
	public boolean equalsIgnoreCase(final Object object) {
		if (getClass().isAssignableFrom(object.getClass())) {
			return toString().equalsIgnoreCase(object.toString());
		} else {
			return false;
		}
	}
	
	public boolean identicals(final Object object) {
		if (getClass().isAssignableFrom(object.getClass())) {
			return JsonHelper.toString(this).equals(JsonHelper.toString(object));
		} else {
			return false;
		}
	}
	
	public boolean identicalsIgnoreCase(final Object object) {
		if (getClass().isAssignableFrom(object.getClass())) {
			return JsonHelper.toString(this).equalsIgnoreCase(JsonHelper.toString(object));
		} else {
			return false;
		}
	}
	
	public void initialize() {
		JpaHelper.initialize(this);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		
		int result = 1;
		
		for (final Object idFieldValue : getIdFieldsValues()) {
			result = prime * result + (idFieldValue == null ? 0 : idFieldValue.hashCode()) + getClass().getName().hashCode();
		}
		
		return result;
	}
}
