package thread;

public class AlphabetThread extends Thread {

	@Override
	public void run() {
		for(char c='a'; c<='z'; c++) {
			System.out.print(c);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				System.err.println(e);
			}
		}
	}
	
}
