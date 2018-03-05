package gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JButton;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;



public class AppWindow {

	private ServerObserver serverObserver;
	
	private JFrame frame;
	
	private JPanel framePanel;
	private JPanel buttonPanel;
	private JPanel serverLogPanel;
	
	private JTextArea serverLog;
	private JButton clearLogButton;
	
	private JButton startServerButton;


	public AppWindow(ServerObserver serverObserver) {
		this.serverObserver = serverObserver;
		serverObserver.setWindow(this);
		
		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("POP3Server");
		
		framePanel = new JPanel();
		frame.setContentPane(framePanel);
		framePanel.setLayout(new BoxLayout(framePanel, BoxLayout.X_AXIS));
		
		serverLogPanel = new JPanel();
		serverLogPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		serverLogPanel.setLayout(new BoxLayout(serverLogPanel, BoxLayout.Y_AXIS));
		framePanel.add(serverLogPanel);
		
		serverLog = new JTextArea();
		serverLog.setEditable(false);
		
		JScrollPane logScrollPane = new JScrollPane(serverLog);
		serverLogPanel.add(logScrollPane);
		
		clearLogButton = new JButton("Clear log");
		clearLogButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				serverLog.setText("");
			}
		});
		clearLogButton.setAlignmentX(0.5f);
		
		serverLogPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		serverLogPanel.add(clearLogButton);
		
		
		buttonPanel = new JPanel();
		framePanel.add(buttonPanel);
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		
		startServerButton = new JButton("Start server");
		startServerButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (serverObserver.isServerRunning()) {
					serverObserver.stopServer();
					startServerButton.setText("Start server");
				} else {
					serverObserver.startServer();
					startServerButton.setText("Stop server");
				}
			}
		});
		
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 10));;
		buttonPanel.add(startServerButton);
		buttonPanel.add(Box.createVerticalGlue());
	}
	
	public void start() {
		frame.setVisible(true);
	}

	public void logServerMessage(String eventLog) {
		serverLog.append(eventLog + '\n');
	}
}
