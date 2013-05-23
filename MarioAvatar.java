/* File: MarioAvatar.java
 * 
 * This class handles the mario animations and actions
 */

import acm.graphics.*;
import acm.program.*;
import java.util.*;

public class MarioAvatar extends MarioObjects {
	//The mario animation images
	private static final String NOOB_MARIO = "MarioSprites/RlilMarioStill.png";
	private static final String DEAD_MARIO = "MarioSprites/dead.png";

	//Possible Mario states (big, small..)
	private static final int MARIO_DEAD = 0;
	private static final int MARIO_SMALL = 1;
	private static final int MARIO_BIG = 2;
	private static final int MARIO_FLAME = 3;

	//How long til mario recovers from injury
	private static final int RECOVER_RATE = 12;
	
	//Current Mario state (big, small, etc)
	private static int mario_state = MARIO_SMALL;
	
	//Mario current frame
	private static int current_mario_frame = 1;
	
	//Mario direction
	public static final int MOVE_RIGHT = 1;
	public static final int MOVE_LEFT = -1;
	
	//Mario jump velocity
	private static final double JUMP_VEL = .7;
	private static final double MOVE_VEL = 7;

	//Mario punch velocity
	private static final double MARIO_SMOOSH_OFFSET = -.5;
	
	//Mario alive or dead
	private boolean alive = true;
	
	//Mario hurt or safe?
	private int hurt = 0;
	
	/* Instantiation */
	public MarioAvatar() {
		//Mario is small at first
		super(NOOB_MARIO);
		mario_state = MARIO_SMALL;
	}
	
	/* Create an place mario on screen */
	public void createMario(GraphicsProgram screen, GPoint location) {
		placeMario(location);
		screen.add(this);
	}
	
	/* Placement
	 */
	private void placeMario(GPoint pt) {
		setLocation(pt);
	}
	
	/* Mario jump 
	 * 
	 * Return if mario was successful in jumping
	 */ 
	public boolean jump() {
		if (onGround) {
			vely -= JUMP_VEL;
			onGround = false;
			return true;
		}
		return false;
	}
	
	/* Mario move */
	public void move(int rightOrLeft) {
		//Check bounds
		velx = MOVE_VEL * rightOrLeft;
	}
	
	/* This animates mario in a specific direction */
	public void animateMario(int leftOrRight) {		
		//Check first if mario is dead
		if (mario_state == MARIO_DEAD)
			return;
		
		String newImage = "MarioSprites/";
		if (leftOrRight == MOVE_RIGHT) {
			if (current_mario_frame > 0)
				newImage += "R" + marioWalkingImageFromState() + current_mario_frame + ".png";
			else 
				newImage += "R" + marioSedentaryImageFromState() + ".png";
		}
		else {
			if (current_mario_frame > 0)
				newImage += "L" + marioWalkingImageFromState() + current_mario_frame + ".png";
			else 
				newImage += "L" + marioSedentaryImageFromState() + ".png";
		}
		
		if (hurt > 0)
			hurt--;
		
		setImage(newImage);
		
		//Repeat frame loop
		if (current_mario_frame++ >= 3) {
			current_mario_frame = 0;
		}
	}
	
	/* This method returns mario's walking image given his state */
	private String marioWalkingImageFromState() {
		switch(mario_state) {
		case MARIO_BIG:
			return "MarioWalk";
		case MARIO_SMALL:
			return "lilMarioWalk";
		case MARIO_FLAME:
			return "FlameMarioWalk";
		}
		return null;
	}
	
	/* This method returns mario's sedentary image given his state */
	private String marioSedentaryImageFromState() {
		switch(mario_state) {
		case MARIO_BIG:
			return "MarioStill";
		case MARIO_SMALL:
			return "lilMarioStill";
		case MARIO_FLAME:
			return "FlameMarioStill";
		}
		return null;
	}
	
	/* This method sets mario to be sedentary. */
	private void sedentaryMario(int leftOrRight) {
		String newImage = "MarioSprites/";
		if (leftOrRight == MOVE_RIGHT) {
			newImage += "R" + marioSedentaryImageFromState() + ".png";
		}
		else {
			newImage += "L" + marioSedentaryImageFromState() + ".png";
		}
		setImage(newImage);
	}
	
	/* This method checks if mario has picked up any items */
	public boolean pickItemsUp(GraphicsProgram screen) {
		GObject obj;
		if ((obj = screen.getElementAt(getX() + getWidth()/2, getY() + getHeight()/2)) != null) {
			if (obj.getClass() == MarioItems.class) {
				//Pick up this item
				screen.remove(obj);
				
				MarioItems item = (MarioItems)obj;
				
				//Power up
				powerUp(item.getItemType());
				
				return true;
			}
		}
		return false;
	}
	
	/* This method checks if mario has died (mario below screen or mario state <= 0 */
	public boolean dead(GraphicsProgram screen) {
		//Check if mario fell down a pit
		return (getY() > screen.getHeight() || !alive);
	}
	
	/* This method checks if mario has completed the level */
	public boolean complete(GraphicsProgram screen) {
		GObject obj = null;
		if ((obj = screen.getElementAt(getX() + getWidth(), getY())) != null) {
			//Check if the object in front of mario is the flagpole
			if (obj.getClass() == MarioBlock.class) {
				MarioBlock flagpole = (MarioBlock)obj;
				return flagpole.type == MarioBlock.END_FLAGPOLE;
			}
		}
		//No object in front of mario
		return false;
	}
	
	/* This method powers up mario depending on what item he attained */
	private void powerUp(int item) {
		switch(item) {
		case MarioItems.MUSHROOM:
			if (mario_state < MARIO_BIG)
				mario_state = MARIO_BIG;
			break;
		case MarioItems.FLAME_FLOWER:
			mario_state = MARIO_FLAME;
			break;
		}
		//Update mario 
		sedentaryMario(MOVE_RIGHT);
		//Make sure that mario doesn't get stuck -- bump him
		//up a little
		setLocation(getX(), getY() - getHeight()/2);
	}
	
	/* This method determines if mario killed an enemy,
	 * or if the enemy killed mario
	 * 
	 * return true if mario killed an enemy -- false otherwise
	 */
	public boolean marioVsEnemies(ArrayList<MarioObjects> enemies) {
		GRectangle marioBounds = getBounds();
		
		for (MarioObjects enemy : enemies) {
			GRectangle enemyBounds = enemy.getBounds();
			MarioEnemy realEnemy = (MarioEnemy)enemy;
			
			//Check if the enemy is a healthy enemy to kill
			if (marioBounds.intersects(enemyBounds) && !realEnemy.dying) {
				if (marioSmashed(realEnemy)) {
					//The enemy is dying
					realEnemy.dying = true;
					realEnemy.vely = MARIO_SMOOSH_OFFSET;
				
					//Mario kicks up a little bit
					vely = -JUMP_VEL/2;
				
					//Remove this enemy
					enemies.remove(enemy);
					return true;
				}
				else {
					//Dont affect mario if he is hurt
					if (hurt <= 0) {
						//Mario loss a life
						if (mario_state == MARIO_SMALL) {
							alive = false;
							
							mario_state = MARIO_DEAD;
						
							//Set to dead image
							setImage(DEAD_MARIO);
						}
						else {
							mario_state = MARIO_SMALL;
							sedentaryMario(MarioAvatar.MOVE_RIGHT);
							hurt = RECOVER_RATE;
						}
					}
				}
			}
		}
		return false;
	}
	
	/* This method allows mario to attack enemies */
	public void attack() {
		//Mario state must be beyond MARIO_FLAME state to attack
		switch(mario_state) {
		case MARIO_FLAME:
			//Create a fireball.
			break;
		}
	}
	
	/* method determines if mario jumped on top of the enemy */
	private boolean marioSmashed(MarioEnemy enemy) {
		return (getY() < enemy.getY());
	}
}
