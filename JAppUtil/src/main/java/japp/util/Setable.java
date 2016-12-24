package japp.util;

public class Setable<T> {
	
	private T value;
	
	public T defaultValue() {
		return null;
	}
	
	public Setable() {
		value = defaultValue();
	}
	
	public Setable(T value) {
		this.value = value;
	}
	
	public T getValue() {
		return value;
	}
	
	public void setValue(final T value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return value == null ? "null" : value.toString();
	}
	
	@Override
	public boolean equals(final Object object) {
		return value == null && object == null ? true : value == null && object != null ? false : value.equals(object);
	}
	
	@Override
	public int hashCode() {
		return value == null ? 0 : value.hashCode();
	}
}
