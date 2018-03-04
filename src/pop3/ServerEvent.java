package pop3;

import java.util.ArrayList;
import java.util.List;



public class ServerEvent {

	public enum EventType {
		SERVER_STARTED,
		SERVER_STOPPED,
		COMMAND_RECEIVED,
		RESPONSE_SENT,
		ACCEPT_CLIENT,
		REGISTER_COMMAND,
		DISCONNECT_CLIENT
	}


	private EventType type;
	private List<String> args;


	public ServerEvent(EventType type) {
		setType(type);
		setArgs(new ArrayList<String>());
	}

	public ServerEvent(EventType type, List<String> args) {
		setType(type);
		setArgs(args);
	}
	
	public ServerEvent(EventType type, String arg) {
		setType(type);
		setArgs(new ArrayList<String>());
		addArg(arg);
	}

	public EventType getType() {
		return type;
	}

	public void setType(EventType type) {
		this.type = type;
	}

	public List<String> getArgs() {
		return args;
	}

	public void setArgs(List<String> args) {
		this.args = args;
	}
	
	public void addArg(String arg) {
		args.add(arg);
	}
}
