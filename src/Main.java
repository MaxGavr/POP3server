import pop3.Server;
import pop3.command.*;



public class Main {

	public static void main(String[] args) {
		Server server = new Server();
		
		server.registerCommand("USER", new USERCommandProcessor(server));
		server.registerCommand("PASS", new PASSCommandProcessor(server));
		server.registerCommand("QUIT", new QUITCommandProcessor(server));
		server.registerCommand("STAT", new STATCommandProcessor(server));
		server.registerCommand("LIST", new LISTCommandProcessor(server));
		server.registerCommand("RETR", new RETRCommandProcessor(server));
		server.registerCommand("DELE", new DELECommandProcessor(server));
		server.registerCommand("NOOP", new NOOPCommandProcessor(server));
		
		server.start();
	}

}
