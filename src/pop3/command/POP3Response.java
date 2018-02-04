package pop3.command;



public class POP3Response {
	
	private boolean mIsPositive;
	private String mArgs;
	
	
	public static String getPosPrefix() {
		return "+OK";
	}
	
	public static String getErrPrefix() {
		return "-ERR";
	}
	
	
	public POP3Response() {
		mIsPositive = false;
		mArgs = new String();
	}
	
	public POP3Response(boolean isPositive, String arg) {
		mIsPositive = isPositive;
		mArgs = arg;
	}
	
	
	public void setPositive(boolean isPositive) {
		mIsPositive = isPositive;
	}
	
	public void setArgs(String args) {
		mArgs = args;
	}
	
	public void setResponse(boolean isPositive, String args) {
		mIsPositive = isPositive;
		mArgs = args;
	}
	
	public String getString() {
		String str = new String();
		
		str += mIsPositive ? getPosPrefix() : getErrPrefix();
		str += " " + mArgs;
		
		return str;
	}
}
