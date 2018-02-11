package pop3;

import java.net.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import pop3.command.POP3Response;
import pop3.command.CommandParser;
import pop3.command.CommandProcessor;
import pop3.command.CommandProcessor.ClientSessionState;



public class ClientHandler implements Runnable {

	private Socket mSocket;
	
	private DataOutputStream mSocketOutput;
	private BufferedReader mSocketInput;
	
	private String mUser;
	private SessionState mState;
	private boolean mCloseConnection = false;

	private Server mServer;
	
	
	ClientHandler(Socket clientSocket, Server server) throws IOException {
		mSocket = clientSocket;
		mServer = server;
		
		mSocketOutput = new DataOutputStream(mSocket.getOutputStream());
		mSocketInput = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
	}
	
	
	@Override
	public void run() {
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
			
			mServer.serverMessage("Send response \n---\n" + response.getString() + "---\nto " + getClientAddress());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void receiveCommand() {
		try {
			String command = mSocketInput.readLine();
			if (command == null) {
				mCloseConnection = true;
				return;
			}
			
			mServer.serverMessage("Received command \"" + command + "\" from " + getClientAddress());
			
			if (!CommandParser.validate(command)) {
				sendResponse(CommandParser.getInvalidResponse());
			} else if (!mServer.isCommandAvailable(CommandParser.getCommandKeyword(command))) {
				sendResponse(new POP3Response(false, "command is not implemented"));
			} else {
				CommandProcessor processor = getCommandProcessor(CommandParser.getCommandKeyword(command));
				
				processor.process(command, getClientSessionState());
				applyCommandChanges(processor.retrieveCommandArgs());

				sendResponse(processor.getResponse());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void disconnect() {
		mServer.serverMessage("Disconnecting client " + getClientAddress());
		try {
			mSocketOutput.close();
			mSocketInput.close();
			mSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getClientAddress() {
		return mSocket.getInetAddress().getHostAddress() + ":" + mSocket.getPort();
	}
	
	private CommandProcessor getCommandProcessor(String commandKeyword) {
		return mServer.mProcessors.get(commandKeyword);
	}
	
	private ClientSessionState getClientSessionState() {
		// TODO: check references
		return new CommandProcessor.ClientSessionState(mUser, mState, mCloseConnection);
	}
	
	private void applyCommandChanges(CommandProcessor.ClientSessionState sessionState) {
		mState = sessionState.mState;
		mUser = sessionState.mUser;
		mCloseConnection = sessionState.mCloseConnection;
	}
}
