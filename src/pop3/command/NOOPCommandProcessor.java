package pop3.command;

import pop3.Server;
import pop3.SessionState;

public class NOOPCommandProcessor extends CommandProcessor {

	public NOOPCommandProcessor(Server server) {
		super("NOOP", server);
	}

	@Override
	public void process(String command, ClientSessionState session) {
		mSession = session;
		
		if (mSession.mState != SessionState.TRANSACTION) {
			mResponse.setResponse(false, "NOOP command can only be used in TRANSACTION state");
			return;
		}
		
		mResponse.clearArgs();
		mResponse.setPositive(true);
	}

}
