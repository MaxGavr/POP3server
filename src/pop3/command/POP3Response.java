package pop3.command;

import java.util.ArrayList;
import java.util.Arrays;



public class POP3Response {
	
	private boolean mIsPositive;
	private boolean mIsMultiline;
	private ArrayList<String> mArgs;
	
	
	public static String getPosPrefix() {
		return "+OK";
	}
	
	public static String getErrPrefix() {
		return "-ERR";
	}
	
	public static String getLineEnd() {
		return "\r\n";
	}
	
	
	public POP3Response() {
		mIsPositive = false;
		mIsMultiline = false;
		mArgs = new ArrayList<String>();
	}
	
	public POP3Response(boolean isPositive) {
		mIsPositive = isPositive;
		mIsMultiline = false;
		mArgs = new ArrayList<String>();
	}
	
	public POP3Response(boolean isPositive, String arg) {
		mIsPositive = isPositive;
		mIsMultiline = false;
		mArgs = new ArrayList<String>();
		mArgs.add(arg);
	}
	
	public POP3Response(boolean isPositive, String[] args) {
		mIsPositive = isPositive;
		setArgs(args);
	}
	
	
	public void setPositive(boolean isPositive) {
		mIsPositive = isPositive;
	}
	
	public void setMultiline(boolean isMultiline) {
		mIsMultiline = true;
	}
	
	public void setArgs(String[] args) {
		mIsMultiline = true;
		mArgs = new ArrayList<String>(Arrays.asList(args));
	}
	
	public void addArg(String arg) {
		mArgs.add(arg);
		if (mArgs.size() > 1) {
			mIsMultiline = true;
		}
	}
	
	public void clearArgs() {
		mIsMultiline = false;
		mArgs.clear();
	}
	
	public void setResponse(boolean isPositive, String arg) {
		mIsPositive = isPositive;
		clearArgs();
		addArg(arg);
	}
	
	public void setResponse(boolean isPositive, String[] args) {
		mIsPositive = isPositive;
		setArgs(args);
	}
	
	public String getString() {
		String str = new String(); 
		
		str += mIsPositive ? getPosPrefix() : getErrPrefix();
		
		if (mIsMultiline) {
			str += getLineEnd();
			
			for (String arg : mArgs) {
				str += arg + getLineEnd();
			}
			
			str += "." + getLineEnd();
		} else if (!mArgs.isEmpty()) {
			str += " " + mArgs.get(0) + getLineEnd();
		} else {
			str += getLineEnd();
		}
		
		return str;
	}
}
