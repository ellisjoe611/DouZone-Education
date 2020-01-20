package echo;

import java.io.*;
import java.net.*;

public class EchoServer {
	private static final int PORT = 8000;

	public static void main(String[] args) {

		ServerSocket serversocket = null;

		try {
			// 1. create server socket
			serversocket = new ServerSocket();

			// 2. bind socket address(ip + port)
			serversocket.bind(new InetSocketAddress("127.0.0.1", PORT));
			log("Server Starts...[port:" + PORT + "]");

			// 3. accept (= blocking)
			while (true) {
				Socket socket = serversocket.accept();
				new EchoServerReceiverThread(socket).start();
			}

		} catch (IOException e) {
			System.out.println(e);
		} finally {
			try {

				if (serversocket.isClosed() == false && serversocket != null) {
					serversocket.close();
				}
			} catch (IOException e) {
				System.out.println(e);
			}
		}

	}

	public static void log(String s) {
		System.out.println("[Server #" + Thread.currentThread().getId() + "] " + s);

	}

}
