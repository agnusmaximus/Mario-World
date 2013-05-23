/* File: MarioObjects.java
 * 
 * This class defines every thing in the mario 
 * world that can jump, move, fall, etc.
 * 
 * These include beings like Mario himself.
 * 
 * This class will contain public flags that allow for customization for
 * child objects.
 */

import acm.graphics.*;

public class MarioObjects extends GImage {
	//All avatars have a y and x velocity (jump, move)
	public double vely = 0;
	public double velx = 0;
	
	public boolean onGround = false;
	public boolean shouldSlide = false;
	
	public MarioObjects(String image) {
		super(image);
	}
}
