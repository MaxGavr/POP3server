package pop3.command;

public class POP3Response {
	
	private String mString;
	
	
	public static String getPosPrefix() {
		return "+OK";
	}
	
	public static String getErrPrefix() {
		return "-ERR";
	}
	
	public POP3Response(boolean isPositive, String arg) {
		if (isPositive)
			mString += getPosPrefix();
		else
			mString += getErrPrefix();
		
		mString += arg;
	}
	
	public String getString() {
		return mString;
	}
}
