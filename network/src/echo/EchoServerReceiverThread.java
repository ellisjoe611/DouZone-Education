package echo;

import java.io.*;
import java.net.*;

public class EchoServerReceiverThread extends Thread {

	private Socket socket;

	public EchoServerReceiverThread(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		// get port & ip address of client
		InetSocketAddress remoteInetSocketAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
		int remotePort = remoteInetSocketAddress.getPort();

		InetAddress remoteInetAddress = remoteInetSocketAddress.getAddress();
		String remoteHostAddress = remoteInetAddress.getHostAddress();

		EchoServer.log("connected by client - " + remoteHostAddress + ":" + remotePort);

		try {
			// 4. receive IOStream
			InputStream is = socket.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
			BufferedReader br = new BufferedReader(isr);

			OutputStream os = socket.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
			PrintWriter pw = new PrintWriter(osw, true); // true: 버퍼가 다 채워지면 바로 보내고 비운다. (auto flush)

			while (true) {
				// 5. read data from client
				String data = br.readLine(); // blocking
				if (data == null) {
					EchoServer.log("Closed by client...");
					break;
				}

				EchoServer.log("Received >> " + data);

				// 6. write bytes on outputStream
				pw.println(data);
			}
		} catch (SocketException e) {
			System.out.println("[server] sudden closed by client...");
		} catch (IOException e) {
			System.out.println(e);
		} finally {
			try {
				if (socket.isClosed() != true && socket != null) {
					socket.close();
				}
			} catch (IOException e) {
				System.out.println(e);
			}
		}
	}

}
