package pop3.command;


import java.io.FileNotFoundException;

import pop3.Maildrop;
import pop3.Server;
import pop3.SessionState;



public class QUITCommandProcessor implements ICommandProcessor {

	private Server server;

	
	public QUITCommandProcessor(Server server) {
		this.server = server;
	}
	

	@Override
	public POP3Response process(ClientSessionState state) {
		POP3Response response = new POP3Response();
		
		if (state.getSessionState() == SessionState.AUTHORIZATION) {
			if (!state.getUser().isEmpty()) {
				state.setUser("");
			}
			
			response.setResponse(true, "POP3 server signing off");
			
		} else if (state.getSessionState() == SessionState.TRANSACTION) {
			state.setSessionState(SessionState.UPDATE);
			
			Maildrop mail = server.getUserMaildrop(state.getUser());
			if (mail.deleteMarkedMessages()) {
				response.setResponse(true, "POP3 server signing off (" + mail.getMessageCount() + " messages left)");
			} else {
				response.setResponse(false, "fail to delete some messages");
			}
			
			try {
				mail.saveToFile(server.getUserMailFileName(state.getUser()));
			} catch (FileNotFoundException e) {
				server.serverMessage(e.getMessage());
			}
			mail.unlock();
			
			state.setUser("");
		}
		
		state.setCloseConnection(true);
		return response;
	}

}
