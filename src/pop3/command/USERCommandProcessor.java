package pop3.command;

import pop3.Server;
import pop3.SessionState;



public class USERCommandProcessor implements ICommandProcessor {

	private Server server;
	
	
	public USERCommandProcessor(Server server) {
		this.server = server;
	}
	

	@Override
	public POP3Response process(ClientSessionState state) {
		if (state.getSessionState() != SessionState.AUTHORIZATION) {
			return new POP3Response(false, "USER command can only be used in AUTHORIZATION state");
		}
		
		String commandArgs[] = CommandParser.getCommandArgs(state.getCommand());
		String user = String.join("", commandArgs);
	
		if (!server.hasUser(user)) {
			return new POP3Response(false, "user " + user + " is not registered");
		}
		
		if (server.getUserMaildrop(user).isLocked()) {
			return new POP3Response(false, "user " + user + " already signed in");
		}
		
		// success
		state.setUser(user);
		return new POP3Response(true, "user " + user + " found");
	}
}
