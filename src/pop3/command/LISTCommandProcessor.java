package pop3.command;

import pop3.Maildrop;
import pop3.Server;
import pop3.SessionState;



public class LISTCommandProcessor implements ICommandProcessor {

	private Server server;

	
	public LISTCommandProcessor(Server server) {
		this.server = server;
	}

	
	@Override
	public POP3Response process(CommandState state) {
		POP3Response response = new POP3Response();
		
		if (state.getSessionState() != SessionState.TRANSACTION) {
			response.setResponse(false, "LIST command can only be used in TRANSACTION state");
			return response;
		}
		
		Maildrop mail = server.getUserMaildrop(state.getUser());
		
		String[] commandArgs = CommandParser.getCommandArgs(state.getCommand());
		if (commandArgs.length > 1){ 
			response.setResponse(false, "too much arguments");
		} else if (commandArgs.length == 1) {
			
			int msgIndex = 0;
			try {
				msgIndex = Integer.parseInt(String.join("", commandArgs));
			} catch (NumberFormatException e) {
				response.setResponse(false, "invalid message index");
				return response;
			}
			
			if (!mail.isValidIndex(msgIndex)) {
				response.setResponse(false, "no such message");
			} else if (mail.isMessageMarked(msgIndex)) {
				response.setResponse(false, "message is marked for deletion");
			} else {
				response.setResponse(true, msgIndex + " " + mail.getMessageSize(msgIndex));
			}

		} else {
			response.clearArgs();
			response.setPositive(true);
			for (int msgIndex = 0; msgIndex < mail.getMessageCount(); ++msgIndex) {
				response.addArg((msgIndex + 1) + " " + mail.getMessageSize(msgIndex + 1));
			}
		}
		
		return response;
	}
}
