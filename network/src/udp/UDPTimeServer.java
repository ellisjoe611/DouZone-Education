package udp;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

public class UDPTimeServer {

	private static final int PORT = 7000;
	private static final int BUFFER_SIZE = 1024;

	public static void main(String[] args) {
		DatagramSocket socket = null;

		try {
			// 1. 소켓 생성
			socket = new DatagramSocket(PORT);
			while (true) {
				// 2. 데이터 수신
				DatagramPacket receivePacket = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
				socket.receive(receivePacket); // blocking

				byte[] data = receivePacket.getData();
				int length = receivePacket.getLength();
				String message = new String(data, 0, length, "UTF-8");

				System.out.println("[Server] received: " + message);

				// 3. 데이터 송신
				if(message.equals("")) {
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a");
					byte[] sendData = format.format(new Date()).getBytes("UTF-8");
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(), receivePacket.getPort()); // byte buffer, buffer size, receiver's address, receiver's port
					socket.send(sendPacket);
				}
			}
		} catch (SocketException e) {
			System.out.println(e);
		} catch (IOException e) {
			System.out.println(e);
		} finally {
			if (socket != null && !socket.isClosed()) {
				socket.close();
			}
		}

	}

}
