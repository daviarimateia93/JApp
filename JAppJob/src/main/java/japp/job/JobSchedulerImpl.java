package japp.job;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import japp.job.callable.CallableJob;
import japp.job.runnable.RunnableJob;
import japp.util.SingletonFactory;
import japp.util.Singletonable;

public class JobSchedulerImpl implements Singletonable, JobScheduler {
	
	protected final ScheduledExecutorService scheduledExecutorService;
	
	public static synchronized JobSchedulerImpl getInstance() {
		return SingletonFactory.getInstance(JobSchedulerImpl.class);
	}
	
	protected JobSchedulerImpl() {
		scheduledExecutorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
	}
	
	@Override
	public void schedule(final CallableJob<?> callableJob, final long delay, final TimeUnit timeUnit) {
		scheduledExecutorService.schedule(callableJob, delay, timeUnit);
	}
	
	@Override
	public void schedule(final RunnableJob runnableJob, final long delay, final TimeUnit timeUnit) {
		scheduledExecutorService.schedule(runnableJob, delay, timeUnit);
	}
	
	@Override
	public void scheduleAtFixedRate(final RunnableJob runnableJob, final long initialDelay, final long period, final TimeUnit timeUnit) {
		scheduledExecutorService.scheduleAtFixedRate(runnableJob, initialDelay, period, timeUnit);
	}
	
	@Override
	public void scheduleWithFixedDelay(final RunnableJob runnableJob, final long initialDelay, final long delay, final TimeUnit timeUnit) {
		scheduledExecutorService.scheduleWithFixedDelay(runnableJob, initialDelay, delay, timeUnit);
	}
}
