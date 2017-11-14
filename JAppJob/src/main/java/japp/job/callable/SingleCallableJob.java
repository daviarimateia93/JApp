package japp.job.callable;

public abstract class SingleCallableJob<T> extends CallableJob<T> {
	
	private boolean called = false;
	
	protected SingleCallableJob() {
		
	}
	
	protected abstract T executeOnce();
	
	@Override
	public final T execute() {
		if (!called) {
			called = true;
			
			return executeOnce();
		}
		
		return null;
	}
}
