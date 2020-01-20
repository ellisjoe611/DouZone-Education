package udp;

import java.io.*;
import java.net.*;
import java.util.*;

public class UDPTimeClient {
	private static final String SERVER_IP = "127.0.0.1";
	private static final int SERVER_PORT = 7000;
	private static final int BUFFER_SIZE = 1024;

	public static void main(String[] args) {
		Scanner scanner = null;
		DatagramSocket socket = null;

		try {
			// 1. scanner 생성(표준입력, 키보드연결)
			scanner = new Scanner(System.in);

			// 2. socket 생성
			socket = new DatagramSocket();

			System.out.print(">> ");
			String line = scanner.nextLine();
			if (line.equals("")) {
				// 4. 데이터 쓰기
				byte[] sendData = line.getBytes("UTF-8");
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, new InetSocketAddress(SERVER_IP, SERVER_PORT));
				socket.send(sendPacket);

				// 5. 데이터 읽기
				DatagramPacket receivePacket = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
				socket.receive(receivePacket); // blocking

				byte[] data = receivePacket.getData();
				int length = receivePacket.getLength();
				String message = new String(data, 0, length, "UTF-8");

				System.out.println("[Client] received: " + message);
			}

		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (scanner != null) {
				scanner.close();
			}
			if (socket != null && socket.isClosed() != true) {
				socket.close();
			}

		}

	}
}
