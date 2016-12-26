package japp.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public abstract class CloneHelper {
	
	protected CloneHelper() {
		
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T clone(final T object) {
		if (object == null) {
			return null;
		}
		
		try {
			ObjectOutputStream objectOutputStream = null;
			ObjectInputStream objectInputStream = null;
			
			try {
				final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
				objectOutputStream.writeObject(object);
				objectOutputStream.flush();
				objectInputStream = new ObjectInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
				
				return (T) objectInputStream.readObject();
			} finally {
				if (objectOutputStream != null) {
					objectOutputStream.close();
				}
				
				if (objectInputStream != null) {
					objectInputStream.close();
				}
			}
		} catch (final ClassNotFoundException | IOException exception) {
			return null;
		}
	}
}
