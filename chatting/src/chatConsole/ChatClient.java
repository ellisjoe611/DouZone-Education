package chatConsole;

import java.util.*;
import java.io.*;
import java.net.*;

public class ChatClient {

	private static final int SERVER_PORT = 8080;
	private static final String SERVER_ADDRESS = "127.0.0.1";

	public static void main(String[] args) {
		Scanner scanner = null;
		Socket socket = null;

		try {
			// 1. scanner 생성(표준입력, 키보드연결)
			scanner = new Scanner(System.in);

			// 2. socket 생성
			socket = new Socket();

			// 3. 서버에 접속하기 (서버의 .accept() 와 연결됨)
			socket.connect(new InetSocketAddress(SERVER_ADDRESS, SERVER_PORT));
			log("connected to server");

			// 4. reader/writer 생성
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true); // auto
																													// flush
																													// 켬

			// 5. JOIN 프로토콜 사용
			while (true) {
				System.out.print("사용자 이름 >> ");
				String userName = scanner.nextLine();
				writer.println("join:" + userName);

				String response = reader.readLine();
				if (response.equalsIgnoreCase("join:ok")) {
					System.out.println("서버와 연결되었습니다. 채팅 시작!");
					break;
				} else if (response.equalsIgnoreCase("join:already_exists")) {
					log("ERROR : 이미 사용중인 유저 이름입니다");
					// 다시 사용자의 이름을 받게 된다.
				} else {
					System.out.println("ERROR - 알 수 없는 프로토콜입니다.");
//					return;
				}
			}

			// 6. ChatClientReceiveThread 시작
			ChatClientReceiveThread thread = new ChatClientReceiveThread(socket);
			thread.start();

			// 7. 사용자 입력 처리(quit 입력시 종료)
			while (true) {
				String input = scanner.nextLine();
				if (input.equalsIgnoreCase("quit")) {
					writer.println(input);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				} else {
					writer.println("message:" + input);
				}
			}

		} catch (IOException e) {
			System.out.println("error - " + e);
		} finally {
			try {
				if (scanner != null) {
					scanner.close();
				}
				if (socket != null && socket.isClosed() != true) {
					socket.close();
				}
			} catch (IOException e) {
				log("error - " + e);
			}

		}

	}

	public static void log(String s) {
		System.out.println("[Client] " + s);
	}

}
