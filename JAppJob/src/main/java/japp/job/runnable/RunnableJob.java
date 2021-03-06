package japp.job.runnable;

import japp.job.Job;
import japp.util.ThreadHelper;

public abstract class RunnableJob extends Job implements Runnable {

    protected RunnableJob() {

    }

    public abstract void execute();

    @Override
    public void run() {
        if (executeInNewThread()) {
            ThreadHelper.executeInNewThreadAndJoin(this::execute);
        } else {
            execute();
        }
    }
}