package thread;

public class UppercaseAlphabet extends Thread {
	public void print() {
		for (char c = 'A'; c <= 'Z'; c++) {
			System.out.print(c);
		}
	}
}
