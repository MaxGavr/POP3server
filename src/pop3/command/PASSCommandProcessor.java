package pop3.command;

import pop3.Server;
import pop3.SessionState;
import pop3.Maildrop;


public class PASSCommandProcessor extends CommandProcessor {

	public PASSCommandProcessor(Server server) {
		super("PASS", server);
	}

	@Override
	public void process(String command, ClientSessionState session) {
		mSession = session;
		
		if (mSession.mState != SessionState.AUTHORIZATION) {
			mResponse.setResponse(false, "PASS command can only be used in AUTHORIZATION state");
			return;
		}
		
		if (!mServer.hasUser(mSession.mUser)) {
			mResponse.setResponse(false, "login first");
			return;
		}
		
		String password = String.join("", CommandParser.getCommandArgs(command));
		if (password.isEmpty()) {
			mResponse.setResponse(false, "specify password");
			return;
		}
		
		String actualPassword = mServer.getUserPassword(mSession.mUser);
		if (!actualPassword.equals(password)) {
			mResponse.setResponse(false, "invalid password for user " + mSession.mUser);
			return;
		}
		
		Maildrop userMaildrop = mServer.getUserMaildrop(mSession.mUser);
		if (userMaildrop == null) {
			mResponse.setResponse(false, "maildrop for user " + mSession.mUser + " not found");
			return;
		} else if (userMaildrop.isLocked()) {
			mResponse.setResponse(false, "maildrop is already locked");
			return;
		}
		
		// success
		userMaildrop.lock();
		mSession.mState = SessionState.TRANSACTION;
		mResponse.setResponse(true, "maildrop for user " + mSession.mUser + " successfully locked");
	}

}
