package pop3;

import java.time.LocalDateTime;
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
	private LocalDateTime timeStamp;


	public ServerEvent(EventType type, LocalDateTime time) {
		setType(type);
		setArgs(new ArrayList<String>());
		setTimeStamp(time);
	}

	public ServerEvent(EventType type, List<String> args, LocalDateTime time) {
		setType(type);
		setArgs(args);
		setTimeStamp(time);
	}
	
	public ServerEvent(EventType type, String arg, LocalDateTime time) {
		setType(type);
		setArgs(new ArrayList<String>());
		addArg(arg);
		setTimeStamp(time);
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

	public LocalDateTime getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(LocalDateTime timeStamp) {
		this.timeStamp = timeStamp;
	}
}
