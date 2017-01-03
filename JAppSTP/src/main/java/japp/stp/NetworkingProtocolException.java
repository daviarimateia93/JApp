package japp.stp;

import japp.util.JAppRuntimeException;

public class NetworkingProtocolException extends JAppRuntimeException {
	
	private static final long serialVersionUID = -3320874714819665043L;
	
	public NetworkingProtocolException(final String message) {
		super(message);
	}
	
	public NetworkingProtocolException(final Throwable throwable) {
		super(throwable);
	}
}
