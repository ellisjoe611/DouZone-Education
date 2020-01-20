package socket;

import java.io.*;
import java.net.*;

public class TCPServerApp {

	public static void main(String[] args) {

		ServerSocket serversocket = null;

		try {
			// 1. create server socket
			serversocket = new ServerSocket();

			// 1-1. Time-wait 시간동안 소켓에 포트번호 할당을 가능하게 한다.
			serversocket.setReuseAddress(true);

			// 2. bind socket address(ip + port)
			serversocket.bind(new InetSocketAddress("127.0.0.1", 5000));
			log("Server Starts...[port:" + 5000 + "]");

			// 3. accept (= blocking)
			Socket socket = serversocket.accept();

			InetSocketAddress remoteInetSocketAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
			InetAddress remoteInetAddress = remoteInetSocketAddress.getAddress();
			String remoteHostAddress = remoteInetAddress.getHostAddress();
			int remotePort = remoteInetSocketAddress.getPort();

			log("connected by client - " + remoteHostAddress + ":" + remotePort);

			try {
				// 4. receive IOStream
				InputStream is = socket.getInputStream();
				OutputStream os = socket.getOutputStream();

				while (true) {
					// 5. read data from client
					byte[] buffer = new byte[256];
					int readByteCount = is.read(buffer);

					if (readByteCount == -1) {
						System.out.println("Closed successfully by client...");
						break;
					}

					String message = new String(buffer, 0, readByteCount, "UTF-8");
					System.out.println("Received >> " + message);

					Thread.sleep(200);

					// 6. write bytes on outputStream
					message = "Hi, Client!";
					os.write(message.getBytes("UTF-8"));
				}
			} catch (SocketException e) {
				System.out.println("[server] sudden closed by client...");
			} catch (IOException e) {
				System.err.println(e);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				try {
					if (socket.isClosed() != true && socket != null) {
						socket.close();
					}
				} catch (IOException e) {
					System.out.println(e);
				}
			}

		} catch (IOException e) {
			System.out.println(e);
		} finally {
			try {
				if (serversocket.isClosed() == false && serversocket != null) {
					System.out.println("[server] closing ServerSocket now...");
					serversocket.close();
				}
			} catch (IOException e) {
				System.out.println(e);
			}
		}

	}

	public static void log(String s) {
		System.out.println(s);
	}

}
