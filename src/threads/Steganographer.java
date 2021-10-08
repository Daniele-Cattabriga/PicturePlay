package threads;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import exceptions.MessageDoesNotExistException;

import javax.imageio.ImageIO;
public class Steganographer implements Runnable {

	private File imageFile;
	private BufferedImage image;
	private String[] args;
	public Steganographer(String[] args) throws IOException {
		
			if (args.length==2 || args.length==3) {
				this.imageFile=new File(args[1]);
				this.image = ImageIO.read(imageFile);
				this.args=args;
			}
			else
				throw new IOException("Wrong number of arguments, usage: argument vector with option, imgpath, [message to encode]");
	}
	
	public void run() {
		
		if(args[0].compareTo("-e")==0) {
			try {
				encoder(args[2]);
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		} else
		{
			if(args[0].compareTo("-d")==0) {
				try {
					decoder();
				} catch (MessageDoesNotExistException e) {
					System.out.println(e.getMessage());
				}
			}
			else
				System.out.println("Wrong option, options supported are: -e for encoding, -p for decoding");
		}
		
		
		
	}
	
	private void encoder(String msg) throws IOException {
		int x=0;
		int y=0;
		int injector=0;
		int buffer=0;
		
		msg="msg"+msg+"\0";
		if (msg.length() * 8 > (image.getWidth() * image.getHeight() * 3)) {
			throw new IOException("Image too small, message can't be encrypted in it\n");
		}
		ArrayList<Integer> bitsBuffer=new ArrayList<Integer>();
		try {
			byte[] encoding=msg.getBytes("US-ASCII");
			encoding=flipper(encoding);
			BitSet bs=BitSet.valueOf(encoding);
			for(int i=bs.length(); i> -1;i--) {
				if(bs.get(i)) 
					bitsBuffer.add(1);
					else
						bitsBuffer.add(0);
			};
			for(int i=0; i<bitsBuffer.size();i=i+3) {
				try {
					buffer=(image.getRGB(x, y) & 0xFFFEFEFE);
					injector=(bitsBuffer.get(i)<< 16);
					injector=injector | (bitsBuffer.get(i+1)<<8);
					injector=injector | bitsBuffer.get(i+2);
					buffer=injector |buffer;
					image.setRGB(x, y, buffer);
					x++;
					if (x==image.getWidth()) {
						x=0;
						y++;
					}
				} catch (IndexOutOfBoundsException e) {
					injector=injector |buffer;
					image.setRGB(x, y, injector);
					image.flush();
				}
			}
			
			ImageIO.write(image, "png", imageFile);
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void decoder() throws MessageDoesNotExistException {
		String curDecoded="enable";
		String completeString="";
		ArrayList<Byte> byteArray=new ArrayList<Byte>();
		int j=0;
		int x=(("msg".length()*8)/3);
		int y=0;
		byte[] buffer;
		
		if(!checkForMessageExistance())
			throw new MessageDoesNotExistException("Nella foto selezionata non è presente un messaggio codificato da questo steganografo\n");
		
		while(curDecoded.compareTo("\0")!=0) {
			buffer=decompiler(image.getRGB(x, y));
			
			for (int i=0;i<3;i++) {
				byteArray.add(buffer[i]);
				j++;
				if(j==8){
					try {
						curDecoded=stringParser(byteArray);
						completeString=completeString+curDecoded;
						byteArray.clear();
						j=0;
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}
			if((x++)==image.getWidth()) {
				x=0;
				y++;
			}
			
			
		}
		
		System.out.println(completeString);
	}
	
	// i'm gonna try and build this on the assumption that
	// converting byte to bit takes the eigth, unexisting, 0
	// from the ASCII-US format
	
	private Boolean checkForMessageExistance() {
		String complete="";
		ArrayList<Byte> buffer=new ArrayList<Byte>();
		byte[] buffer2;
		int i=0;
		for(int x=0;x< (("msg".length()*8)/3);x++) {
			buffer2=decompiler(image.getRGB(x, 0));
			for(Byte b: buffer2) {
				buffer.add(b);
				i++;
				if (i==8) {
					try {
						complete=complete+stringParser(buffer);
						i=0;
						buffer.clear();
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
			
				
			}
		}
		if(complete.compareTo("msg")==0)
			return true;
		else
			return false;
		
	}
	
	private String stringParser(ArrayList<Byte> toConvert) throws UnsupportedEncodingException {
		int buffer=0;
		byte[] bt= new byte[1];
		for(Byte b:toConvert) {
			buffer= buffer<<1;
			buffer= buffer | b;
		}
		buffer=buffer & 0xFF;
		bt[0]=(byte)buffer;
		return new String(bt, "US-ASCII");
	
		
		
		
		
	}
	
	private byte[] decompiler(int rgb) {
		rgb=rgb &0x00010101;
		byte[] result= new byte[3];
		for(int i=0; i<3; i++) {
			rgb=rgb<<8;
			if((rgb&0x01000000)==0) {
				result[i]=0;
			}
			else
				result[i]=1;
		}
		return result;
	}
	
	private byte[] flipper(byte[] arr) {
		int j=arr.length-1;
		byte temp;
		for(int i=0; i<j; i++) {
			temp=arr[i];
			arr[i]=arr[j];
			arr[j]=temp;
			j--;
		}
		return arr;
	}
}