package pop3.command;

import pop3.command.POP3Response;



public interface ICommandProcessor {
	
	public POP3Response process(CommandState state);
}