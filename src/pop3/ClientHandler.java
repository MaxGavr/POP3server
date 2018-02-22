package pop3;

import java.net.*;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

import pop3.command.POP3Response;
import pop3.command.CommandParser;
import pop3.command.CommandState;
import pop3.command.ICommandProcessor;



public class ClientHandler implements Runnable {

	private Socket mSocket;
	
	private DataOutputStream mSocketOutput;
	private Scanner mSocketInput;
	
	private String mUser;
	private SessionState mState;
	private boolean mCloseConnection = false;

	private Server mServer;
	
	private Map<String, ICommandProcessor> mProcessors;
	
	
	ClientHandler(Socket clientSocket, Server server) {
		mSocket = clientSocket;
		mServer = server;
		
		mProcessors = new HashMap<String, ICommandProcessor>();
	}
	
	
	@Override
	public void run() {
		try {
			mSocketInput = new Scanner(mSocket.getInputStream());
			mSocketInput.useDelimiter(CommandParser.getLineEnd());

			mSocketOutput = new DataOutputStream(mSocket.getOutputStream());

		} catch (IOException e) {
			e.printStackTrace();
			disconnect();
			return;
		}
		
		mState = SessionState.AUTHORIZATION;
		sendGreeting();
		
		while (!mSocket.isClosed() && !mCloseConnection) {
			receiveCommand();
		}
		
		disconnect();
	}
	
	
	private void sendGreeting() {
		sendResponse(new POP3Response(true, "POP3 server is ready"));
	}
	
	private void sendResponse(POP3Response response) {
		try {
			mSocketOutput.write(response.getString().getBytes(StandardCharsets.US_ASCII));
			mSocketOutput.flush();
			
			mServer.serverMessage("Send response to " + getClientAddress() + "\n---\n" + response.getString() + "---");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void receiveCommand() {
		String command = null;
		
		try {
			command = mSocketInput.nextLine();
		} catch (NoSuchElementException e) {
			e.printStackTrace();
			return;
		}
		
		if (command == null) {
			mCloseConnection = true;
			return;
		}
		
		mServer.serverMessage("Received command \"" + command + "\" from " + getClientAddress());
		
		if (!CommandParser.validate(command)) {
			sendResponse(CommandParser.getInvalidResponse());
			
		} else if (!isCommandAvailable(CommandParser.getCommandKeyword(command))) {
			sendResponse(new POP3Response(false, "command is not implemented"));
			
		} else {
			ICommandProcessor processor = getCommandProcessor(CommandParser.getCommandKeyword(command));
			CommandState state = getClientSessionState();
			state.setCommand(command);
			
			POP3Response response = processor.process(state);
			
			applyCommandChanges(state);
			sendResponse(response);
		}
	}

	
	private void disconnect() {
		mServer.serverMessage("Disconnecting client " + getClientAddress());
		
		if (mState == SessionState.TRANSACTION) {
			mServer.getUserMaildrop(mUser).unlock();
		}
		
		try {
			mSocketOutput.close();
			mSocketInput.close();
			
			if (!mSocket.isClosed()) {
				mSocket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getClientAddress() {
		return mSocket.getInetAddress().getHostAddress() + ":" + mSocket.getPort();
	}
	
	public boolean isCommandAvailable(String command) {
		return mProcessors.containsKey(command);
	}
	
	public void registerCommand(String command, ICommandProcessor processor) {
		mProcessors.put(command, processor);
		//serverMessage("Register command " + command);
	}
	
	
	private ICommandProcessor getCommandProcessor(String commandKeyword) {
		return mProcessors.get(commandKeyword);
	}
	
	private CommandState getClientSessionState() {
		// TODO: check references
		CommandState state = new CommandState();
		state.setUser(mUser);
		state.setSessionState(mState);
		state.setCloseConnection(mCloseConnection);
		//state.setCommand(command);
		return state;
	}
	
	private void applyCommandChanges(CommandState sessionState) {
		mState = sessionState.getSessionState();
		mUser = sessionState.getUser();
		mCloseConnection = sessionState.isCloseConnection();
	}
}
