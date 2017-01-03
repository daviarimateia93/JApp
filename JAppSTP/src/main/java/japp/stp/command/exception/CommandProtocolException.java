package japp.stp.command.exception;

import japp.util.JAppRuntimeException;

public class CommandProtocolException extends JAppRuntimeException {
	
	private static final long serialVersionUID = -3320874714819665043L;
	
	public CommandProtocolException(final String message) {
		super(message);
	}
	
	public CommandProtocolException(final Throwable throwable) {
		super(throwable);
	}
}
