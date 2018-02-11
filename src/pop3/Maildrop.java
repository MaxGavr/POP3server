package pop3;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.nio.charset.StandardCharsets;



public class Maildrop {
	private ArrayList<String> mMessages;
	private HashSet<Integer> mMarkedMessages;
	
	private boolean mIsLocked;
	
	
	public Maildrop() {
		mMessages = new ArrayList<String>();
		mMarkedMessages = new HashSet<Integer>();
	}
	
	public Maildrop(String fileName) {
		try {
			loadFromFile(fileName);
		} catch (FileNotFoundException e) {
			mMessages = new ArrayList<String>();
		}
		
		mMarkedMessages = new HashSet<Integer>();
	}
	
	
	public void markMessageToDelete(int msgIndex) {
		if (isValidIndex(msgIndex)) {
			mMarkedMessages.add(msgIndex - 1);
		}
	}
	
	public void unmarkMessageToDelete(int msgIndex) {
		if (isValidIndex(msgIndex)) {
			mMarkedMessages.remove(msgIndex - 1);
		}
	}
	
	public void deleteMarkedMessages() {
		for (Integer msgIndex : mMarkedMessages) {
			mMessages.remove(msgIndex.intValue() - 1);
		}
		mMarkedMessages.clear();
	}
	
	public ArrayList<String> getMessages() {
		return mMessages;
	}
	
	public HashSet<Integer> getMarkedMessages() {
		return mMarkedMessages;
	}
	
	public String getMessage(int msgIndex) {
		if (isValidIndex(msgIndex)) {
			return mMessages.get(msgIndex - 1);
		} else {
			// TODO: get rid of null
			return null;
		}
	}
	
	public int getMessageSize(int msgIndex) {
		if (isValidIndex(msgIndex)) {
			return mMessages.get(msgIndex - 1).getBytes(StandardCharsets.US_ASCII).length;
		} else {
			// TODO: dunno what to do
			return 0;
		}
	}
	
	public int getMessageCount() {
		return mMessages.size();
	}
	
	public int getMailSize() {
		int size = 0;
		for (String string : mMessages) {
			size += string.getBytes(StandardCharsets.US_ASCII).length;
		}
		
		return size;
	}
	
	public boolean isValidIndex(int msgIndex) {
		return msgIndex > 0 && msgIndex <= mMessages.size();
	}
	
	public boolean isMessageMarked(int msgIndex) {
		return mMarkedMessages.contains(msgIndex -1 );
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
