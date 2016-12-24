package japp.job.runnable;

public abstract class SingleRunnableJob extends RunnableJob {
	
	private boolean ran = false;
	
	protected abstract void runOnce();
	
	@Override
	public final void run() {
		if (!ran) {
			ran = true;
			
			runOnce();
		}
	}
}
