package japp.job.callable;

public abstract class SingleCallableJob<T> implements CallableJob<T> {
	
	private boolean called = false;
	
	protected SingleCallableJob() {
		
	}
	
	protected abstract T callOnce();
	
	@Override
	public final T call() throws Exception {
		if (!called) {
			called = true;
			
			return callOnce();
		}
		
		return null;
	}
}
