package exceptions;

public class MessageDoesNotExistException extends Exception {

	private static final long serialVersionUID = -215178007853729037L;
	public MessageDoesNotExistException(String msg){
		super(msg);
	}
}
