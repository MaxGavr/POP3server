package gui;

import java.util.Observable;
import java.util.Observer;

import pop3.Server;
import pop3.ServerEvent;
import pop3.ServerEvent.EventType;



public class ServerObserver implements Observer {
	
	private AppWindow window;
	
	
	public ServerObserver(Server server) {
		server.addObserver(this);
		window = null;
	}

	
	@Override
	public void update(Observable server, Object event) {
		ServerEvent serverEvent = (ServerEvent) event;
	}

	public String getServerEventLog(ServerEvent event) {
		
		String eventLog = "";
		
		switch (event.getType()) {
		
		case SERVER_STARTED:
			eventLog = "Server started";
			break;
			
		case SERVER_STOPPED:
			eventLog = "Server stopped";
			break;
			
		case ACCEPT_CLIENT:
			eventLog = "Accept client " + event.getArgs().get(0);
			break;
			
		case DISCONNECT_CLIENT:
			eventLog = "Disconnect client " + event.getArgs().get(0);
			break;
			
		case REGISTER_COMMAND:
			eventLog = "Register command \"" + event.getArgs().get(0) + "\" for client " + event.getArgs().get(1);
			break;
			
		case COMMAND_RECEIVED:
			eventLog = "Received command from " + event.getArgs().get(0) + "\n---\n" + event.getArgs().get(1) + "---";
			break;
			
		case RESPONSE_SENT:
			eventLog = "Send response to " + event.getArgs().get(0) + "\n---\n" + event.getArgs().get(1) + "---";
			break;

		default:
			break;
		}
		
		return eventLog;
	}

	public void setWindow(AppWindow window) {
		this.window = window;
	}
}
