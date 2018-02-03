package pop3;

import java.net.*;

public class ClientHandler implements Runnable {

	private Socket mSocket;
	
	ClientHandler(Socket clientSocket) {
		mSocket = clientSocket;
	}
	
	@Override
	public void run() {
		
	}

}
