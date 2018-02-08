package pop3.command;

import pop3.Server;
import pop3.SessionState;



public class USERCommandProcessor extends CommandProcessor {

	public USERCommandProcessor(Server server) {
		super(server);
	}

	
	@Override
	public void process(String command, CommandArgs args) {
		if (mArgs.mState != SessionState.AUTHORIZATION) {
			mResponse.setResponse(false, "USER command can only be used in AUTHORIZATION state");
			return;
		}
		
		String arguments[] = CommandParser.getCommandArgs(command);
		String user = arguments[0];
	
		if (!mServer.hasUser(user)) {
			mResponse.setResponse(false, "user " + mArgs.mUser + " is not registered");
			return;
		}
		
		// success
		mArgs.mUser = user;
		mResponse.setResponse(true, "user " + user + " found");
	}
}