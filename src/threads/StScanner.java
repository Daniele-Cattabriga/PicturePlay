package threads;

import java.io.File;
import java.io.IOException;

import exceptions.IllegalNumberOfArgumentsException;
import tools.Utilities;
public class StScanner implements Runnable {
	// first arg is the option, second arg is the start folder, third arg ONLY FOR MASS ENCODING, message
	
	private String[] args;
	private File startFolder;
	
	public StScanner(String[] args) throws IOException
	{
		startFolder=new File(args[1]);
		if(!startFolder.exists()||!startFolder.isDirectory()||args.length<3)
			throw new IOException("The specified start folder does not exist or the number of arguments is too low.\n"
					+ "Usage: PicturePlay.jar option(-md,-me) startfolder depth [message to mass encode]");
		this.args=args;
		
	}
	
	
	public void run() {
		
		
		if(Utilities.convertOption(args[0])==3) {
			massDecode();
		} else
			try {
				massEncode();
			} catch (IllegalNumberOfArgumentsException e) {
				System.out.println(e.getMessage());
			}
	}
	
	private void massEncode() throws IllegalNumberOfArgumentsException {
		
		if(args.length<4)
			throw new IllegalNumberOfArgumentsException("Wrong number of arguments for mass encoding\n"
					+ "Usage: PicturePlay.jar option(either -md,-me) startfolder depth [message to mass encode]");
		
		String[] folderContents=startFolder.list();
		for(String str: folderContents) {
			str=startFolder.getAbsolutePath()+"/"+str;
			if(str.substring(str.lastIndexOf(".")).compareTo("png")==0) {
				try {
					Thread t =new Thread(new Steganographer(new String[]{"-e", str, args[3]}));
					t.start();
					t.join();
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
			else
				if((new File(str)).isDirectory() && Integer.parseInt(args[2]) != 0) {
					try {
						Thread t=new Thread(new StScanner(new String[]{args[0], str, String.valueOf(Integer.parseInt(args[2])-1), args[3]}));
						t.start();
						t.join();
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
				}
		}
		
	}
	
	private void massDecode() {

		String[] folderContents=startFolder.list();
		for(String str: folderContents) {
			str=startFolder.getAbsolutePath()+"/"+str;
			if(str.substring(str.lastIndexOf(".")).compareTo("png")==0) {
				try {
					Thread t =new Thread(new Steganographer(new String[]{"-d", str}));
					t.start();
					t.join();
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
			else
				if((new File(str)).isDirectory() && Integer.parseInt(args[2]) != 0) {
					try {
						Thread t=new Thread(new StScanner(new String[]{args[0], str, String.valueOf(Integer.parseInt(args[2])-1)}));
						t.start();
						t.join();
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
				}
		}
	}
	
	
}
