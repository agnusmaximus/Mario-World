/* File: MarioButton.java
 * 
 * This file creates a button with a mario font.
 */

import acm.program.*;
import acm.graphics.*;
import java.awt.*;
import java.io.*;

public class MarioButton {
	
	//The font file
	private static final String fontFile = "tlpsmb.ttf";
	
	//The actual font
	private Font marioFont;
	
	//The label
	public GLabel label;
	
	//Instantiate the button.
	public MarioButton(int x, int y, int fontsize, 
			String message,
			GraphicsProgram screen) {
		//Create the mario font
		File data = new File(fontFile);
		
		try {
			marioFont = Font.createFont(Font.TRUETYPE_FONT, data);
		}
		catch(Exception e) {
			System.out.println("Error creating Mario font" + e.getMessage());
		}
		
		//Create the message
		label = createLabel(x, y, fontsize, message);
		screen.add(label);
	}
	
	//This method creates a label at the specified position, and returns it
	private GLabel createLabel(int x, int y, 
			int fntsz, String message) {
		GLabel newLabel = new GLabel(message);
		
		//Set the label's font
		newLabel.setFont(marioFont.deriveFont((float)fntsz));
		
		newLabel.setLocation(x - newLabel.getWidth()/2, y + newLabel.getHeight()/2);
		
		return newLabel;
	}
}
