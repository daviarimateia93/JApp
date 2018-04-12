package japp.model.service.authorization;

import japp.util.JAppRuntimeException;

public class UnauthorizedException extends JAppRuntimeException {

    private static final long serialVersionUID = 4607185947586115855L;

    public UnauthorizedException() {
        super();
    }

    public UnauthorizedException(final String message) {
        super(message);
    }

    public UnauthorizedException(final Throwable throwable) {
        super(throwable);
    }

    public UnauthorizedException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}
