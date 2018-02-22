package pop3.command;

import pop3.Maildrop;
import pop3.Server;
import pop3.SessionState;

public class DELECommandProcessor implements ICommandProcessor {

	private Server server;

	
	public DELECommandProcessor(Server server) {
		this.server = server;
	}

	
	@Override
	public POP3Response process(ClientSessionState state) {
	
		if (state.getSessionState() != SessionState.TRANSACTION) {
			return new POP3Response(false, "DELE command can only be used in TRANSACTION state");
		}
		
		String[] commandArgs = CommandParser.getCommandArgs(state.getCommand());
		if (commandArgs.length == 0) {
			return new POP3Response(false, "message index is not specified");
		} else if (commandArgs.length > 1) {
			return new POP3Response(false, "too much arguments");
		}
		
		int msgIndex = 0;
		try {
			msgIndex = Integer.parseInt(String.join("", commandArgs));
		} catch (NumberFormatException e) {
			return new POP3Response(false, "invalid message index");
		}
		
		Maildrop mail = server.getUserMaildrop(state.getUser());
		
		if (!mail.isValidIndex(msgIndex)) {
			return new POP3Response(false, "no such message");
		}
		
		if (mail.isMessageMarked(msgIndex)) {
			return new POP3Response(false, "message is already marked for deletion");
		}
		
		// success
		mail.markMessageToDelete(msgIndex);
		return new POP3Response(true, "message marked for deletion");
	}

}
