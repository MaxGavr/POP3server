package pop3.command;

import pop3.Maildrop;
import pop3.Server;
import pop3.SessionState;

public class LISTCommandProcessor extends CommandProcessor {

	public LISTCommandProcessor(Server server) {
		super("LIST", server);
	}

	@Override
	public void process(String command, ClientSessionState session) {
		mSession = session;
		
		if (mSession.mState != SessionState.TRANSACTION) {
			mResponse.setResponse(false, "LIST command can only be used in TRANSACTION state");
			return;
		}
		
		Maildrop mail = mServer.getUserMaildrop(mSession.mUser);
		
		String[] commandArgs = CommandParser.getCommandArgs(command);
		if (commandArgs.length > 1){ 
			mResponse.setResponse(false, "too much arguments");
		} else if (commandArgs.length == 1) {
			
			int msgIndex = 0;
			try {
				msgIndex = Integer.parseInt(String.join("", commandArgs));
			} catch (NumberFormatException e) {
				mResponse.setResponse(false, "invalid message index");
				return;
			}
			
			if (!mail.isValidIndex(msgIndex)) {
				mResponse.setResponse(false, "no such message");
			} else if (mail.isMessageMarked(msgIndex)) {
				mResponse.setResponse(false, "message is marked for deletion");
			} else {
				mResponse.setResponse(true, msgIndex + " " + mail.getMessageSize(msgIndex));
			}

		} else {
			mResponse.clearArgs();
			mResponse.setPositive(true);
			for (int msgIndex = 0; msgIndex < mail.getMessageCount(); ++msgIndex) {
				mResponse.addArg((msgIndex + 1) + " " + mail.getMessageSize(msgIndex + 1));
			}
		}
	}

}
