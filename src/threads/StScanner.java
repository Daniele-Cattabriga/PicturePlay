package threads;

import java.io.File;
import java.io.IOException;

import exceptions.IllegalNumberOfArgumentsException;
import tools.Utilities;
public class StScanner implements Runnable {
	// first arg is the option, second arg is the start folder, third arg ONLY FOR MASS ENCODING, message
	
	private String[] args;
	private File startFolder;
	
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
