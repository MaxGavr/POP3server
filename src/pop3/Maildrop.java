package pop3;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.stream.Collectors;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.nio.charset.StandardCharsets;



public class Maildrop {
	private ArrayList<String> mMessages;
	private TreeSet<Integer> mMarkedMessages;
	
	private boolean mIsLocked;
	
	
	public Maildrop() {
		mMessages = new ArrayList<String>();
		mMarkedMessages = new TreeSet<Integer>();
	}
	
	public Maildrop(String fileName) {
		try {
			loadFromFile(fileName);
		} catch (FileNotFoundException e) {
			mMessages = new ArrayList<String>();
		}
		
		mMarkedMessages = new TreeSet<Integer>();
	}
	
	
	public void markMessageToDelete(int msgIndex) {
		if (isValidIndex(msgIndex)) {
			mMarkedMessages.add(msgIndex);
		}
	}
	
	public void unmarkMessageToDelete(int msgIndex) {
		if (isValidIndex(msgIndex)) {
			mMarkedMessages.remove(msgIndex);
		}
	}
	
	public boolean deleteMarkedMessages() {
		for (Integer msgIndex : mMarkedMessages.descendingSet()) {
			mMessages.remove(msgIndex - 1);
		}
		mMarkedMessages.clear();
		
		return true;
	}
	
	public ArrayList<String> getMessages() {
		return mMessages;
	}
	
	public TreeSet<Integer> getMarkedMessages() {
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
		return mMarkedMessages.contains(msgIndex);
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
