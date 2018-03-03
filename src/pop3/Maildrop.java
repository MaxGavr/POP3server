package pop3;

import java.util.List;
import java.util.ArrayList;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;



public class Maildrop {
	
	public static class MailParser {

		private static List<String> headerFields;
		
		static {
			headerFields = new ArrayList<String>();
			
			headerFields.add("to");
			headerFields.add("from");
			headerFields.add("subject");
		}
		
		
		public static String getLineEnd() {
			return "\r\n";
		}
		
		public static ArrayList<String> parseMail(String fileName) throws IOException {
			
			File mailFile = new File(fileName);
			ArrayList<String> messages = new ArrayList<String>();
			
			try {
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				
				Document doc = db.parse(mailFile);
					
				NodeList msgNodes = doc.getElementsByTagName("message");
				for (int i = 0; i < msgNodes.getLength(); i++) {
					messages.add(readMessage(msgNodes.item(i)));
				}
			
			} catch (IOException e) {
				throw new IOException("Failed to load mail from file " + fileName);
			} catch (SAXException e) {
				throw new IOException("XML parsing error in file " + fileName);
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
			
			return messages;
		}
		
		public static void saveToFile(List<String> mail, String fileName) throws FileNotFoundException {
			File mailFile = new File(fileName);
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			try {
				DocumentBuilder db = dbf.newDocumentBuilder();
				
				Document doc = db.newDocument();
				
				Element rootEl = doc.createElement("mail");
				for (String message : mail) {
					Element messageEl = doc.createElement("message");
					
					String[] headerAndBody = message.split(getLineEnd() + getLineEnd());
					
					String header = headerAndBody[0];
					String body = headerAndBody[1];
					
					for (String field : header.split(getLineEnd())) {
						String[] fieldAndValue = field.split(": ");
						
						String tag = fieldAndValue[0].toLowerCase();
						if (headerFields.contains(tag)) {
							Element fieldEl = doc.createElement(tag);
							fieldEl.setTextContent(fieldAndValue[1]);
							messageEl.appendChild(fieldEl);
						}
					}
					
					Element bodyEl = doc.createElement("body");
					bodyEl.setTextContent(body);
					
					messageEl.appendChild(bodyEl);					
					rootEl.appendChild(messageEl);
				}
				
				doc.appendChild(rootEl);
				
				Transformer tr = TransformerFactory.newInstance().newTransformer();
				tr.setOutputProperty(OutputKeys.INDENT, "yes");
				tr.setOutputProperty(OutputKeys.METHOD, "xml");
				tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
				tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
				
				tr.transform(new DOMSource(doc), new StreamResult(new FileOutputStream(mailFile)));
				
			} catch (FileNotFoundException e) {
				throw new FileNotFoundException("Failed to open/create file " + fileName);
			} catch (ParserConfigurationException | TransformerException e) {
				e.printStackTrace();
			}
		}
		
		private static String readMessage(Node messageNode) {
			if (messageNode.getNodeType() != Node.ELEMENT_NODE) {
				return null;
			}
			
			Element messageEl = (Element) messageNode;
			String message = "";
			
			for (String field : headerFields) {
				String fieldTitle = field.substring(0, 1).toUpperCase() + field.substring(1) + ": ";
				
				NodeList fieldNodes = messageEl.getElementsByTagName(field);
				if (fieldNodes.getLength() == 1) {
					message += fieldTitle + fieldNodes.item(0).getTextContent() + getLineEnd();
				}
			}
			
			if (!message.isEmpty()) {
				message += getLineEnd();
			}
			
			message += messageEl.getElementsByTagName("body").item(0).getTextContent();
			
			return message;
		}
	}
	
	private List<String> messages;
	private TreeSet<Integer> markedMessages;
	
	private boolean isLocked;
	
	
	public Maildrop() {
		messages = new ArrayList<String>();
		markedMessages = new TreeSet<Integer>();
	}
	
	
	public void loadFromFile(String fileName) throws IOException {
		messages = MailParser.parseMail(fileName);
	}
	
	public void saveToFile(String fileName) throws FileNotFoundException {
		MailParser.saveToFile(messages, fileName);
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
}
