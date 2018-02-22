package pop3;

import java.net.*;

import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import pop3.ClientHandler;
import pop3.Maildrop;
import pop3.command.*;



public class Server {
	
	private final int POP3_IP_PORT = 110;
	private final int TOTAL_CLIENTS = 4;
	private int acceptTimeout = 1_000 * 1;
	
	private ServerSocket serverSocket;
	private volatile Map<String, Maildrop> userMaildrop;
	private volatile Map<String, String> userPassword;
	
	private ExecutorService executor;
	
	private BufferedReader consoleInput;
	
	
	public Server() {
		loadUsers();
	}
	
	public void start(){
		serverMessage("Server started");
		executor = Executors.newFixedThreadPool(TOTAL_CLIENTS);
		
		try {
			serverSocket = new ServerSocket(POP3_IP_PORT);
			serverSocket.setSoTimeout(acceptTimeout);
			
			consoleInput = new BufferedReader(new InputStreamReader(System.in));

		} catch (IOException e) {
			e.printStackTrace();	
		}
		
		acceptNewClients();
		
		shutdown();
	}
	
	private void shutdown() {
		serverMessage("Stopping server...");
		
		if (!serverSocket.isClosed()) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		executor.shutdown();
		serverMessage("Server stopped");
	}

	private void acceptNewClients() {
		while (!serverSocket.isClosed()) {
			
			String input = getConsoleInput();
			
			if (input != null && input.equalsIgnoreCase("quit")) {
				serverMessage("Receive QUIT command from console");
				return;
			}
			
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
		serverMessage("Accept client " + client.getClientAddress());
		
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
	
	private String getConsoleInput() {
		String input = null;

		try {
			if (consoleInput.ready()) {
				input = consoleInput.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return input;
	}
	
	private void loadUsers() {
		userMaildrop = new HashMap<String, Maildrop>();
		userMaildrop.put("max", new Maildrop("max.txt"));
		userMaildrop.put("jack", new Maildrop("jack.txt"));
		userMaildrop.put("lucy", new Maildrop("lucy.txt"));
		
		userPassword = new HashMap<String, String>();
		userPassword.put("max", "pass_max");
		userPassword.put("jack", "pass_jack");
		userPassword.put("lucy", "pass_lucy");
	}
	
	synchronized void serverMessage(String msg) {
		System.out.println(msg);
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
}
