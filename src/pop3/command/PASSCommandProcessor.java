package pop3.command;

import pop3.Server;
import pop3.SessionState;
import pop3.Maildrop;


public class PASSCommandProcessor extends CommandProcessor {

	public PASSCommandProcessor(Server server) {
		super(server);
	}

	@Override
	public void process(String command, CommandArgs args) {
		mArgs = args;
		if (mArgs.mState != SessionState.AUTHORIZATION) {
			mResponse.setResponse(false, "PASS command can only be used in AUTHORIZATION state");
			return;
		}
		
		if (!mServer.hasUser(mArgs.mUser)) {
			mResponse.setResponse(false, "login first");
			return;
		}
		
		String password = String.join("", CommandParser.getCommandArgs(command));
		String actualPassword = mServer.getUserPassword(mArgs.mUser);
		
		// TODO: handle actualPassword == null
		if (actualPassword == null || !actualPassword.equals(password)) {
			mResponse.setResponse(false, "invalid password for user " + mArgs.mUser);
			return;
		}
		
		Maildrop userMaildrop = mServer.getUserMaildrop(mArgs.mUser);
		if (userMaildrop == null) {
			mResponse.setResponse(false, "maildrop for user " + mArgs.mUser + " not found");
			return;
		} else if (userMaildrop.isLocked()) {
			mResponse.setResponse(false, "maildrop is already locked");
			return;
		}
		
		// success
		userMaildrop.lock();
		mArgs.mState = SessionState.TRANSACTION;
		mResponse.setResponse(true, "maildrop for user " + mArgs.mUser + " successfully locked");
	}

}
