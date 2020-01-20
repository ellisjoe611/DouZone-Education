package socket;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class LocalHostApp {

	public static void main(String[] args) {
		InetAddress inet = null;
		
		try {
			inet = InetAddress.getLocalHost();
			
			String hostName = inet.getHostName();
			String hostAddress = inet.getHostAddress();
			byte[] addressList = inet.getAddress();
			for(byte add:addressList) {
				int n = (int) (add & 0x000000ff);
				System.out.println(n);
			}
			
			System.out.println(hostName + "\n" + hostAddress);
		} catch (UnknownHostException e) {
			System.out.println(e);
		}

	}

}
