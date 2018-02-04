package pop3;

import java.net.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import pop3.command.*;



public class ClientHandler implements Runnable {

	private Socket mSocket;
	
	private DataOutputStream mSocketOutput;
	private DataInputStream mSocketInput;
	
	private String mUser;
	private SessionState mState;
	private Server mServer;
	
	
	ClientHandler(Socket clientSocket, Server server) throws IOException {
		mSocket = clientSocket;
		mServer = server;
		
		mSocketOutput = new DataOutputStream(mSocket.getOutputStream());
		mSocketInput = new DataInputStream(mSocket.getInputStream());
		
		// TODO: initialize processors map
	}
	
	@Override
	public void run() {
		mState = SessionState.AUTHORIZATION;
		sendGreeting();
		
		while (!mSocket.isClosed()) {
			receiveCommand();
		}
	}
	
	private void sendGreeting() {
		sendResponse(new POP3Response(true, "POP3 server is ready"));
	}
	
	private void sendResponse(POP3Response response) {
		try {
			mSocketOutput.writeChars(response.getString());
			mSocketOutput.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void receiveCommand() {
		try {
			// TODO: get rid of UTF
			String command = mSocketInput.readUTF();
			
			if (!CommandValidator.validate(command)) {
				sendResponse(CommandValidator.getInvalidResponse());
			} else {
				/*
				CommandProcessor processor = getCommandProcessor(CommandValidator.getCommandKeyword(command));
				
				processor.process(command);
				applyCommandChanges(processor.getCommandArgs());
				sendResponse(processor.getResponse());
				*/
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void applyCommandChanges(CommandProcessor.CommandArgs args) {
		mState = args.mState;
		mUser = args.mUser;
	}
}
