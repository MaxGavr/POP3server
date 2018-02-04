package pop3;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.net.*;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

import pop3.ClientHandler;
import pop3.Maildrop;



public class Server {
	
	private final int IP_PORT = 110;
	private final int TOTAL_CLIENTS = 4;
	private int mAcceptTimeout = 1_000 * 1;
	
	private ServerSocket mServerSocket;
	private volatile HashMap<String, Maildrop> mUserMaildrop;
	private volatile HashMap<String, String> mUserPassword;
	
	private ExecutorService executor;
	
	private BufferedReader mConsoleInput;
		
	
	public void start(){
		executor = Executors.newFixedThreadPool(TOTAL_CLIENTS);
		
		try {
			mServerSocket = new ServerSocket(IP_PORT);
			mServerSocket.setSoTimeout(mAcceptTimeout);
			
			mConsoleInput = new BufferedReader(new InputStreamReader(System.in));
			
			acceptNewClients();
		}
		catch (IOException e)
		{
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
			executor.execute(client);
		}
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
	
	public String getUserPassword(String user) {
		return mUserPassword.get(user);
	}
	
	public Maildrop getUserMaildrop(String user) {
		return mUserMaildrop.get(user);
	}
}
