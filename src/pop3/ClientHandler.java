package pop3;

import java.net.*;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

import pop3.command.POP3Response;
import pop3.command.CommandParser;
import pop3.ServerEvent.EventType;
import pop3.command.ClientSessionState;
import pop3.command.ICommandProcessor;



public class ClientHandler implements Runnable {

	private Socket socket;
	
	private DataOutputStream socketOutput;
	private Scanner socketInput;
	private InputStream socketInputStream;
	
	private String user = "";
	private SessionState sessionState = SessionState.AUTHORIZATION;
	private boolean closeConnection = false;

	private Server server;
	
	private Map<String, ICommandProcessor> processors;
	
	
	ClientHandler(Socket clientSocket, Server server) {
		this.socket = clientSocket;
		this.server = server;
		
		processors = new HashMap<String, ICommandProcessor>();
	}
	
	
	@Override
	public void run() {
		try {
			socketInputStream = socket.getInputStream();
			socketInput = new Scanner(socketInputStream);
			socketInput.useDelimiter(CommandParser.getLineEnd());

			socketOutput = new DataOutputStream(socket.getOutputStream());

		} catch (IOException e) {
			e.printStackTrace();
			disconnect();
			return;
		}
		
		sessionState = SessionState.AUTHORIZATION;
		sendGreeting();
		
		while (!socket.isClosed() && !closeConnection && !Thread.interrupted()) {
			receiveCommand();
		}
		
		disconnect();
	}
	
	
	private void sendGreeting() {
		sendResponse(new POP3Response(true, "POP3 server is ready"));
	}
	
	private void sendResponse(POP3Response response) {
		try {
			socketOutput.write(response.getString().getBytes(StandardCharsets.US_ASCII));
			socketOutput.flush();
			
			ServerEvent event = new ServerEvent(EventType.RESPONSE_SENT, server.getCurrentTime());
			event.addArg(getClientAddress());
			event.addArg(response.getString());
			
			server.addEvent(event);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void receiveCommand() {
		String command = null;
		
		try {
			if (socketInputStream.available() > 0) {
				command = socketInput.nextLine();
			} else {
				return;
			}
		} catch (NoSuchElementException | IOException e) {
			e.printStackTrace();
			return;
		}
		
		if (command == null) {
			closeConnection = true;
			return;
		}
		
		ServerEvent event = new ServerEvent(EventType.COMMAND_RECEIVED, server.getCurrentTime());
		event.addArg(getClientAddress());
		event.addArg(command);
		server.addEvent(event);
		
		if (!CommandParser.validate(command)) {
			sendResponse(CommandParser.getInvalidResponse());
			
		} else if (!isCommandAvailable(CommandParser.getCommandKeyword(command))) {
			sendResponse(new POP3Response(false, "command is not implemented"));
			
		} else {
			ICommandProcessor processor = getCommandProcessor(CommandParser.getCommandKeyword(command));
			ClientSessionState state = getClientSessionState();
			state.setCommand(command);
			
			POP3Response response = processor.process(state);
			
			applyCommandChanges(state);
			sendResponse(response);
		}
	}

	private void disconnect() {
		server.addEvent(new ServerEvent(EventType.DISCONNECT_CLIENT, getClientAddress(), server.getCurrentTime()));
		
		if (sessionState == SessionState.TRANSACTION) {
			server.getUserMaildrop(user).unlock();
		}
		
		try {
			socketOutput.close();
			socketInput.close();
			
			if (!socket.isClosed()) {
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getClientAddress() {
		return socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
	}
	
	public boolean isCommandAvailable(String command) {
		return processors.containsKey(command);
	}
	
	public void registerCommand(String command, ICommandProcessor processor) {
		processors.put(command, processor);
		
		ServerEvent event = new ServerEvent(EventType.REGISTER_COMMAND, server.getCurrentTime());
		event.addArg(getClientAddress());
		event.addArg(command);
		
		server.addEvent(event);
	}
	
	
	private ICommandProcessor getCommandProcessor(String commandKeyword) {
		return processors.get(commandKeyword);
	}
	
	private ClientSessionState getClientSessionState() {
		ClientSessionState state = new ClientSessionState();

		state.setUser(user);
		state.setSessionState(sessionState);
		state.setCloseConnection(closeConnection);
		
		return state;
	}
	
	private void applyCommandChanges(ClientSessionState state) {
		sessionState = state.getSessionState();
		user = state.getUser();
		closeConnection = state.isCloseConnection();
	}
}
