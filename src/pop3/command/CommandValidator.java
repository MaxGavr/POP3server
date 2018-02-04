package pop3.command;

import pop3.command.POP3Response;

public class CommandValidator {
	public static boolean validate(String command) {
		return true;
	}
	
	public static String getCommandKeyword(String command) {
		return new String();
	}
	
	public static POP3Response getInvalidResponse() {
		return new POP3Response(false, "invalid command");
	}
}
