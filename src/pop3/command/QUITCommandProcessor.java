package pop3.command;


import pop3.Maildrop;
import pop3.Server;
import pop3.SessionState;



public class QUITCommandProcessor implements ICommandProcessor {

	private Server server;

	
	public QUITCommandProcessor(Server server) {
		this.server = server;
	}
	

	@Override
	public POP3Response process(CommandState state) {
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
				response.setResponse(true, "POP3 server signing off (" + mail.getMailSize() + " messages left)");
			} else {
				response.setResponse(false, "fail to delete some messages");
			}
			
			state.setUser("");
			mail.unlock();
		}
		
		state.setCloseConnection(true);
		return response;
	}

}
