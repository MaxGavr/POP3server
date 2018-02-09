package pop3.command;

import pop3.Maildrop;
import pop3.Server;
import pop3.SessionState;

public class STATCommandProcessor extends CommandProcessor {

	public STATCommandProcessor(Server server) {
		super(server);
	}

	@Override
	public void process(String command, CommandArgs args) {
		mArgs = args;
		if (mArgs.mState != SessionState.TRANSACTION) {
			mResponse.setResponse(false, "STAT command can only be used in TRANSACTION state");
			return;
		}
		
		// success
		Maildrop mail = mServer.getUserMaildrop(mArgs.mUser);
		mResponse.setResponse(true, mail.getMessageCount() + " " + mail.getMailSize());
	}

}
