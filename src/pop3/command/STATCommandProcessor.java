package pop3.command;

import pop3.Maildrop;
import pop3.Server;
import pop3.SessionState;



public class STATCommandProcessor implements ICommandProcessor {

	private Server server;

	
	public STATCommandProcessor(Server server) {
		this.server = server;
	}

	
	@Override
	public POP3Response process(CommandState state) {

		if (state.getSessionState() != SessionState.TRANSACTION) {
			return new POP3Response(false, "STAT command can only be used in TRANSACTION state");
		}
		
		// success
		Maildrop mail = server.getUserMaildrop(state.getUser());
		return new POP3Response(true, mail.getMessageCount() + " " + mail.getMailSize());
	}

}
