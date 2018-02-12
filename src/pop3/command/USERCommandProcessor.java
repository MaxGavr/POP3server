package pop3.command;

import pop3.Server;
import pop3.SessionState;



public class USERCommandProcessor extends CommandProcessor {

	public USERCommandProcessor(Server server) {
		super("USER", server);
	}

	
	@Override
	public void process(String command, ClientSessionState session) {
		mSession = session;

		if (mSession.mState != SessionState.AUTHORIZATION) {
			mResponse.setResponse(false, "USER command can only be used in AUTHORIZATION state");
			return;
		}
		
		String commandArgs[] = CommandParser.getCommandArgs(command);
		String user = String.join("", commandArgs);
	
		if (!mServer.hasUser(user)) {
			mResponse.setResponse(false, "user " + user + " is not registered");
			return;
		}
		
		if (mServer.getUserMaildrop(user).isLocked()) {
			mResponse.setResponse(false, "user " + user + " already signed in");
			return;
		}
		
		// success
		mSession.mUser = user;
		mResponse.setResponse(true, "user " + user + " found");
	}
}
