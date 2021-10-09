package exceptions;

public class IllegalNumberOfArgumentsException extends Exception {
	
	private static final long serialVersionUID = -2216450577812497341L;
	
	public IllegalNumberOfArgumentsException(String msg) {
		super(msg);
	}
}
