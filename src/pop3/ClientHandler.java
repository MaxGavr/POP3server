package pop3;

import java.net.*;

import java.util.HashMap;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import pop3.command.*;
import pop3.command.CommandProcessor.CommandArgs;



public class ClientHandler implements Runnable {

	private Socket mSocket;
	
	private DataOutputStream mSocketOutput;
	private DataInputStream mSocketInput;
	
	private String mUser;
	private SessionState mState;
	private Server mServer;
	
	private HashMap<String, CommandProcessor> mProcessors;
	
	
	ClientHandler(Socket clientSocket, Server server) throws IOException {
		mSocket = clientSocket;
		mServer = server;
		
		mSocketOutput = new DataOutputStream(mSocket.getOutputStream());
		mSocketInput = new DataInputStream(mSocket.getInputStream());
		
		mProcessors = new HashMap<String, CommandProcessor>();
		mProcessors.put("USER", new USERCommandProcessor(mServer));
		mProcessors.put("PASS", new PASSCommandProcessor(mServer));
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
			
			if (!CommandParser.validate(command)) {
				sendResponse(CommandParser.getInvalidResponse());
			} else {
				CommandProcessor processor = getCommandProcessor(CommandParser.getCommandKeyword(command));
				
				processor.process(command, formCommandArgs());
				applyCommandChanges(processor.retrieveCommandArgs());

				sendResponse(processor.getResponse());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private CommandProcessor getCommandProcessor(String commandKeyword) {
		return mProcessors.get(commandKeyword);
	}
	
	private CommandArgs formCommandArgs() {
		return new CommandProcessor.CommandArgs(mUser, mState);
	}
	
	private void applyCommandChanges(CommandProcessor.CommandArgs args) {
		mState = args.mState;
		mUser = args.mUser;
	}
}
