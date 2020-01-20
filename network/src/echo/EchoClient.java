package echo;

import java.io.*;
import java.net.*;
import java.util.*;

public class EchoClient {

	private static final String SERVER_IP = "127.0.0.1";
	private static final int SERVER_PORT = 8000;
	
	public static void main(String[] args) {
		Scanner scanner = null;
		Socket socket = null;
		
		try {
			//1. scanner 생성(표준입력, 키보드연결)
			scanner = new Scanner(System.in);
			
			//2. socket 생성
			socket = new Socket();
			
			//2. connect to Server
			socket.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT));
			log("connected to server");
			
			//3. IOStream
			InputStream is = socket.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
			BufferedReader br = new BufferedReader(isr);
			
			OutputStream os = socket.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
			PrintWriter pw = new PrintWriter(osw, true);	//true: 버퍼가 다 채워지면 바로 보내고 비운다. (auto flush)
			
			while(true) {
				// 키보드 입력 받기
				System.out.print(">> ");
				String line = scanner.nextLine();
				
				if(line.equalsIgnoreCase("quit")) {
					break;
				}
				
				// 데이터 쓰기
				pw.println(line);
				
				// 데이터 읽기 작업
				String data = br.readLine();
				if(data == null) {
					log("closed by server...");
					break;
				}
				System.out.println("<< " + data);
			}
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(scanner != null) {
					scanner.close();
				}
				if(socket != null && socket.isClosed() != true) {
					socket.close();
				}
			} catch(IOException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	public static void log(String s) {
		System.out.println("[client]" + s);
	}
}
