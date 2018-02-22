package pop3.command;

import java.util.ArrayList;

import pop3.Maildrop;
import pop3.Server;
import pop3.SessionState;



public class RETRCommandProcessor implements ICommandProcessor {

	private Server server;

	
	public RETRCommandProcessor(Server server) {
		this.server = server;
	}

	
	@Override
	public POP3Response process(CommandState state) {

		if (state.getSessionState() != SessionState.TRANSACTION) {
			return new POP3Response(false, "RETR command can only be used in TRANSACTION state");
		}

		String commandArgs[] = CommandParser.getCommandArgs(state.getCommand());
		if (commandArgs.length == 0) {
			return new POP3Response(false, "no message is specified");
		} else if (commandArgs.length > 1) {
			return new POP3Response(false, "too much arguments");
		} else {
			int msgIndex = 0;
			try {
				msgIndex = Integer.parseInt(commandArgs[0]);
			} catch (NumberFormatException e) {
				return new POP3Response(false, "invalid message index");
			}

			Maildrop mail = server.getUserMaildrop(state.getUser());
			
			if (!mail.isValidIndex(msgIndex)) {
				return new POP3Response(false, "no such message");
			}
			
			if (mail.isMessageMarked(msgIndex)) {
				return new POP3Response(false, "message is marked for deletion");
			}

			String msg = mail.getMessage(msgIndex);
			return new POP3Response(true, splitMessageIntoLines(msg));
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
