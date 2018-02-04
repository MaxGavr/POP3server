package pop3.command;

import pop3.Server;
import pop3.SessionState;
import pop3.command.POP3Response;



public abstract class CommandProcessor {
	
	public class CommandArgs {
		public String mUser;
		public SessionState mState;
		
		public CommandArgs(String user, SessionState state) {
			mUser = user;
			mState = state;
		}
	}
	
	
	protected Server mServer;
	protected CommandArgs mArgs;
	protected POP3Response mResponse;
	
	
	public CommandProcessor(Server server, CommandArgs args) {
		mServer = server;
		mArgs = args;
		mResponse = new POP3Response();
	}
	
	
	public CommandArgs getCommandArgs() {
		return mArgs;
	}
	
	abstract public void process(String command);
	
	public POP3Response getResponse() {
		return mResponse;
	}
}
