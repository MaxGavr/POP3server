package pop3.command;

import pop3.Server;
import pop3.SessionState;

public class QUITCommandProcessor extends CommandProcessor {

	public QUITCommandProcessor(Server server) {
		super(server);
	}

	@Override
	public void process(String command, ClientSessionState session) {
		mSession = session;
		
		if (mSession.mState == SessionState.AUTHORIZATION) {
			if (!mSession.mUser.isEmpty()) {
				mSession.mUser = new String();
			}
			
			mSession.mCloseConnection = true;
			mResponse.setResponse(true, "signing off");
		}
	}

}
