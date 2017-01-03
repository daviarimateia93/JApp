package japp.stp.command.runnable;

import japp.stp.command.Command;
import stp.gateway.Peer;

public interface CommandRunnable {
	
	public void run(final Peer peer, final Command command);
}
