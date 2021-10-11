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
}
