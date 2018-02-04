package pop3.command;

import pop3.Server;
import pop3.SessionState;



public class USERCommandProcessor extends CommandProcessor {

	public USERCommandProcessor(Server server, CommandArgs args) {
		super(server, args);
	}

	
	@Override
	public void process(String command) {
		if (mArgs.mState != SessionState.AUTHORIZATION) {
			mResponse.setResponse(false, "USER command can only be used in AUTHORIZATION state");
			return;
		}
		
		String user = CommandValidator.getCommandArgs(command);
	
		if (!mServer.hasUser(user)) {
			mResponse.setResponse(false, "user " + mArgs.mUser + " is not registered");
			return;
		}
		
		mArgs.mUser = user;
		mResponse.setResponse(true, "user " + user + " found");
	}
}
