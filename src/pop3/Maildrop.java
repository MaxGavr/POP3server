package pop3;

import java.util.List;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.stream.Collectors;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.nio.charset.StandardCharsets;



public class Maildrop {
	private List<String> messages;
	private TreeSet<Integer> markedMessages;
	
	private boolean isLocked;
	
	
	public Maildrop() {
		messages = new ArrayList<String>();
		markedMessages = new TreeSet<Integer>();
	}
	
	public Maildrop(String fileName) {
		try {
			loadFromFile(fileName);
		} catch (FileNotFoundException e) {
			messages = new ArrayList<String>();
		}
		
		markedMessages = new TreeSet<Integer>();
	}
	
	
	public void markMessageToDelete(int msgIndex) {
		if (isValidIndex(msgIndex)) {
			markedMessages.add(msgIndex);
		}
	}
	
	public void unmarkMessageToDelete(int msgIndex) {
		if (isValidIndex(msgIndex)) {
			markedMessages.remove(msgIndex);
		}
	}
	
	public boolean deleteMarkedMessages() {
		for (Integer msgIndex : markedMessages.descendingSet()) {
			messages.remove(msgIndex - 1);
		}
		markedMessages.clear();
		
		return true;
	}
	
	public ArrayList<String> getMessages() {
		return (ArrayList<String>) messages;
	}
	
	public TreeSet<Integer> getMarkedMessages() {
		return markedMessages;
	}
	
	public String getMessage(int msgIndex) {
		if (isValidIndex(msgIndex)) {
			return messages.get(msgIndex - 1);
		} else {
			// TODO: get rid of null
			return null;
		}
	}
	
	public int getMessageSize(int msgIndex) {
		if (isValidIndex(msgIndex)) {
			return messages.get(msgIndex - 1).getBytes(StandardCharsets.US_ASCII).length;
		} else {
			// TODO: dunno what to do
			return 0;
		}
	}
	
	public int getMessageCount() {
		return messages.size();
	}
	
	public int getMailSize() {
		int size = 0;

		for (int msgIndex = 1; msgIndex <= messages.size(); ++msgIndex) {
			size += getMessageSize(msgIndex);
		}

		return size;
	}
	
	public boolean isValidIndex(int msgIndex) {
		return msgIndex > 0 && msgIndex <= messages.size();
	}
	
	public boolean isMessageMarked(int msgIndex) {
		return markedMessages.contains(msgIndex);
	}
	
	public boolean isLocked() {
		return isLocked;
	}
	
	public void lock() {
		isLocked = true;
	}
	
	public void unlock() {
		isLocked = false;
	}
	
	public void loadFromFile(String fileName) throws FileNotFoundException {
		BufferedReader fileReader = new BufferedReader(new FileReader(fileName));
		messages = (ArrayList<String>)fileReader.lines().collect(Collectors.toList());
		try {
			fileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
