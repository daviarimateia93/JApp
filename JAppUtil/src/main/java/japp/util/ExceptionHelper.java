package japp.util;

public abstract class ExceptionHelper {
	
	protected ExceptionHelper() {
		
	}
	
	public static Throwable getCause(final Throwable throwable, final Class<? extends Throwable> throwableClass) {
		final Setable<Throwable> throwableCause = new Setable<>();
		
		iterateThroughCauses(throwable, new IterableListener<Throwable>() {
			
			@Override
			public void iterate(final Throwable throwable) {
				if (throwableClass.isAssignableFrom(throwable.getClass())) {
					throwableCause.setValue(throwable);
				}
			}
		});
		
		return throwableCause.getValue();
	}
	
	public static Throwable getRootCause(final Throwable throwable) {
		final Setable<Throwable> throwableRootCause = new Setable<>(throwable);
		
		iterateThroughCauses(throwable, new IterableListener<Throwable>() {
			
			@Override
			public void iterate(final Throwable throwable) {
				throwableRootCause.setValue(throwable);
			}
		});
		
		return throwableRootCause.getValue();
	}
	
	public static void iterateThroughCauses(final Throwable throwable, final IterableListener<Throwable> iterableListener) {
		Throwable throwableLastCause = throwable;
		Throwable throwableCurrentCause = null;
		
		while (null != (throwableCurrentCause = throwableLastCause.getCause()) && (throwableLastCause != throwableCurrentCause)) {
			throwableLastCause = throwableCurrentCause;
			
			iterableListener.iterate(throwableLastCause);
		}
	}
}
