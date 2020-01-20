package chatConsole;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServerThread extends Thread {
	private String userName;
	private Socket socket;
	private static Map<String, Writer> writerMap = Collections.synchronizedMap(new HashMap<String, Writer>());
	
	
	public ChatServerThread(Socket s) {
		this.socket = s;
	}

	@Override
	public void run() {
		// 1. remote host information
		InetSocketAddress remoteInetSocketAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
		int remotePort = remoteInetSocketAddress.getPort();

		InetAddress remoteInetAddress = remoteInetSocketAddress.getAddress();
		String remoteHostAddress = remoteInetAddress.getHostAddress();

		ChatServer.log("connected by client - " + remoteHostAddress + ":" + remotePort);

		BufferedReader reader = null;
		PrintWriter writer = null;
		try {
			// 2. Stream 얻기
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), false); // auto
																													// flush
																													// 끔

			// 3. 요청 처리
			while (true) {
				String request = reader.readLine();
				if (request == null) {
					ChatServer.log("클라이언드로부터 연결이 끊어짐...");
					return; // 나가기
				}

				// 4. 토큰을 통해 프로토콜을 분석하기
				String[] tokens = request.split(":");
				if (tokens[0].equalsIgnoreCase("join")) {
					doJoin(tokens[1], writer);
				} else if (tokens[0].equalsIgnoreCase("message")) {
					doMessage(tokens[1], writer);
				} else if (tokens[0].equalsIgnoreCase("quit")) {
					return;
				} else {
					ChatServer.log("Error: 알 수 없는 요청 (" + tokens[0] + ")");
				}
			}
			

		} catch (SocketException e) {
			ChatServer.log("Suddenly closed by client...");
		} catch (IOException e) {
			ChatServer.log(e.toString());
		} finally {
			try {
				doQuit(writer);
				if (socket.isClosed() != true && socket != null) {
					socket.close();
				}
			} catch (IOException e) {
				ChatServer.log(e.toString());
			} finally {
				ChatServer.log("Closed connection with - " + remoteHostAddress + ":" + remotePort);
			}
		}

	}

	private void doQuit(Writer writer) {
		removeWriter(this.userName);

		// ACK 보내주기
		PrintWriter printWriter = (PrintWriter) writer;
		printWriter.println("quit:ok");
		printWriter.flush();

		//전체에게 보내기
		broadcast(this.userName + " 님이 퇴장하였습니다.");
	}

	private void removeWriter(String userName) {
		synchronized (writerMap) {
			writerMap.remove(userName);
		}

	}

	private void doMessage(String msg, Writer writer) {
		if (msg.contains(";;")) {
			// user;;secret_message
			String[] secretMsgToken = msg.split(";;");
			
			PrintWriter senderPrintWriter = (PrintWriter) writer;
			PrintWriter receiverPrintWriter = (PrintWriter) writerMap.get(secretMsgToken[0]);
			receiverPrintWriter.println(this.userName + "[SECRET] : " + secretMsgToken[1]);
			senderPrintWriter.println(this.userName + "[SECRET] : " + secretMsgToken[1]);
			
			senderPrintWriter.flush();
			receiverPrintWriter.flush();
			
			
		} else {
			broadcast(this.userName + " : " + msg);
		}
	}

	private void doJoin(String name, Writer writer) {
		//만약 중복된 유저가 있으면 거절 프로토콜을 전송한다.
		if (writerMap.containsKey(name)) {
			// ACK 보내주기 (join:already_exists)
			PrintWriter printWriter = (PrintWriter) writer;
			printWriter.println("join:already_exists");
			printWriter.flush(); // 정리
			return;
		}
		
		this.userName = name;
		broadcast(name + " 님이 입장하였습니다.");

		// writer pool에 저장
		addWriter(name, writer);

		// ACK 보내주기 (join:ok)
		PrintWriter printWriter = (PrintWriter) writer;
		printWriter.println("join:ok");
		printWriter.flush(); // 정리
	}

	private void broadcast(String data) {
		synchronized (writerMap) {
			for (Writer writer : writerMap.values()) {
				PrintWriter printWriter = (PrintWriter) writer;
				printWriter.println(data);

				printWriter.flush(); // 내보내기
			}
		}

	}

	private void addWriter(String name, Writer writer) {
		synchronized (writerMap) {
			writerMap.put(name, writer);
		}
	}

}
