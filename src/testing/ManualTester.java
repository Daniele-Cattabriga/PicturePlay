package testing;
import java.io.IOException;

import threads.*;
public class ManualTester {

	public static void main(String[] args) {
		Thread t;
		try {
			t = new Thread(new Steganographer("/home/daniele/Downloads/test.png","Ciao, questo messaggio verrà inserito nella foto!"));
			t.start();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		
	}

}
