package pop3;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;


public class Maildrop {
	private ArrayList<String> mMessages;
	
	private boolean mIsLocked;
	
	
	public Maildrop() {
		mMessages = new ArrayList<String>();
	}
	
	public Maildrop(String fileName) {
		try {
			loadFromFile(fileName);
		} catch (FileNotFoundException e) {
			mMessages = new ArrayList<String>();
		}
	}
	
	public ArrayList<String> getMessages() {
		return mMessages;
	}
	
	public boolean isLocked() {
		return mIsLocked;
	}
	
	public void lock() {
		mIsLocked = true;
	}
	
	public void unlock() {
		mIsLocked = false;
	}
	
	public void loadFromFile(String fileName) throws FileNotFoundException {
		BufferedReader fileReader = new BufferedReader(new FileReader(fileName));
		mMessages = (ArrayList<String>)fileReader.lines().collect(Collectors.toList());
		try {
			fileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
