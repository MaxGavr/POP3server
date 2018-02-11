package pop3.command;

import pop3.Server;
import pop3.SessionState;
import pop3.command.POP3Response;



public abstract class CommandProcessor {
	
	static public class ClientSessionState {
		public String mUser;
		public SessionState mState;
		public boolean mCloseConnection;
		
		public ClientSessionState(String user, SessionState state, boolean closeConnection) {
			mUser = user;
			mState = state;
			mCloseConnection = closeConnection;
		}
	}
	
	
	protected Server mServer;
	protected ClientSessionState mSession;
	protected POP3Response mResponse;
	
	
	public CommandProcessor(Server server) {
		mServer = server;
		mResponse = new POP3Response();
	}
	
	
	public ClientSessionState retrieveCommandArgs() {
		return mSession;
	}
	
	abstract public void process(String command, ClientSessionState session);
	
	public POP3Response getResponse() {
		return mResponse;
	}
}
