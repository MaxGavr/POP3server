package pop3;

import java.net.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.Map;
import java.util.Observable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;

import java.util.concurrent.ExecutorService;

import java.io.IOException;
import java.io.File;

import pop3.ClientHandler;
import pop3.Maildrop;
import pop3.ServerEvent.EventType;
import pop3.command.*;



public class Server extends Observable {
	
	private final int POP3_IP_PORT = 110;
	private final int TOTAL_CLIENTS = 4;
	private int acceptTimeout = 1_000 * 1;
	
	private ServerSocket serverSocket;
	private volatile Map<String, Maildrop> userMaildrop;
	private volatile Map<String, String> userPassword;
	
	private String mailFolder = "";
	
	private ExecutorService executor;
	
	private List<ServerEvent> events; 
	
	
	public Server() {
		userMaildrop = new HashMap<String, Maildrop>();
		userPassword = new HashMap<String, String>();
		
		events = new ArrayList<ServerEvent>();
	}


	public void start() throws IOException {
		addEvent(new ServerEvent(EventType.SERVER_STARTED));
		
		executor = Executors.newFixedThreadPool(TOTAL_CLIENTS);
		
		try {
			serverSocket = new ServerSocket(POP3_IP_PORT);
			serverSocket.setSoTimeout(acceptTimeout);

		} catch (IOException e) {
			throw new IOException("Failed to set up server socket.");	
		}
		
		acceptNewClients();
		
		shutdown();
	}
	
	private void shutdown() {
		if (!serverSocket.isClosed()) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		executor.shutdown();
		addEvent(new ServerEvent(EventType.SERVER_STOPPED));
	}

	private void acceptNewClients() {
		while (!serverSocket.isClosed()) {
			
			Socket clientSocket;
			try {
				clientSocket = serverSocket.accept();
			} catch (SocketTimeoutException e) {
				continue;
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}
			
			executor.execute(createClientHandler(clientSocket));
		}
	}
	
	private ClientHandler createClientHandler(Socket clientSocket) {
		ClientHandler client = new ClientHandler(clientSocket, this);
		addEvent(new ServerEvent(EventType.ACCEPT_CLIENT, client.getClientAddress()));
		
		client.registerCommand("USER", new USERCommandProcessor(this));
		client.registerCommand("PASS", new PASSCommandProcessor(this));
		client.registerCommand("QUIT", new QUITCommandProcessor(this));
		client.registerCommand("STAT", new STATCommandProcessor(this));
		client.registerCommand("LIST", new LISTCommandProcessor(this));
		client.registerCommand("RETR", new RETRCommandProcessor(this));
		client.registerCommand("DELE", new DELECommandProcessor(this));
		client.registerCommand("NOOP", new NOOPCommandProcessor());
		client.registerCommand("RSET", new RSETCommandProcessor(this));

		client.registerCommand("UIDL", new UIDLCommandProcessor(this));
		
		return client;
	}
	
	
	public void loadUsers(String fileName) throws IOException {
		
		try {
			List<String> lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
			
			for (String line : lines) {
				String[] userAndPass = line.split(" ", 2);
				userPassword.put(userAndPass[0], userAndPass[1]);
			}
		} catch (IOException e) {
			throw new IOException("Failed to load users and passwords from file.");
		}
	}
	
	public void loadMail(String folder) {
		mailFolder = folder;
		
		for (String user : userPassword.keySet()) {
			Maildrop mail = new Maildrop();
			
			try {
				mail.loadFromFile(getUserMailFileName(user));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			userMaildrop.put(user, mail);
		}
	}
	
	
	public synchronized void serverMessage(String msg) {
		System.out.println(msg);
	}
	
	public synchronized void addEvent(ServerEvent event) {
		events.add(event);
		
		setChanged();
		notifyObservers(event);
	}
	
	public ServerEvent getLastEvent() {
		return events.get(events.size() - 1);
	}
	
	
	public int getTimeout() {
		return acceptTimeout;
	}

	public void setTimeout(int timeout) {
		acceptTimeout = timeout;
	}
	
	
	public boolean hasUser(String user) {
		return userMaildrop.get(user) != null;
	}
	
	public synchronized String getUserPassword(String user) {
		return userPassword.get(user);
	}
	
	public synchronized Maildrop getUserMaildrop(String user) {
		return userMaildrop.get(user);
	}

	public synchronized String getUserMailFileName(String user) {
		if (!mailFolder.isEmpty()) {
			return mailFolder + File.separator + user + ".xml";
		} else {
			return user + ".xml";
		}
	}

	public String getMailFolder() {
		return mailFolder;
	}
}
