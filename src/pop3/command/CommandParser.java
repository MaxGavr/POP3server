package pop3.command;

import pop3.command.POP3Response;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class CommandParser {	
	
	//private static String mKeywordRegex = "[\\p{Print}]{3,4}";
	//private static String mArgumentRegex = "[\\p{Print}]{1,40}";
	
	// TODO: add \r\n
	private static Pattern mPOP3CommandPattern = Pattern.compile("^[\\p{Print}]{3,4}( [\\p{Print}]{1,40})*$");
	private static LinkedList<String> mKnownCommands = new LinkedList<String>();
	static {
		mKnownCommands.add("USER");
		mKnownCommands.add("PASS");
		mKnownCommands.add("QUIT");
		mKnownCommands.add("STAT");
		mKnownCommands.add("LIST");
		mKnownCommands.add("RETR");
	}
	
	
	public static boolean validate(String command) {
		if (command == null) {
			return false;
		}
		
		Matcher commandMatcher = mPOP3CommandPattern.matcher(command);
		if (!commandMatcher.matches()) {
			return false;
		}
		
		String keyword = getCommandKeyword(command).toUpperCase();
		
		return commandMatcher.matches() && mKnownCommands.contains(keyword);
	}
	
	public static String getCommandKeyword(String command) {
		String argsAndKeyword[] = command.split(" ");
		return argsAndKeyword[0].toUpperCase();
	}
	
	public static String[] getCommandArgs(String command) {
		String argsAndKeyword[] = command.split(" ");
		if (argsAndKeyword.length == 1) {
			return new String[0];
		} else {
			return Arrays.copyOfRange(argsAndKeyword, 1, argsAndKeyword.length);
		}
	}
	
	public static POP3Response getInvalidResponse() {
		return new POP3Response(false, "invalid command");
	}
	
	public static int getLineMaxLength() {
		return 512;
	}
}
