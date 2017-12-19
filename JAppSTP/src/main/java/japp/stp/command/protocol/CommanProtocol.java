package japp.stp.command.protocol;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import japp.stp.command.Command;
import stp.core.STPObject;
import stp.message.Message;
import stp.message.Payload;

public class CommanProtocol extends STPObject {
	
	public static Message parse(final String type, final Command command) {
		try {
			final StringBuilder stringBuilder = new StringBuilder();
			
			if (command.getName() != null) {
				stringBuilder.append(encode(command.getName()));
				stringBuilder.append(";");
			}
			
			if (command.getValues() != null) {
				for (String value : command.getValues()) {
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
