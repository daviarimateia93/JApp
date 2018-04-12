package japp.util;

public class JAppRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1848235014147606607L;

    public JAppRuntimeException() {
        super();
    }

    public JAppRuntimeException(final String message) {
        super(message);
    }

    public JAppRuntimeException(final Throwable throwable) {
        super(throwable);
    }

    public JAppRuntimeException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

    public JAppRuntimeException(final String message, final Throwable throwable, final boolean enableSuppression,
            final boolean writableStackTrace) {
        super(message, throwable, enableSuppression, writableStackTrace);
    }
}
