package thread;

public class ThreadExample01 {

	public static void main(String[] args) {
		Thread digitThread = new DigitThread();
		Thread alphabetThread = new AlphabetThread();
		
		digitThread.start();
		alphabetThread.start();
		

	}

}
