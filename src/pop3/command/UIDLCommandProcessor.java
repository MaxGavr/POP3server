package pop3.command;


import pop3.Maildrop;
import pop3.Server;
import pop3.SessionState;


public class UIDLCommandProcessor implements ICommandProcessor {

	private Server server;

	
	public UIDLCommandProcessor(Server server) {
		this.server = server;
	}

	
	@Override
	public POP3Response process(CommandState state) {
		
		if (state.getSessionState() != SessionState.TRANSACTION) {
			return new POP3Response(false, "UIDL command can only be used in TRANSACTION state");
		}
		
		String[] commandArgs = CommandParser.getCommandArgs(state.getCommand());
		Maildrop mail = server.getUserMaildrop(state.getUser());
		
		if (commandArgs.length > 1) {
			return new POP3Response(false, "too much arguments");
		} else if (commandArgs.length == 1) {
			int msgIndex = 0;

			try {
				msgIndex = Integer.parseInt(commandArgs[0]);	
			} catch (NumberFormatException e) {
				return new POP3Response(false, "invalid message index");
			}
			
			if (!mail.isValidIndex(msgIndex)) {
				return new POP3Response(false, "no such message");
			}
			
			if (mail.isMessageMarked(msgIndex)) {
				return new POP3Response(false, "message marked for deletion");
			}
			
			// success
			return new POP3Response(true, msgIndex + " " + mail.getMessage(msgIndex).hashCode());
			
		} else {
			// success
			POP3Response response = new POP3Response(true);
			
			for (int msgIndex = 1; msgIndex <= mail.getMessageCount(); ++msgIndex) {
				response.addArg(msgIndex + " " + mail.getMessage(msgIndex).hashCode());
			}
			
			return response;
		}
	}

}
