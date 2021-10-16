package threads;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import exceptions.IllegalNumberOfArgumentsException;
import tools.Utilities;


/**
 * 
 * This class utilizes strict thread synchronization enforced by a turn system in order to recursively
 * instantiate new threads on itself, which will run one by one, analyze the contents of the folder they are started in
 * and then, if any png file is found, a steganographer is istantiated on it in order to encode one of the messages.
 * When the messages end, or the entirety of the folder tree is explored, the program will end.
 * 
 * @author daniele
 * @version 1.0
 * @since 16-10-2021
 * @param args Arguments passed by the terminal.
 * @param messagesLeft Messages left to encode.
 * @param currentTurn Current depth of activated thread.
 * @param currentFolder Folder onto which another thread of this class will be started.
 */
public class MultEncoder implements Runnable {

	private String[] args;
	private int messagesLeft;
	private byte currentTurn;
	private String currentFolder;
	
	
	/**
	 * Standard constructor of MultEncoder, it checks whether the number of arguments is correct for function.
	 * 
	 *  @param args Arguments passed by terminal.
	 *  @exception IllegalNumberOfArgumentsException Exception thrown when the number of arguments is too low to function.
	 */
	public MultEncoder(String[] args) throws IllegalNumberOfArgumentsException {
		if (args.length<3)
			throw new IllegalNumberOfArgumentsException("Not enough arguments, at least 3 required.\n"
					+ Utilities.printUsage());
		this.args=args;
		messagesLeft=Arrays.copyOfRange(args, 2, args.length).length;
		this.currentTurn=0;
		this.currentFolder=args[1];
	}
	
	/**
	 *<p>Override of run method inherited from java.lang.Runnable</p>
	 *<p>First, it analyzes the folder in which the thread is started and detects all png pictures,
	 *then it starts a steganographer on it. Once all pictures are allocated, it starts a new thread on itself one of the
	 *detected folders, in which the logical flux will repeat itself.</p>
	 */
	public void run() {
		byte assignedTurn=currentTurn;
		File fo=new File(currentFolder);
		String[] contents= fo.list();
		ArrayList<String> folders= new ArrayList<String>();
		int i= args.length-messagesLeft;
		for(String f: contents) {
			if(messagesLeft!=0){
				if(new File(currentFolder+File.separator+f).isDirectory()) {
					folders.add(f);
				}
				else
					if(f.substring(f.lastIndexOf(".")).compareTo(".png")==0) {
						try {
							Thread et=new Thread(new Steganographer(new String[]{"-e",currentFolder+File.separator+f, args[i]}));
							i++;
							messagesLeft--;
							et.start();
							et.join();
						} catch (Exception e) {
							System.out.println(e.getMessage());
						} 
					}
			}
			else
				break;
		}
		
		i=0;
		while(messagesLeft!=0 && i<folders.size()) {
			currentFolder=fo+File.separator+folders.get(i);
			i++;
			currentTurn++;
			Thread t= new Thread(this);
			t.start();
			synchronized(this) {
				try {
					while(assignedTurn!=currentTurn)
						wait();
							
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		currentTurn--;
		synchronized(this) {
		notifyAll();
		}
	}
}
