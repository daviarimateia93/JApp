package japp.model.entity;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;

import japp.model.util.JPAHelper;
import japp.util.CloneHelper;
import japp.util.JsonHelper;
import japp.util.ReflectionHelper;

public abstract class Entity implements Serializable, Cloneable {
	
	private static final long serialVersionUID = 1272141855986763067L;
	
	public List<Field> getIdFields() {
		final List<Field> idFields = new ArrayList<>();
		final List<Field> fields = ReflectionHelper.getFields(this);
		
		for (final Field field : fields) {
			if (field.isAnnotationPresent(Id.class)) {
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
	
	public List<Object> getIdFieldsValues() {
		final List<Object> idFieldsValues = new ArrayList<>();
		
		for (final Field field : getIdFields()) {
			idFieldsValues.add(getFieldValue(field));
		}
		
		return idFieldsValues;
	}
	
	public <T extends Entity> void merge(final T entity) {
		JPAHelper.merge(entity, this);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Entity> T copy() {
		return (T) CloneHelper.clone(this);
	}
	
	@Override
	public String toString() {
		final List<Object> idFieldsValues = getIdFieldsValues();
		
		if (idFieldsValues.isEmpty() || idFieldsValues.stream().anyMatch(object -> object == null)) {
			return JsonHelper.toString(this);
		} else {
			final StringBuilder stringBuilder = new StringBuilder();
			
			for (int i = 0; i < idFieldsValues.size(); i++) {
				if (i > 0) {
					stringBuilder.append(";");
				}
				
				stringBuilder.append(idFieldsValues.get(i));
			}
			
			return stringBuilder.toString();
		}
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
		JPAHelper.initialize(this);
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
