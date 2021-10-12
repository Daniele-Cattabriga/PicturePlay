package testing;
import threads.*;
import tools.Utilities;
public class ManualTester {
	public static void main(String[] args) {
		Thread t;
		if (!(args.length<2) && (1 == Utilities.convertOption(args[0]) || 2 == Utilities.convertOption(args[0]))) {
			try {
				t = new Thread(new Steganographer(args));
				t.start();
				t.join();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		else
			if(!(args.length<2) && (3 == Utilities.convertOption(args[0]) || 4 == Utilities.convertOption(args[0]))) {
				try {
					t= new Thread(new StScanner(args));
					t.start();
					t.join();
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
			else
			if(!(args.length<2) && (5 == Utilities.convertOption(args[0]))) {
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
						+ "Usage: PicturePlay.jar option(-e, -d, -me, -md, -mule) startfolder|targeted-file(depends on option) [depth](for -me and -md) [message{0...n}](for encoding options)");
				System.exit(1);
			}
		
	}
		
}
