package pop3.command;

import pop3.Maildrop;
import pop3.Server;
import pop3.SessionState;

public class UIDLCommandProcessor extends CommandProcessor {

	public UIDLCommandProcessor(Server server) {
		super("UIDL", server);
	}

	@Override
	public void process(String command, ClientSessionState session) {
		mSession = session;
		
		if (mSession.mState != SessionState.TRANSACTION) {
			mResponse.setResponse(false, "UIDL command can only be used in TRANSACTION state");
			return;
		}
		
		String[] commandArgs = CommandParser.getCommandArgs(command);
		Maildrop mail = mServer.getUserMaildrop(mSession.mUser);
		
		if (commandArgs.length > 1) {
			mResponse.setResponse(false, "too much arguments");
			return;
		} else if (commandArgs.length == 1) {
			int msgIndex = 0;

			try {
				msgIndex = Integer.parseInt(commandArgs[0]);	
			} catch (NumberFormatException e) {
				mResponse.setResponse(false, "invalid message index");
				return;
			}
			
			if (!mail.isValidIndex(msgIndex)) {
				mResponse.setResponse(false, "no such message");
				return;
			}
			
			if (mail.isMessageMarked(msgIndex)) {
				mResponse.setResponse(false, "message marked for deletion");
				return;
			}
			
			// success
			mResponse.setResponse(true, msgIndex + " " + mail.getMessage(msgIndex).hashCode());
			
		} else {
			// success
			mResponse.clearArgs();
			mResponse.setPositive(true);
			for (int msgIndex = 1; msgIndex <= mail.getMessageCount(); ++msgIndex) {
				mResponse.addArg(msgIndex + " " + mail.getMessage(msgIndex).hashCode());
			}
		}
	}

}
