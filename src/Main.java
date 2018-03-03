import java.io.IOException;

import pop3.Server;



public class Main {

	public static void main(String[] args) {
		Server server = new Server();
		try {
			server.loadUsers("users.txt");
			server.loadMail("mail");
			server.start();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

}
