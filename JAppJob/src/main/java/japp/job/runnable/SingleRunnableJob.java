package japp.job.runnable;

public abstract class SingleRunnableJob extends RunnableJob {
	
	private boolean ran = false;
	
	protected SingleRunnableJob() {
		
	}
	
	protected abstract void executeOnce();
	
	@Override
	public final void execute() {
		if (!ran) {
			ran = true;
			
			executeOnce();
		}
	}
}
