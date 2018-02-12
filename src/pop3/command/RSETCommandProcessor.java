package pop3.command;

import pop3.Maildrop;
import pop3.Server;
import pop3.SessionState;

public class RSETCommandProcessor extends CommandProcessor {

	public RSETCommandProcessor(Server server) {
		super("RSET", server);
	}

	@Override
	public void process(String command, ClientSessionState session) {
		mSession = session;
		
		if (mSession.mState != SessionState.TRANSACTION) {
			mResponse.setResponse(false, "RSET commmand can only be used in TRANSACTION state");
			return;
		}
		
		Maildrop mail = mServer.getUserMaildrop(mSession.mUser);
		
		for (Integer msgIndex : mail.getMarkedMessages()) {
			mail.unmarkMessageToDelete(msgIndex);
		}
		
		mResponse.setResponse(true, "all messages saved");
	}
}
