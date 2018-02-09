package pop3.command;

import pop3.Maildrop;
import pop3.Server;
import pop3.SessionState;

public class LISTCommandProcessor extends CommandProcessor {

	public LISTCommandProcessor(Server server) {
		super(server);
	}

	@Override
	public void process(String command, CommandArgs args) {
		mArgs = args;
		
		if (mArgs.mState != SessionState.TRANSACTION) {
			mResponse.setResponse(false, "LIST command can only be used in TRANSACTION state");
			return;
		}
		
		Maildrop mail = mServer.getUserMaildrop(mArgs.mUser);
		
		String[] commandArgs = CommandParser.getCommandArgs(command);
		if (commandArgs.length > 0) {
			// TODO: catch NumberFormatException
			int msgIndex = Integer.parseInt(String.join("", commandArgs));
			
			if (!mail.isValidIndex(msgIndex)) {
				mResponse.setResponse(false, "no such message");
				return;
			} else if (mail.isMessageMarked(msgIndex)) {
				mResponse.setResponse(false, "message is marked for deletion");
				return;
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
