package pop3.command;

import pop3.Maildrop;
import pop3.Server;
import pop3.SessionState;

public class DELECommandProcessor extends CommandProcessor {

	public DELECommandProcessor(Server server) {
		super(server);
	}

	@Override
	public void process(String command, ClientSessionState session) {
		mSession = session;
		
		if (mSession.mState != SessionState.TRANSACTION) {
			mResponse.setResponse(false, "DELE command can only be used in TRANSACTION state");
			return;
		}
		
		String[] commandArgs = CommandParser.getCommandArgs(command);
		if (commandArgs.length != 1) {
			mResponse.setResponse(false, "message index is not specified");
			return;
		}
		
		// TODO: catch NumberFormatException
		int msgIndex = Integer.parseInt(String.join("", commandArgs));
		Maildrop mail = mServer.getUserMaildrop(mSession.mUser);
		
		if (!mail.isValidIndex(msgIndex)) {
			mResponse.setResponse(false, "no such message");
			return;
		}
		
		if (mail.isMessageMarked(msgIndex)) {
			mResponse.setResponse(false, "message is already marked for deletion");
			return;
		}
		
		// success
		mail.markMessageToDelete(msgIndex);
		mResponse.setResponse(true, "message marked for deletion");
	}

}
