package pop3;

import java.net.*;

import java.util.HashMap;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import pop3.command.*;
import pop3.command.CommandProcessor.CommandArgs;



public class ClientHandler implements Runnable {

	private Socket mSocket;
	
	private DataOutputStream mSocketOutput;
	private BufferedReader mSocketInput;
	
	private String mUser;
	private SessionState mState;
	private boolean mCloseConnection = false;
	private Server mServer;
	
	private HashMap<String, CommandProcessor> mProcessors;
	
	
	ClientHandler(Socket clientSocket, Server server) throws IOException {
		mSocket = clientSocket;
		mServer = server;
		
		mSocketOutput = new DataOutputStream(mSocket.getOutputStream());
		mSocketInput = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
		
		mProcessors = new HashMap<String, CommandProcessor>();
		mProcessors.put("USER", new USERCommandProcessor(mServer));
		mProcessors.put("PASS", new PASSCommandProcessor(mServer));
		mProcessors.put("QUIT", new QUITCommandProcessor(mServer));
		mProcessors.put("STAT", new STATCommandProcessor(mServer));
		mProcessors.put("LIST", new LISTCommandProcessor(mServer));
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
			//mSocketOutput.writeChars(response.getString());
			mSocketOutput.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void receiveCommand() {
		try {
			String command = mSocketInput.readLine();
			if (command == null) {
				return;
			}
			
			System.out.println("Received command: " + command);
			
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
	
	private void disconnect() {
		try {
			mSocketOutput.close();
			mSocketInput.close();
			mSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private CommandProcessor getCommandProcessor(String commandKeyword) {
		return mProcessors.get(commandKeyword);
	}
	
	private CommandArgs formCommandArgs() {
		// TODO: check references
		return new CommandProcessor.CommandArgs(mUser, mState, mCloseConnection);
	}
	
	private void applyCommandChanges(CommandProcessor.CommandArgs args) {
		mState = args.mState;
		mUser = args.mUser;
		mCloseConnection = args.mCloseConnection;
	}
}
