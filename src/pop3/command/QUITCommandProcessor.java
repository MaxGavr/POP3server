package pop3.command;

import pop3.Maildrop;
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
				mSession.mUser = "";
			}
			
			mResponse.setResponse(true, "POP3 server signing off");
			
		} else if (mSession.mState == SessionState.TRANSACTION) {
			mSession.mState = SessionState.UPDATE;
			
			Maildrop mail = mServer.getUserMaildrop(mSession.mUser);
			if (mail.deleteMarkedMessages()) {
				mResponse.setResponse(true, "POP3 server signing off (" + mail.getMailSize() + " messages left)");
			} else {
				mResponse.setResponse(false, "fail to delete some messages");
			}
			
			mSession.mUser = "";
			mail.unlock();
		}
		
		mSession.mCloseConnection = true;
	}

}
