package japp.job;

public abstract class Job {
	
	protected Job() {
		
	}
	
	public boolean executeInNewThread() {
		return true;
	}
}
