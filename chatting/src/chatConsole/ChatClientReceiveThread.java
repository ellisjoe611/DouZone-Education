package chatConsole;

import java.io.*;
import java.net.*;

public class ChatClientReceiveThread extends Thread {
	private Socket socket;

	public ChatClientReceiveThread(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

			while (true) {
				String msgFromServer = reader.readLine();	//blocked 상태
				if (msgFromServer == null) {
					ChatClient.log("error - 연결이 끊어짐");
					break;
				} else if (msgFromServer.equalsIgnoreCase("quit:ok")) {
					ChatClient.log("서버와 접속이 종료되었습니다.");
					break;
				} else {
					System.out.println(msgFromServer);
				}
			}

		} catch (UnsupportedEncodingException e) {
			System.out.println(e);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (socket != null && socket.isClosed() != true) {
					socket.close();
				}
				ChatClient.log("채팅 종료가 완료되었습니다. 감사합니다.");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
