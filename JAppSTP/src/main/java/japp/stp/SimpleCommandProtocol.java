package japp.stp;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import stp.message.Message;
import stp.message.Payload;
import stp.system.STPObject;

public class SimpleCommandProtocol extends STPObject {
	
	public static class Command {
		private String name;
		private String[] values;
		
		public Command(final String name, final String... values) {
			this.name = name;
			this.values = values;
		}
		
		public String getName() {
			return name;
		}
		
		public String[] getValues() {
			return values;
		}
		
		@Override
		public String toString() {
			final StringBuilder stringBuilder = new StringBuilder();
			
			stringBuilder.append(name + ";");
			
			for (final String value : values) {
				stringBuilder.append(value + ";");
			}
			
			return stringBuilder.toString();
		}
	}
	
	public static Message parse(final String type, final Command command) {
		try {
			final StringBuilder stringBuilder = new StringBuilder();
			
			if (command.name != null) {
				stringBuilder.append(encode(command.name));
				stringBuilder.append(";");
			}
			
			if (command.values != null) {
				for (String value : command.values) {
					stringBuilder.append(encode(value));
					stringBuilder.append(";");
				}
			}
			
			final byte[] bytes = stringBuilder.toString().getBytes("UTF-8");
			
			final Payload payload = new Payload();
			payload.setContent(bytes);
			payload.setPosition(0);
			payload.setLength(bytes.length);
			payload.setTotalLength(bytes.length);
			
			final Message message = new Message();
			message.setType(type);
			message.setPayload(payload);
			
			return message;
		} catch (final UnsupportedEncodingException unsupportedEncodingException) {
			return null;
		}
	}
	
	public static Command parse(final Message message) {
		try {
			final String string = new String(message.getPayload().getContent(), "UTF-8");
			final String[] fragments = string.split("(?<!\\\\);");
			final String name = fragments[0];
			final List<String> values = new ArrayList<>();
			
			if (fragments.length > 1) {
				for (int i = 1; i < fragments.length; i++) {
					values.add(decode(fragments[i]));
				}
			}
			
			return new Command(name, values.toArray(new String[values.size()]));
		} catch (final UnsupportedEncodingException exception) {
			return null;
		}
	}
	
	private static String encode(final String string) {
		return string.replaceAll("\\;", "\\\\;");
	}
	
	private static String decode(final String string) {
		return string.replaceAll("\\\\;", "\\;");
	}
}
