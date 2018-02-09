package pop3.command;

import pop3.Server;
import pop3.SessionState;

public class QUITCommandProcessor extends CommandProcessor {

	public QUITCommandProcessor(Server server) {
		super(server);
	}

	@Override
	public void process(String command, CommandArgs args) {
		mArgs = args;
		
		if (mArgs.mState == SessionState.AUTHORIZATION) {
			if (!mArgs.mUser.isEmpty()) {
				mArgs.mUser = new String();
			}
			
			mArgs.mCloseConnection = true;
			mResponse.setResponse(true, "signing off");
		}
	}

}
