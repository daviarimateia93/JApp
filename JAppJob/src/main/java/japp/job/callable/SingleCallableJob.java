package japp.job.callable;

public abstract class SingleCallableJob<T> extends CallableJob<T> {
	
	protected SingleCallableJob() {
		
	}
	
	private boolean called = false;
	
	protected abstract T callOnce();
	
	@Override
	public final T call() throws Exception {
		if (!called) {
			called = true;
			
			callOnce();
		}
		
		return (T) null;
	}
}
