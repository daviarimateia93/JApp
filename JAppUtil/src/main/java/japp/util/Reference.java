package japp.util;

public class Reference<T> {
	
	private T value;
	
	
	public Reference() {
		
	}
	
	public Reference(T value) {
		this.value = value;
	}
	
	public T get() {
		return value;
	}
	
	public void set(final T value) {
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
