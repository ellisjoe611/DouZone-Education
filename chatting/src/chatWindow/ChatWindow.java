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

	// 생성자
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
		// 현재의 윈도우를 열었을 때, 닫기 전에, 닫은 후에 할 행동들을 Thread로 활용하여 정의한다.
		frame.addWindowListener(new WindowAdapter() {
			ChatDisplayThread displayThread = null;

			// 맨 처음으로 윈도우를 열었을 때
			@Override
			public void windowOpened(WindowEvent e) {
				displayThread = new ChatDisplayThread();
				displayThread.start();
				
				textArea.setText("<서버와 연결되었습니다. 채팅 시작!>\n\n귓속말 방법 :\t유저명;;귓속말내용\n\n");
			}

			// 윈도우 닫기 버튼을 눌렀을 때에 할 일. 아래의 행동들이 끝난 다음에야 윈도우가 닫힌다.
			@Override
			public void windowClosing(WindowEvent e) {
				BufferedReader reader = null;
				PrintWriter writer = null;

				try {
					// 1. 우선 displayThread 종료
					displayThread.join(100);

					// 2. QUIT 프로토콜 처리하기
					reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
					writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), false);

					while (true) {
						writer.println("quit");
						writer.flush();
						
						String response = reader.readLine();
						if (response.equalsIgnoreCase("quit:ok")) {
							System.out.println("QUIT:OK 수신 완료");
							break;
						} else {
							System.out.println("연결을 아직 끊지 못했습니다.");
						}
					}

				} catch (InterruptedException e1) {
					System.out.println("[ChatWindow.java] ERROR - ChatDisplayThread 상에서의 interrupt 발생...");
					e1.printStackTrace();
				} catch (IOException e1) {
					System.out.println("[ChatWindow.java] ERROR - 창 닫는 도중 IO exception 발생...");
				} finally {
					try {
						if (reader != null) {
							reader.close();
						}
						if (writer != null) {
							writer.close();
						}
						if (socket != null && socket.isClosed() != true) {
							socket.close();
						}
					} catch (IOException e1) {
						System.out.println("[ChatWindow.java] ERROR - 소켓 종료 실패...");
						e1.printStackTrace();
					} finally {
						System.out.println("서버와의 연결 종료 완료. 감사합니다!");
						System.exit(0);
					}
					
				}
			}

		});
		frame.setVisible(true);
		frame.pack();

		while (true) {
			// blocking for current ChatWindow
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
			System.out.println("[ChatWindow.java] ERROR - SendMessage 오류. 지원되지 않는 인코딩입니다...");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("[ChatWindow.java] ERROR - SendMessage 오류. IO Exception 발생...");
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
				System.out.println("[ChatWindow.java] ERROR - 지원되지 않는 인코딩");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("[ChatWindow.java] ERROR - ChatDisplayThread 에서 IO Exception 발생...");
				e.printStackTrace();
			} finally {
				try {
					if (reader != null) {
						reader.close();
					}
				} catch (IOException e) {
					System.out.println("[ChatWindow.java] ERROR - ChatDisplayThread 에서 종료 실패...");
					e.printStackTrace();
				} finally {
					System.out.println("Display Thread 종료.");
				}
			}

		}

	}
}
