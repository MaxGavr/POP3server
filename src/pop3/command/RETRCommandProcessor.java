package pop3.command;

import java.util.ArrayList;

import pop3.Server;
import pop3.SessionState;



public class RETRCommandProcessor extends CommandProcessor {

	public RETRCommandProcessor(Server server) {
		super(server);
	}

	
	@Override
	public void process(String command, ClientSessionState session) {
		mSession = session;

		if (mSession.mState != SessionState.TRANSACTION) {
			mResponse.setResponse(false, "RETR command can only be used in TRANSACTION state");
			return;
		}

		String commandArgs[] = CommandParser.getCommandArgs(command);
		if (commandArgs.length == 0) {
			mResponse.setResponse(false, "no message is specified");
			return;
		} else if (commandArgs.length > 1) {
			mResponse.setResponse(false, "too much arguments");
			return;
		} else {
			// TODO: catch NumberFormatException
			int msgIndex = Integer.parseInt(commandArgs[0]);
			String msg = mServer.getUserMaildrop(mSession.mUser).getMessage(msgIndex);

			mResponse.clearArgs();
			mResponse.setPositive(true);
			mResponse.setArgs(splitMessageIntoLines(msg));
		}

	}
	
	private String[] splitMessageIntoLines(String msg) {
		int maxLength = CommandParser.getLineMaxLength();
		
		ArrayList<String> lines = new ArrayList<String>();
		int i = 0;
		while (i < msg.length()) {
			lines.add(msg.substring(i, Math.min(i + maxLength, msg.length())));
			i += maxLength;
		}
		
		return lines.toArray(new String[0]);
	}

}
