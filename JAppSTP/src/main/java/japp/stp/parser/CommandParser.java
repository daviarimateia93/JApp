package japp.stp.parser;

import java.util.HashMap;
import java.util.Map;

import japp.model.ModelApp;
import japp.model.service.Service;
import japp.stp.NetworkingProtocolException;
import japp.stp.CommanProtocol;
import japp.stp.CommanProtocol.Command;
import stp.gateway.Peer;
import stp.message.Message;
import stp.parser.Parser;
import stp.system.STPException;

public abstract class CommandParser extends Parser {
	
	private Map<String, Runnable> events = new HashMap<>();
	
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
			throw new NetworkingProtocolException(exception);
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
			throw new NetworkingProtocolException(exception);
		}
	}
	
	@Override
	protected void read(final Peer peer, final Message message) {
		super.read(peer, message);
		
		read(peer, CommanProtocol.parse(message));
	}
	
	protected void read(final Peer peer, final Command command) {
		
	}
	
	@Override
	protected void written(Peer peer, Message message) {
		super.written(peer, message);
		
		written(peer, CommanProtocol.parse(message));
	}
	
	protected void written(final Peer peer, final Command command) {
		
	}
	
	protected <T extends Service> T getService(final Class<T> serviceClass) {
		return ModelApp.getModelAppConfiguration().getServiceFactory().getService(serviceClass, ModelApp.getModelAppConfiguration().getRepositoryFactory().getEntityManager("MMO"));
	}
	
	protected void on(final String commandName, final Runnable runnable) {
		events.put(commandName, runnable);
	}
}
