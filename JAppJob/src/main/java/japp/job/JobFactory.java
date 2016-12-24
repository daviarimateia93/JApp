package japp.job;

import java.util.concurrent.TimeUnit;

import japp.job.callable.CallableJob;
import japp.job.runnable.RunnableJob;

public interface JobFactory {
	
	public <T extends Job> T getJob(final Class<T> jobClass);
	
	public void schedule(final CallableJob<?> callableJob, final long delay, final TimeUnit timeUnit);
	
	public void schedule(final RunnableJob runnableJob, final long delay, final TimeUnit timeUnit);
	
	public void scheduleAtFixedRate(final RunnableJob runnableJob, final long initialDelay, final long period, final TimeUnit timeUnit);
	
	public void scheduleWithFixedDelay(final RunnableJob runnableJob, final long initialDelay, final long delay, final TimeUnit timeUnit);
}
