package japp.stp.command.parser;

import java.util.HashMap;
import java.util.Map;

import japp.model.ModelApp;
import japp.model.service.Service;
import japp.stp.command.Command;
import japp.stp.command.exception.CommandProtocolException;
import japp.stp.command.protocol.CommanProtocol;
import japp.stp.command.runnable.CommandRunnable;
import stp.gateway.Peer;
import stp.message.Message;
import stp.parser.Parser;
import stp.system.STPException;

public abstract class CommandParser extends Parser {
	
	private Map<String, CommandRunnable> readEvents = new HashMap<>();
	private Map<String, CommandRunnable> writtenEvents = new HashMap<>();
	
	protected CommandParser() {
		
	}
	
	protected void sendAsync(final Peer peer, final String name, final String... values) {
		sendAsync(peer, new Command(name, values));
	}
	
	protected void sendAsync(final Peer peer, final Command command) {
		sendAsync(peer, CommanProtocol.parse(getType(), command));
	}
	
	protected void sendAsync(final Peer peer, final Message message) {
		try {
			peer.getTransporter().sendAsync(message);
		} catch (final STPException exception) {
			throw new CommandProtocolException(exception);
		}
	}
	
	protected void sendSync(final Peer peer, final String name, final String... values) {
		sendAsync(peer, new Command(name, values));
	}
	
	protected void sendSync(final Peer peer, final Command command) {
		sendSync(peer, CommanProtocol.parse(getType(), command));
	}
	
	protected void sendSync(final Peer peer, final Message message) {
		try {
			peer.getTransporter().sendSync(message);
		} catch (final STPException exception) {
			throw new CommandProtocolException(exception);
		}
	}
	
	@Override
	protected void read(final Peer peer, final Message message) {
		super.read(peer, message);
		
		read(peer, CommanProtocol.parse(message));
	}
	
	protected void read(final Peer peer, final Command command) {
		if (readEvents.containsKey(command.getName())) {
			readEvents.get(command.getName()).run(peer, command);
		}
	}
	
	@Override
	protected void written(Peer peer, Message message) {
		super.written(peer, message);
		
		written(peer, CommanProtocol.parse(message));
	}
	
	protected void written(final Peer peer, final Command command) {
		if (writtenEvents.containsKey(command.getName())) {
			writtenEvents.get(command.getName()).run(peer, command);
		}
	}
	
	protected <T extends Service> T getService(final Class<T> serviceClass) {
		return ModelApp.getModelAppConfiguration().getServiceFactory().getService(serviceClass, ModelApp.getModelAppConfiguration().getRepositoryFactory().getEntityManager("MMO"));
	}
	
	protected void onRead(final String commandName, final CommandRunnable commandRunnable) {
		readEvents.put(commandName, commandRunnable);
	}
	
	protected void onWritten(final String commandName, final CommandRunnable commandRunnable) {
		writtenEvents.put(commandName, commandRunnable);
	}
}
