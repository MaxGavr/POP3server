package pop3;

import java.net.*;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import pop3.ClientHandler;
import pop3.Maildrop;
import pop3.command.CommandProcessor;



public class Server {
	
	private final int IP_PORT = 110;
	private final int TOTAL_CLIENTS = 4;
	private int mAcceptTimeout = 1_000 * 1;
	
	private ServerSocket mServerSocket;
	private volatile HashMap<String, Maildrop> mUserMaildrop;
	private volatile HashMap<String, String> mUserPassword;
	
	volatile HashMap<String, CommandProcessor> mProcessors;
	
	private ExecutorService executor;
	
	private BufferedReader mConsoleInput;
	
	
	public Server() {
		mProcessors = new HashMap<String, CommandProcessor>();
		loadUsers();
	}
	
	public void start(){
		serverMessage("Server started");
		executor = Executors.newFixedThreadPool(TOTAL_CLIENTS);
		
		try {
			mServerSocket = new ServerSocket(IP_PORT);
			mServerSocket.setSoTimeout(mAcceptTimeout);
			
			mConsoleInput = new BufferedReader(new InputStreamReader(System.in));
			
			acceptNewClients();

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		executor.shutdown();
	}

	private void acceptNewClients() throws IOException {
		while (!mServerSocket.isClosed()) {
			if (mConsoleInput.ready()) {
				String input = mConsoleInput.readLine();
				
				if (input.equalsIgnoreCase("quit")) {
					System.out.println("Quit command received");
					
					mServerSocket.close();
					break;
				}
			}
			
			Socket clientSocket;
			try {
				clientSocket = mServerSocket.accept();
			}
			catch (SocketTimeoutException e)
			{
				continue;
			}
			
			ClientHandler client = new ClientHandler(clientSocket, this);
			serverMessage("Accept client " + client.getClientAddress());
			executor.execute(client);
		}
	}
	
	private void loadUsers() {
		mUserMaildrop = new HashMap<String, Maildrop>();
		mUserMaildrop.put("max", new Maildrop("max.txt"));
		mUserMaildrop.put("jack", new Maildrop("jack.txt"));
		mUserMaildrop.put("lucy", new Maildrop("lucy.txt"));
		
		mUserPassword = new HashMap<String, String>();
		mUserPassword.put("max", "pass_max");
		mUserPassword.put("jack", "pass_jack");
		mUserPassword.put("lucy", "pass_lucy");
	}
	
	synchronized void serverMessage(String msg) {
		System.out.println(msg);
	}
	
	public boolean isCommandAvailable(String command) {
		return mProcessors.containsKey(command);
	}
	
	public void registerCommand(String command, CommandProcessor processor) {
		mProcessors.put(command, processor);
		serverMessage("Register command " + command);
	}
	
	public int getTimeout() {
		return mAcceptTimeout;
	}

	public void setTimeout(int timeout) {
		mAcceptTimeout = timeout;
	}
	
	public boolean hasUser(String user) {
		return mUserMaildrop.get(user) != null;
	}
	
	public synchronized String getUserPassword(String user) {
		return mUserPassword.get(user);
	}
	
	public synchronized Maildrop getUserMaildrop(String user) {
		return mUserMaildrop.get(user);
	}
}
