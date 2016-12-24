package japp.model.service.authorization;

import japp.util.JAppRuntimeException;

public class ForbiddenException extends JAppRuntimeException {
	
	private static final long serialVersionUID = -729194906525348957L;
	
	public ForbiddenException() {
		super();
	}
	
	public ForbiddenException(final String message) {
		super(message);
	}
	
	public ForbiddenException(final Throwable throwable) {
		super(throwable);
	}
	
	public ForbiddenException(final String message, final Throwable throwable) {
		super(message, throwable);
	}
}
