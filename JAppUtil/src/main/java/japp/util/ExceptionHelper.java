package japp.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public abstract class ExceptionHelper {

    protected ExceptionHelper() {

    }

    public static Throwable getCause(final Throwable throwable, final Class<? extends Throwable> throwableClass) {
        final Reference<Throwable> throwableCause = new Reference<>();

        iterateThroughCauses(throwable, new IterableListener<Throwable>() {

            @Override
            public void iterate(final Throwable throwable) {
                if (throwableClass.isAssignableFrom(throwable.getClass())) {
                    throwableCause.set(throwable);
                }
            }
        });

        return throwableCause.get();
    }

    public static Throwable getRootCause(final Throwable throwable) {
        final Reference<Throwable> throwableRootCause = new Reference<>(throwable);

        iterateThroughCauses(throwable, new IterableListener<Throwable>() {

            @Override
            public void iterate(final Throwable throwable) {
                throwableRootCause.set(throwable);
            }
        });

        return throwableRootCause.get();
    }

    public static void iterateThroughCauses(final Throwable throwable,
            final IterableListener<Throwable> iterableListener) {
        Throwable throwableLastCause = throwable;
        Throwable throwableCurrentCause = null;

        while (null != (throwableCurrentCause = throwableLastCause.getCause())
                && (throwableLastCause != throwableCurrentCause)) {
            throwableLastCause = throwableCurrentCause;

            iterableListener.iterate(throwableLastCause);
        }
    }

    public static String getStackTraceAsString(final Throwable throwable) {
        final StringWriter stringWriter = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(stringWriter);

        throwable.printStackTrace(printWriter);

        return stringWriter.toString();
    }
}
