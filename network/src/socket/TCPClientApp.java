package socket;

import java.io.*;
import java.net.*;

public class TCPClientApp {
	private static final String SERVER_IP = "127.0.0.1";
	private static final int SERVER_PORT = 5000;

	public static void main(String[] args) {
		// 1. create socket
		Socket socket = null;

		try {
			socket = new Socket();

			// Socket 크기 확인
			System.out.println(socket.getReceiveBufferSize() + " : " + socket.getSendBufferSize());

			// Socket 버퍼 사이즈 변경
			socket.setReceiveBufferSize(1024 * 10);
			socket.setSendBufferSize(1024 * 10);
			System.out.println(socket.getReceiveBufferSize() + " : " + socket.getSendBufferSize());

			// Nagle off을 통해 delay를 해제한다.
			socket.setTcpNoDelay(true);

			// timeout 설정
			socket.setSoTimeout(1000); // 1초 이상 delay되면 timeout

			// 2. connect to Server
			socket.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT));
			System.out.println("connected to server");

			// 3. IOStream
			InputStream in = socket.getInputStream();
			OutputStream out = socket.getOutputStream();

			// 4. write
			String data = "Hi, Server!";
			out.write(data.getBytes("UTF-8"));

			// 5. read
			byte[] buffer = new byte[256];
			int readByteCount = in.read(buffer); // blocking

			if (readByteCount == -1) {
				// terminate from Server
				System.out.println("Closed successfully by Server...");
				return;
			}

			data = new String(buffer, 0, readByteCount, "UTF-8");
			System.out.println("[client] recieved > " + data);

		} catch (SocketTimeoutException e) {
			System.out.println("[Client] Timeout...\n" + e);
		} catch (IOException e) {
			System.out.println(e);
		} finally {
			try {
				if (socket != null && socket.isClosed() == false) {
					System.out.println("[client] closing Socket now...");
					socket.close();
				}
			} catch (IOException e) {
				System.out.println(e);
			}
		}

	}

}
