package japp.job.runnable;

public abstract class SingleRunnableJob implements RunnableJob {
	
	private boolean ran = false;
	
	protected SingleRunnableJob() {
		
	}
	
	protected abstract void runOnce();
	
	@Override
	public final void run() {
		if (!ran) {
			ran = true;
			
			runOnce();
		}
	}
}
