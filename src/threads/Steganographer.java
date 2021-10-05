package threads;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;

import javax.imageio.ImageIO;
public class Steganographer implements Runnable {

	private BufferedImage image;
	private String msg;
	public Steganographer(String imgPath, String msg) throws IOException {

			this.image = ImageIO.read(new File(imgPath));
			this.msg="msg "+msg+"\0";
			if(msg.length()*8>(image.getWidth()*image.getHeight()*3)) {
				throw new IOException("Image too small, message can't be encrypted in it\n");
			}
	}
	/*one word: bitmask with standard binary conversion*/
	public void run() {
		int x=0;
		int y=0;
		try {
			byte[] encoding=msg.getBytes("US-ASCII");
			BitSet bs=BitSet.valueOf(encoding);
			for(int i=0; i< bs.length();i++) {
				if(bs.get(i)) 
					System.out.println(1);
					else
						System.out.println(0);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
			
		
	}

}





/*
 * Boolean finish=false;
			ArrayList<Integer> RGBs= new ArrayList<Integer>();
			for(int i=0;i<image.getHeight() && !finish;i++) {
				for(int j=0;j<image.getWidth() && !finish;j++) {
					int color=image.getRGB(j, i);
					RGBs.add((color & 0xff0000) >>16);  // >> is a bitwise shifter, removes zeroes and leaves number
					RGBs.add((color & 0xff00) >>8);
					RGBs.add(color & 0xff);
					if ((i*j)*3>=msg.length()*8) {
						finish=true;
					}
				}
			}
			*/
//System.out.format("RGB of %d, %d is: RED:%d GREEN:%d BLUE:%d \n",i,j,red, green, blue);
//System.out.format("The last bit of the RGB values are: RED:%d GREEN:%d, BLUE:%d \n", red>>7, green>>7,blue>>7);

//
//System.out.println(encoding[0]+" "+((encoding[0])& 0x80));
//System.out.println(image.getRGB(50, 50));
//System.out.println(image.getRGB(0, 0));