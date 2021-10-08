package testing;
import java.io.IOException;

import threads.*;
public class ManualTester {
/*Still gotta check for megamessages, there was a pesky out of bounds error
 * before :\*/
	public static void main(String[] args) {
		Thread t;
		String[] str= {"-d","/home/daniele/Downloads/test.png", "sussy amorgus funny moment 69" };
		try {
			t = new Thread(new Steganographer(str));
			t.start();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		
	}

}
