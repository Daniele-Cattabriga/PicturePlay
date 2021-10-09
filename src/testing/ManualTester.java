package testing;
import java.io.IOException;

import threads.*;
public class ManualTester {
	public static void main(String[] args) {
		Thread t;
		String[] str= {args[0], args[1], args[2]};
		try {
			t = new Thread(new Steganographer(str));
			t.start();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		
	}

}
