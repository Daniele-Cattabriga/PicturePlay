package testing;
import threads.*;
import tools.Utilities;


/**
 * Main class of the PicturePlay program, detects the chosen option between:
 * <ul>
 * <li> -e: Standard encoding option, it requires a picture to receive the message and a message;</li>
 * <li> -d: Standard decoding option, it require a picture containing a message to decode;</li>
 * <li> -me: Mass encoding option, encodes all png pictures in the chosen folder tree, it requires a startfolder, a maximum depth and a message to encode;</li>
 * <li> -md: Mass decoding option, decodes all png pictures in the chosen folder tree, it requires a startfolder and a depth;</li>
 * <li> -mule: Mass encoding multiple message option, encodes a series of messages in the png pictures detected in the chosen folder tree, it keeps going until it runs out of messages or pictures. Requires a startfolder and a series of messages to encode.</li>
 * </ul>
 * And then calls the proper class to satisfy the requested service.
 * 
 * 
 * @author Daniele Cattabriga
 * @version 1.0
 * @since 16-10-2021
 *
 */
public class ManualTester {
	public static void main(String[] args) {
		Thread t;
		if (1 == Utilities.convertOption(args[0]) || 2 == Utilities.convertOption(args[0])) {
			try {
				t = new Thread(new Steganographer(args));
				t.start();
				t.join();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		else
			if(3 == Utilities.convertOption(args[0]) || 4 == Utilities.convertOption(args[0])) {
				try {
					t= new Thread(new StScanner(args));
					t.start();
					t.join();
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
			else
			if(5 == Utilities.convertOption(args[0])) {
				try {
					t=new Thread(new MultEncoder(args));
					t.start();
					t.join();
				}
				catch(Exception e) {
				System.out.println(e.getMessage());
				}
				
			}
			else {
				System.out.println("Wrong number of arguments or wrong option\n"
						+ Utilities.printUsage());
				System.exit(1);
			}
		
	}
		
}
