/* File: MarioItems.java
 * 
 * This class extends Mario Objects because items have velocities
 * and are as well affected by gravity.
 */

import acm.program.*;
import acm.util.*;

public class MarioItems extends MarioObjects {

	//Constants that define the flags of every type of item
	public static final int MUSHROOM = 1;
	public static final int FLAME_FLOWER = 2;
	
	//Different files that references the images of items.
	private static final String MUSH_IMAGE = "Items/Mario-Mushroom.png";
	private static final String FLAME_FLOWER_IMAGE = "Items/FlameFlower.png";
	
	//Possible item velocity
	private static final double ITEM_VEL = .15;
	
	//This variable specifies what the Mario item is
	private static int itemType;
	
	private static RandomGenerator itemGen = RandomGenerator.getInstance();
	
	//Instantiation
	public MarioItems(double x, double y, GraphicsProgram screen) {
		//Dont know the item image yet.
		super(MUSH_IMAGE);
				
		//Set it now
		setImage(randomItem());
		
		//Set the velocity for the item
		velocityForItem();
		
		//Set the item position
		setLocation(x, y);
				
		//Add item to screen
		screen.add(this);
	}
	
	/*This method returns a random string referencing a random item.
	 *Assumes that the flag that represents the item will be set as the item
	 *type for the class.
	 */
	private String randomItem() {
		//This line below returns a random item flag
		itemType = itemGen.nextInt(MUSHROOM, FLAME_FLOWER);
		
		//Next determine which image is appropriate for that item type
		switch(itemType) {
		case MUSHROOM:
			return MUSH_IMAGE;
		case FLAME_FLOWER:
			return FLAME_FLOWER_IMAGE;
		}
		return null;
	}
	
	/* This method evaluates what type of boost-up this item is,
	 * and appropriately sets a velocity based on this item.
	 */
	private void velocityForItem() {
		switch(itemType) {
		case MUSHROOM:
			//Velocity is a random integer
			velx = ITEM_VEL;
			if (itemGen.nextBoolean(.5))
				//Switch velocity
				velx *= -1;
			
			//The mushroom should continually move until off screen
			shouldSlide = true;
			break;
		case FLAME_FLOWER:
			shouldSlide = false;
			break;
		}
	}
	
	/* This method returns what type of item the class represents */
	public int getItemType() {
		return itemType;
	}
}
