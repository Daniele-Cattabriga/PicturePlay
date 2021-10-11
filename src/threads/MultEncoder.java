package threads;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import exceptions.IllegalNumberOfArgumentsException;
public class MultEncoder implements Runnable {

	private String[] args;
	private int messagesLeft;
	private byte currentTurn;
	private String currentFolder;
	
	public MultEncoder(String[] args) throws IllegalNumberOfArgumentsException {
		if (args.length<4)
			throw new IllegalNumberOfArgumentsException("Not enough arguments, at least 4 required.\n"
					+ "Usage: PicturePlay.jar option startfolder|file (depends on option) [depth] [message 1...n] ");
		this.args=args;
		messagesLeft=Arrays.copyOfRange(args, 3, args.length).length;
		this.currentTurn=0;
		this.currentFolder=args[1];
	}
	
	public void run() {
		byte assignedTurn=currentTurn;
		File fo=new File(currentFolder);
		String[] contents= fo.list();
		ArrayList<String> folders= new ArrayList<String>();
		int i= args.length-messagesLeft;
		for(String f: contents) {
			if(messagesLeft!=0){
				if(f.substring(f.lastIndexOf(".")).compareTo("png")==0) {
					try {
						Thread et=new Thread(new Steganographer(new String[]{"-e",currentFolder+File.separator+f, args[i]}));
						messagesLeft--;
						et.start();
						et.join();
					} catch (Exception e) {
						System.out.println(e.getMessage());
					} 
				}
				if(new File(currentFolder+File.separator+f).isDirectory()) {
					folders.add(f);
				}
				
			}
			else
				break;
		}
		
		i=0;
		while(messagesLeft!=0 && i<folders.size()) {
			currentTurn++;
			Thread t= new Thread(this);
			t.start();
			try {
				wait();
				while(assignedTurn!=currentTurn)
						wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		currentTurn--;
		notifyAll();
	}
}
