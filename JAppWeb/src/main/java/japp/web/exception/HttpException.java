package japp.web.exception;

import japp.util.JAppRuntimeException;

public class HttpException extends JAppRuntimeException {
	
	private static final long serialVersionUID = -2596827018296652367L;
	
	private final int httpStatusCode;
	
	public HttpException(final int httpStatusCode) {
		this.httpStatusCode = httpStatusCode;
	}
	
	public HttpException(final int httpStatusCode, final String message) {
		super(message);
		
		this.httpStatusCode = httpStatusCode;
	}
	
	public HttpException(final int httpStatusCode, final Throwable throwable) {
		super(throwable);
		
		this.httpStatusCode = httpStatusCode;
	}
	
	public HttpException(final int httpStatusCode, final String message, final Throwable throwable) {
		super(message, throwable);
		
		this.httpStatusCode = httpStatusCode;
	}
	
	public int getHttpStatusCode() {
		return httpStatusCode;
	}
}
