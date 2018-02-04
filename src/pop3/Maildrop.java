package pop3;

import java.util.ArrayList;

public class Maildrop {
	private ArrayList<String> mMessages;
	
	private boolean mIsLocked;
	
	public boolean isLocked() {
		return mIsLocked;
	}
	
	public void lock() {
		mIsLocked = true;
	}
	
	public void unlock() {
		mIsLocked = false;
	}
}
