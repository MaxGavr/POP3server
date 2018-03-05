import pop3.Server;
import gui.AppWindow;
import gui.ServerObserver;

import javax.swing.SwingUtilities; 



public class Main {

	public static void main(String[] args) {
		
		Server server = new Server();
		
		ServerObserver observer = new ServerObserver(server);
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				AppWindow window = new AppWindow(observer);
				window.start();
			}
		});
	}
}
