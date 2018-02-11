package pop3.command;

import pop3.Server;
import pop3.SessionState;

public class NOOPCommandProcessor extends CommandProcessor {

	public NOOPCommandProcessor(Server server) {
		super(server);
	}

	@Override
	public void process(String command, ClientSessionState session) {
		mSession = session;
		
		if (mSession.mState != SessionState.TRANSACTION) {
			mResponse.setResponse(false, "NOOP command can only be used in TRANSACTION state");
			return;
		}
		
		mResponse.setResponse(false, "");
	}

}
