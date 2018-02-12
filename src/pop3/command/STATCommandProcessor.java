package pop3.command;

import pop3.Maildrop;
import pop3.Server;
import pop3.SessionState;

public class STATCommandProcessor extends CommandProcessor {

	public STATCommandProcessor(Server server) {
		super("STAT", server);
	}

	@Override
	public void process(String command, ClientSessionState session) {
		mSession = session;

		if (mSession.mState != SessionState.TRANSACTION) {
			mResponse.setResponse(false, "STAT command can only be used in TRANSACTION state");
			return;
		}
		
		// success
		Maildrop mail = mServer.getUserMaildrop(mSession.mUser);
		mResponse.setResponse(true, mail.getMessageCount() + " " + mail.getMailSize());
	}

}
