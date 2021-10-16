package threads;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.BitSet;
import exceptions.MessageDoesNotExistException;
import tools.Utilities;
import exceptions.IllegalNumberOfArgumentsException;
import exceptions.ImageSizeException;

import javax.imageio.ImageIO;
/**
 * <p>
 * This class represents a practice implementation of a steganographer using threads.
 * It encodes messages by this format: prefix+message+\0, where the prefix is the string
 * "msg".
 * This prefix is inserted in the first 8 rgb pixels of the chosen picture and checked for
 * when the decode method is called on said picture.
 * </p>
 * <p>
 * In order to inject the new bits in the rgb of the picture, first off the object checks
 * for adequate size, then it produces an injector integer composed by binary sum by the bits
 * of the message characters shifted depending on their position in the original message
 * (for example, 01011011, 0 is shifted by 16, 1 by 8, 1 isn't, and repeat for the next 3 bits
 * in the next pixel), which is then binary summed to the original rgb value masked by a
 * constant used to clear the values of the last red, green and blue bits.
 * This new value is then setted in the targeted pixel position and the picture is updated.
 * </p>
 * <p>
 * The decoding process is essentially the same, but in reverse.
 * </p>
 * <p>
 * Finally, by implementing runnable we can instantiate the class as a thread, which allows for
 * proper, simultaneous picture injection and extraction if a proper background structure is 
 * erected.
 * </p>
 * 
 * @author Daniele Cattabriga
 * @version 1.0
 * @since 16-10-2021
 * @param imageFile File attribute describing the currently targeted picture.
 * @param image The targeted image loaded as an editable object.
 * @param args The arguments passed by terminal.
 *
 */
public class Steganographer implements Runnable {

	private File imageFile;
	private BufferedImage image;
	private String[] args;
	
	/**
	 * This is the standard constructor of the Steganographer class, it checks whether the 
	 * number of passed arguments is correct, then it prepares the picture for edits.
	 * 
	 * @param args Array of arguments passed by terminal.
	 * @exception IllegalNumberOfArgumentException This exception is thrown when the number of arguments 
	 * passed aren't enough for the proper function of even just the decoder.
	 * @exception IOException This exception is the standard thrown exception from the ImageIO
	 * methods. It is re-thrown for upper level management.
	 */
	
	public Steganographer(String[] args) throws IllegalNumberOfArgumentsException, IOException {
		
			if (args.length>1) {
				this.imageFile=new File(args[1]);
				this.image = ImageIO.read(imageFile);
				this.args=args;
			}
			else
				throw new IllegalNumberOfArgumentsException("Wrong number of arguments\n"
						+ Utilities.printUsage());
	}
	
	/**
	 * Implementation of the run method from java.lang.Runnable interface.
	 * 
	 * @exception IllegalNumberOfArgumentsException Internally thrown exception, same as constructor,
	 * but with tighter requests for the usage of the encoder.
	 *  
	 */
	
	public void run() {
		
		if(Utilities.convertOption(args[0])==1) {
			try {
				if(args.length<3)
					throw new IllegalNumberOfArgumentsException("Wrong number of arguments for encoding\n"
							+ Utilities.printUsage());
				encoder(args[2]);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		} else
		{
			if(Utilities.convertOption(args[0])==2) {
				try {
					decoder();
				} catch (MessageDoesNotExistException e) {
					System.out.println(e.getMessage());
				}
			}
			
		}
		
		
		
	}
	
	/**
	 * This method encodes a message in the chosen picture, first by masking the original lower bits values
	 * with 0, clearing them, followed by the new bits injection (sequential injection, since 8 mod 3 != 0
	 * compartmentalization isn't possible) and picture update.
	 * 
	 * @param msg Message to encode.
	 * @param x X coordinate of the picture.
	 * @param y Y coordinate of the picture.
	 * @param injector Buffer for the 3 bits to inject in the pixel currently targeted by x and y
	 * @param buffer Buffer for the masked picture.
	 * @exception ImageSizeException Thrown when the size of the picture isn't large enough to
	 * accomodate the message to be encoded.
	 * @exception IOException Standard input-output exception.
	 */
	
	private void encoder(String msg) throws ImageSizeException, IOException {
		int x=0;
		int y=0;
		int injector=0;
		int buffer=0;
		System.out.println("Encoding...");
		msg="msg"+msg+"\0";
		if (msg.length() * 8 > (image.getWidth() * image.getHeight() * 3)) {
			throw new ImageSizeException("Image too small, message can't be encrypted in it\n");
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
	
	/**
	 * This method reverses the operation performed by the encoder, and prints out 
	 * the message encoded in the picture. It can only read messages encoded by this 
	 * Steganograph, if the required prefix is missing, the message will be registered as nonexistent.
	 * 
	 * @param curDecoded Character currently decoded, it is added recursively to the complete string.
	 * It is also used as a check for the decoding loop, once this parameter value equals the string terminator character
	 * value it ends the decoding loop.
	 * @param completeString Complete string decoded from the picture.
	 * @param byteArray Bits to be parsed in a character.
	 * @exception MessageDoesNotExistException Thrown when the decoder doesn't detect
	 * a message encoded by this Steganographer.
	 *  
	 */
	
	private void decoder() throws MessageDoesNotExistException {
		String curDecoded="enable";
		String completeString="";
		// for ease of use, next version might turn in array of length 8
		ArrayList<Byte> byteArray=new ArrayList<Byte>();
		int j=0;
		int x=(("msg".length()*8)/3);
		int y=0;
		byte[] buffer;
		System.out.println("Decoding...");
		if(!checkForMessageExistance())
			throw new MessageDoesNotExistException("In the chosen picture there isn't any message encoded by this steganograph\n");
		
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
			if((x++)==(image.getWidth()-1)) {
				x=0;
				y++;
			}
			
			
		}
		
		System.out.println(completeString);
	}
	
	// i'm gonna try and build this on the assumption that
	// converting byte to bit takes the eigth, unexisting, 0
	// from the ASCII-US format
	
	/**
	 * This method decodes the first 8 pixels checking for the presence of an encoded message.
	 * 
	 * @param complete Result of the decoding.
	 * @param buffer Bits to be decoded.
	 * @param buffer2 Bits produced by the pixel decompiler.
	 * @return <b>boolean</b> True if a message is present, false if it isn't.
	 */
	
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
	
	
	/**
	 * This method generates a character from bits passed as argument and returns it.
	 * 
	 *  @param toConvert Array of bits to encode in a character.
	 *  @param buffer Buffer containing the complete value to encode.
	 *  @exception UnsupportedEncodingException Standard encoding exception.
	 *  @return <b>String</b> Generated character, packaged as string.
	 */
	
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
	
	
	/**
	 * Decompiles bits from the argument rgb value.
	 * 
	 *  @param rgb Value to decompile.
	 *  @return <b>result</b> Array containing decompiled bits
	 */
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
	
	/**
	 * Method used to in-place reverse the array in order to have it cope better with the
	 * little-endian generation of the BitSet.valueOf method.
	 * 
	 * @param arr Array to flip.
	 * @return <b>arr</b> Original array, but reversed.
	 */
	
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