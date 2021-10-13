package tools;

public class Utilities {

	public static int convertOption(String option) {
		switch(option) {
		
			case "-e":
				return 1;
			case "-d":
				return 2;
			case "-md":
				return 3;
			case "-me":
				return 4;
			case "-mule":
				return 5;
			default:
				return 0;
				
		}
	}
	
	public static String printUsage() {
		return "Usage: PicturePlay.jar option(-e, -d, -me, -md, -mule) startfolder|targeted-file(depends on option) [depth](for -me and -md) [message{0...n}](for encoding options)";
	}
}
