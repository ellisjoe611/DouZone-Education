package chatWindow;

import java.util.*;
import java.net.*;
import java.io.*;

public class ChatClientApp {

	private static final int SERVER_PORT = 8080;
	private static final String SERVER_ADDRESS = "127.0.0.1";

	public static void main(String[] args) {
		String name = null;
		Scanner scanner = new Scanner(System.in);
		Socket socket = null;
		BufferedReader reader = null;
		PrintWriter writer = null;

		while (true) {
			System.out.println("대화명을 입력하세요.");
			System.out.print(">>> ");
			name = scanner.nextLine();

			if (name.isEmpty() == false) {
				break;
			}

			System.out.println("대화명은 한글자 이상 입력해야 합니다.\n");
		}
		scanner.close();

		try {
			// 1. socket 생성
			socket = new Socket();

			// 2. 서버에 연결
			socket.connect(new InetSocketAddress(SERVER_ADDRESS, SERVER_PORT));
			log("connected to server");

			// 3. iostream 생성
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), false);

			// 4. join 프로토콜 요청 및 처리
			writer.println("join:" + name);
			writer.flush();
			String response = reader.readLine();

			// 5.1 join 프로토콜이 성공 응답 받으면 -> 채팅 윈도우를 실행한다.
			if (response.equalsIgnoreCase("join:ok")) {
				new ChatWindow(name, socket).show(); // ChatWindow 안에 반복형 쓰레드 있음.
			}
			// 5.2 만약 중복된 유저가 있으면 -> 다시 이름을 받도록 한다.
			else if (response.equalsIgnoreCase("join:already_exists")) {
				log("ERROR : 이미 사용중인 유저 이름입니다");
			}
			// 5.3 알 수 없는 프로토콜을 받은 경우
			else {
				System.out.println("ERROR - 알 수 없는 프로토콜입니다.");
			}

		} catch (IOException e) {
			System.out.println("error - " + e);
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
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				log("closed connection with server.");
			}

		}

	}

	public static void log(String s) {
		System.out.println("[Client] " + s);
	}

}
