package japp.test;

import japp.job.runnable.SingleRunnableJob;

public class SampleJob extends SingleRunnableJob {

    @Override
    public void executeOnce() {
        System.out.println("running once");
    }
}
