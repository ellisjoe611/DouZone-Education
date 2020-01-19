package chatWindow;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.*;

public class ChatWindow {

	private Frame frame;
	private Panel pannel;
	private Button buttonSend;
	private TextField textField;
	private TextArea textArea;

	// socket
	private Socket socket;

	public ChatWindow(String name, Socket socket) {
		this.frame = new Frame(name);
		this.pannel = new Panel();
		this.buttonSend = new Button("Send");
		this.textField = new TextField();
		this.textArea = new TextArea(30, 80);

		this.socket = socket;
	}

	public void show() {
		// Button
		buttonSend.setBackground(Color.GRAY);
		buttonSend.setForeground(Color.WHITE);
		buttonSend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				sendMessage();
			}
		});

		// Textfield
		textField.setColumns(80);
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				char keyCode = e.getKeyChar();
				if (keyCode == KeyEvent.VK_ENTER) {
					sendMessage();
				}
			}

		});

		// Pannel
		pannel.setBackground(Color.LIGHT_GRAY);
		pannel.add(textField);
		pannel.add(buttonSend);
		frame.add(BorderLayout.SOUTH, pannel);

		// TextArea
		textArea.setEditable(false);
		frame.add(BorderLayout.CENTER, textArea);

		// Frame
		frame.addWindowListener(new WindowAdapter() {
			ChatDisplayThread displayThread = null;

			@Override
			public void windowOpened(WindowEvent e) {
				displayThread = new ChatDisplayThread();
				displayThread.start();
			}

			@Override
			public void windowClosing(WindowEvent e) {
				try {
					displayThread.join(100);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				} finally {
					System.exit(0);
				}
			}

		});
		frame.setVisible(true);
		frame.pack();
		
		while(true) {
			
		}

	}

	// 보내기 버튼을 눌렀을 때
	private void sendMessage() {
		String message = textField.getText();
		textField.setText("");
		textField.requestFocus();

		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), false);
			writer.println("message:" + message);
			writer.flush();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}

	// 입력받은 문자를 실시간으로 수신하여 받는다.
	public class ChatDisplayThread extends Thread {

		@Override
		public void run() {
			// IO Stream 초기화
			BufferedReader reader = null;

			// 쓰레드 생성작업
			try {
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

				// 소켓을 통해 메시지가 온 경우 (실시간형)
				while (true) {
					String msgFromServer = reader.readLine(); // blocked 상태
					if (msgFromServer == null) {
						System.out.println("[ChatWindow.java] ERROR - 연결이 끊어짐...");
						return;
					} else {
						textArea.append(msgFromServer);
						textArea.append("\n");
					}
				}

			} catch (UnsupportedEncodingException e) {
				System.out.println("[ChatWindow.java] ERROR - 지원되지 않는 인코딩\n");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("[ChatWindow.java] ERROR - IO Exception 발생...");
				e.printStackTrace();
			} finally {
				try {
					if (socket != null && socket.isClosed() != true) {
						socket.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					textArea.append("채팅 종료가 완료되었습니다. 감사합니다.");
				}
			}

		}

	}
}
