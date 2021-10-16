package threads;

import java.io.File;
import java.io.IOException;

import exceptions.IllegalNumberOfArgumentsException;
import tools.Utilities;


/**
 * This class scans the folder tree from the start folder and performs a mass encoding or decoding function, depending on the
 * option chosen.
 * 
 * @author Daniele Cattabriga
 * @version 1.0
 * @since 16-10-2021
 * @param args Arguments passed by terminal.
 * @param startFolder Starting folder 
 */
public class StScanner implements Runnable {
	// first arg is the option, second arg is the start folder, third arg ONLY FOR MASS ENCODING, message
	
	private String[] args;
	private File startFolder;
	
	/**
	 * Standard constructor of StScanner, checks if there are enough arguments to function.
	 * @exception IOException Standard input/output exception.
	 * @exception IllegalNumberOfArgumentsException Exception thrown when the number of arguments is too low to function.
	 */
	
	public StScanner(String[] args) throws IOException, IllegalNumberOfArgumentsException
	{
		if (args.length>1) {
			startFolder = new File(args[1]);
			if (!startFolder.exists() || !startFolder.isDirectory())
				throw new IOException("The specified start folder does not exist\n"
						+ "Usage: PicturePlay.jar option(-md,-me) startfolder depth [message to mass encode]");
			this.args = args;
		}
		else
			throw new IllegalNumberOfArgumentsException("Wrong number of arguments\n"
					+ Utilities.printUsage());
		
	}
	
	/** Override of the method run fron java.lang.Runnable, activates the required service*/
	
	public void run() {
		try {
			if(Utilities.convertOption(args[0])==3) {
				massDecode();
			} else
					massEncode();
		} catch (IllegalNumberOfArgumentsException e) {
			System.out.println(e.getMessage());
		}
	}
	
	
	/** 
	 * Encodes the folder tree png files with the argument message, keeps running until the tree is completely traversed or the depth is reached.
	 * 
	 * @param folderContents the contents of the targeted folder
	 * @exception IllegalNumberOfArgumentsException Exception thrown when the number of arguments is too low to function.
	 */
	private void massEncode() throws IllegalNumberOfArgumentsException {
		
		if(args.length<4)
			throw new IllegalNumberOfArgumentsException("Wrong number of arguments for mass encoding\n"
					+ Utilities.printUsage());
		
		String[] folderContents=startFolder.list();
		for(String str: folderContents) {
			str=startFolder.getAbsolutePath()+"/"+str;
			
					if (Integer.parseInt(args[2]) != 0) {
					
						 if (!iterator(str, new String[] { args[0], str,
									String.valueOf(Integer.parseInt(args[2]) - 1), args[3] })) {
							 stegCall(str, new String[] { "-e", str, args[3] });
						} 
					}
			}
				
		}
		
	/** 
	 * Decodes the folder tree png files, keeps running until the tree is completely traversed or the depth is reached.
	 * 
	 * @param folderContents the contents of the targeted folder
	 * @exception IllegalNumberOfArgumentsException Exception thrown when the number of arguments is too low to function.
	 */
	
	
	private void massDecode() throws IllegalNumberOfArgumentsException {
		if(args.length<3)
			throw new IllegalNumberOfArgumentsException("Wrong number of arguments for mass encoding\n"
					+ Utilities.printUsage());
		String[] folderContents=startFolder.list();
		for(String str: folderContents) {
			str=startFolder.getAbsolutePath()+"/"+str;
				if (Integer.parseInt(args[2]) != 0) {
						if (!iterator(str, new String[] { args[0], str, String.valueOf(Integer.parseInt(args[2]) - 1) })) {
							stegCall(str, new String[] { "-d", str });
						}

					} 
				}
			
		}
	
	/** 
	 * Analyzes the file path passed and returns whether it's a folder or a file, if it's a folder it starts another thread onto that folder.
	 * 
	 * 
	 * @param file The file to check.
	 * @param argsTP Arguments to pass to the new thread.
	 * @return <b>boolean</b> Returns whether the file is a folder or not.
	 */
	
	private boolean iterator(String file, String[] argsTP){
		if ((new File(file)).isDirectory() && Integer.parseInt(args[2]) != 0) {
			try {
				Thread t = new Thread(new StScanner(argsTP));
				t.start();
				t.join();
				return true;
			} catch (Exception e) {
				System.out.println(e.getMessage());
				return true;
			}
		}
		else
			return false;
	}
	
	/** 
	 * Analyzes the file path passed and, if it is a png file, runs a Steganographer on it
	 * 
	 * 
	 * @param file The file to check.
	 * @param argsTP Arguments to pass to the new thread.
	 */
	
	private void stegCall(String file, String[] argsTP) {
		 if (file.substring(file.lastIndexOf(".")).compareTo(".png") == 0
					&& Integer.parseInt(args[2]) != 0) {
				try {
					Thread t = new Thread(new Steganographer(argsTP));
					t.start();
					t.join();
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			} 
	}
	
	
}
