package pop3.command;

import pop3.Maildrop;
import pop3.Server;
import pop3.SessionState;

public class UIDLCommandProcessor extends CommandProcessor {

	public UIDLCommandProcessor(Server server) {
		super(server);
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
		
		if (commandArgs.length > 0) {
			// TODO: catch NumberFormatException
			int msgIndex = Integer.parseInt(String.join("", commandArgs));
			
			if (!mail.isValidIndex(msgIndex)) {
				mResponse.setResponse(false, "no such message");
				return;
			}
			
			if (mail.isMessageMarked(msgIndex)) {
				mResponse.setResponse(false, "message marked for deletion");
				return;
			}
			
			mResponse.setResponse(true, msgIndex + " " + mail.getMessage(msgIndex).hashCode());
			
		} else {
			mResponse.clearArgs();
			mResponse.setPositive(true);
			for (int msgIndex = 1; msgIndex <= mail.getMessageCount(); ++msgIndex) {
				mResponse.addArg(msgIndex + " " + mail.getMessage(msgIndex).hashCode());
			}
		}
	}

}
