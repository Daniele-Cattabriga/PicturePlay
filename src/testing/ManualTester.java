package testing;
import java.io.IOException;

import threads.*;
import tools.Utilities;
public class ManualTester {
	public static void main(String[] args) {
		Thread t;
		if (!(args.length<2) || 1 == Utilities.convertOption(args[0]) || 2 == Utilities.convertOption(args[0])) {
			try {
				t = new Thread(new Steganographer(args));
				t.start();
				t.join();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		else
			if(!(args.length<2) || 3 == Utilities.convertOption(args[0]) || 4 == Utilities.convertOption(args[0])) {
				// run the StScanner
			}
			else
			{	System.out.println("Wrong number of arguments, or wrong format.\n"
					+ "Usage: PicturePlay.jar option(-e, -d, -me, -md) startfolder OR picture_to_encode_or_decode [depth (to be used with mass options)][message to encode or message to mass decode]");
				
			}
		
	}
		
}
