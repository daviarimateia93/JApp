package japp.util;

public abstract class ThreadHelper {

    protected ThreadHelper() {

    }

    public static Thread executeInNewThread(final Runnable runnable) {
        final Thread thread = new Thread(runnable);

        thread.start();

        return thread;
    }

    public static Thread executeInNewThreadAndJoin(final Runnable runnable) {
        final Thread thread = executeInNewThread(runnable);

        try {
            thread.join();
        } catch (final InterruptedException exception) {

        }

        return thread;
    }
}
