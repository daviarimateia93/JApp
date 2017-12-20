package japp.job.callable;

import java.util.concurrent.Callable;

import japp.job.Job;
import japp.util.Reference;
import japp.util.ThreadHelper;

public abstract class CallableJob<T> extends Job implements Callable<T> {
	
	protected CallableJob() {
		
	}
	
	public abstract T execute();
	
	@Override
	public T call() throws Exception {
		final Reference<T> value = new Reference<>();
		
		if (executeInNewThread()) {
			ThreadHelper.executeInNewThreadAndJoin(()-> value.set(execute()));
		} else {
			value.set(execute());
		}
		
		return value.get();
	}
}