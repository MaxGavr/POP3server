package pop3.command;

import pop3.SessionState;



public class CommandState {
	
	private String command;
	
	private String user;
	private SessionState sessionState;
	private boolean closeConnection;
	
	
	public CommandState() {
		command = "";
		user = "";
		sessionState = SessionState.AUTHORIZATION;
		closeConnection = false;
	}
	
	
	public String getCommand() {
		return command;
	}
	
	public void setCommand(String command) {
		this.command = command;
	}
	
	public String getUser() {
		return user;
	}
	
	public void setUser(String user) {
		this.user = user;
	}
	
	public SessionState getSessionState() {
		return sessionState;
	}
	
	public void setSessionState(SessionState sessionState) {
		this.sessionState = sessionState;
	}
	
	public boolean isCloseConnection() {
		return closeConnection;
	}
	
	public void setCloseConnection(boolean close) {
		this.closeConnection = close;
	}
}
