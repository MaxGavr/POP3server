package pop3.command;

import pop3.Maildrop;
import pop3.Server;
import pop3.SessionState;



public class RSETCommandProcessor implements ICommandProcessor {

	private Server server;

	
	public RSETCommandProcessor(Server server) {
		this.server = server;
	}

	
	@Override
	public POP3Response process(CommandState state) {
		
		if (state.getSessionState() != SessionState.TRANSACTION) {
			return new POP3Response(false, "RSET commmand can only be used in TRANSACTION state");
		}
		
		Maildrop mail = server.getUserMaildrop(state.getUser());
		
		for (Integer msgIndex : mail.getMarkedMessages()) {
			mail.unmarkMessageToDelete(msgIndex);
		}
		
		return new POP3Response(true, "all messages saved");
	}
}
