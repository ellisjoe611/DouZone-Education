package chatConsole;

import java.io.*;
import java.net.*;

public class ChatServer {

	private static final int SERVER_PORT = 8080;
	private static final String SERVER_ADDRESS = "0.0.0.0";

	public static void main(String[] args) {
		// 필요한 변수들 준비하기
		ServerSocket serverSocket = null;
		
		try {
			// 서버 소켓 생성하기
			serverSocket = new ServerSocket();

			// 서버 소켓에 bind 시키기
			serverSocket.bind(new InetSocketAddress(SERVER_ADDRESS, SERVER_PORT));
			log("Server Starts...[port:" + SERVER_PORT + "]");
			
			while(true) {
				Socket socket = serverSocket.accept();	//blocked 되는 상태
				System.out.println("ACCEPTED");
				new ChatServerThread(socket).start();
			}

		} catch (IOException e) {
			System.out.println(e);
		} finally {
			try {
				if (serverSocket.isClosed() == false && serverSocket != null) {
					serverSocket.close();
				}
			} catch (IOException e) {
				System.out.println(e);
			}
		}

	}
	
	public static void log(String s) {
		System.out.println("[Server] " + s);
	}
	
}
