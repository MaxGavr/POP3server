package gui;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Observable;
import java.util.Observer;

import pop3.Server;
import pop3.ServerEvent;



public class ServerObserver implements Observer {
	
	private AppWindow view;
	private Server server;
	
	
	public ServerObserver(Server server) {
		this.server = server;
		if (server != null) {
			server.addObserver(this);
		}
		
		view = null;
	}

	
	public void startServer() {
		Thread serverThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					server.loadUsers("users.txt");
					server.loadMail("mail");
					server.start();
				} catch (IOException e) {
					view.logServerMessage(e.getMessage());
				}
			}
		});
		
		serverThread.start();
	}
	
	@Override
	public void update(Observable server, Object event) {
		ServerEvent serverEvent = (ServerEvent) event;
		
		if (view != null) {
			view.logServerMessage(getServerEventLog(serverEvent));
		}
	}

	public String getServerEventLog(ServerEvent event) {
		
		DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
		String time = timeFormat.format(event.getTimeStamp());
		
		String eventLog = time + ": ";
		
		switch (event.getType()) {
		
		case SERVER_STARTED:
			eventLog += "Server started";
			break;
			
		case SERVER_STOPPED:
			eventLog += "Server stopped";
			break;
			
		case ACCEPT_CLIENT:
			eventLog += "Accept client " + event.getArgs().get(0);
			break;
			
		case DISCONNECT_CLIENT:
			eventLog += "Disconnect client " + event.getArgs().get(0);
			break;
			
		case REGISTER_COMMAND:
			eventLog += "Register command \"" + event.getArgs().get(1) + "\" for client " + event.getArgs().get(0);
			break;
			
		case COMMAND_RECEIVED:
			eventLog += "Received command \"" + event.getArgs().get(1) + "\" from " + event.getArgs().get(0);
			break;
			
		case RESPONSE_SENT:
			eventLog += "Send response to " + event.getArgs().get(0) + "\n---\n" + event.getArgs().get(1) + "---";
			break;

		default:
			break;
		}
		
		return eventLog;
	}

	public void setWindow(AppWindow window) {
		this.view = window;
	}
	
	public boolean isServerRunning() {
		return server.isRunning();
	}
	
	public void stopServer() {
		server.stop();
	}
}
