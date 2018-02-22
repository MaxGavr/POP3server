package pop3.command;

import pop3.SessionState;

public class NOOPCommandProcessor implements ICommandProcessor {
	
	@Override
	public POP3Response process(ClientSessionState state) {
		
		if (state.getSessionState() != SessionState.TRANSACTION) {
			return new POP3Response(false, "NOOP command can only be used in TRANSACTION state");
		}
		
		return new POP3Response(true);
	}

}
