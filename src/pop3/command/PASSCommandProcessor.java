package pop3.command;

import pop3.SessionState;
import pop3.Maildrop;
import pop3.Server;


public class PASSCommandProcessor implements ICommandProcessor {

	private Server server;

	
	public PASSCommandProcessor(Server server) {
		this.server = server;
	}
	

	@Override
	public POP3Response process(CommandState state) {
		
		if (state.getSessionState() != SessionState.AUTHORIZATION) {
			return new POP3Response(false, "PASS command can only be used in AUTHORIZATION state");
		}
		
		if (!server.hasUser(state.getUser())) {
			return new POP3Response(false, "login first");
		}
		
		String password = String.join("", CommandParser.getCommandArgs(state.getCommand()));
		if (password.isEmpty()) {
			return new POP3Response(false, "specify password");
		}
		
		String actualPassword = server.getUserPassword(state.getUser());
		if (!actualPassword.equals(password)) {
			return new POP3Response(false, "invalid password for user " + state.getUser());
		}
		
		Maildrop userMaildrop = server.getUserMaildrop(state.getUser());
		if (userMaildrop == null) {
			return new POP3Response(false, "maildrop for user " + state.getUser() + " not found");
		} else if (userMaildrop.isLocked()) {
			return new POP3Response(false, "maildrop is already locked");
		}
		
		// success
		userMaildrop.lock();
		state.setSessionState(SessionState.TRANSACTION);
		return new POP3Response(true, "maildrop for user " + state.getUser() + " successfully locked");
	}
}
