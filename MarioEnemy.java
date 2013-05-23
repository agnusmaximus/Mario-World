/* File: MarioEnemy.java
 * 
 * This class represents the enemies that Mario might
 * find in different levels.
 */

import acm.program.*;
import acm.util.*;

public class MarioEnemy extends MarioObjects {
	
	//Enemy files
	private static final String KOOPA_FILE = "EnemySprites/RKoopa1.png";
	
	//Enemy constants
	private static final int KOOPA = 1;
	
	//What frame is the enemy on
	private int frame = 1;
	
	//What type of monster does this object represent?
	public int enemy_type;
	
	public boolean alive = true;
	public boolean dying = false;
	
	private int sync = 0;
	
	private RandomGenerator enemyGen = RandomGenerator.getInstance();
	
	/* Instantiation method --
	 * Creates a random enemy at specified coordinates.
	 *
	 * @param x and y are coordinates to create the new enemy
	 * @param screen is where to draw the enemy
	 */
	public MarioEnemy(double x, double y, GraphicsProgram screen) {
		super(KOOPA_FILE);
		
		//Set location
		setLocation(x, y);
		
		//Set a random enemy image
		setImage(randomEnemy());
		
		setVelocity();
		
		//Add to screen
		screen.add(this);
	}
	
	/* This method returns an enemy image based on a randomly generated
	 * enemy-type flag
	 */
	private String randomEnemy() {
		enemy_type = enemyGen.nextInt(KOOPA, KOOPA);
		
		//Location of enemy files
		String location = "EnemySprites/";
		
		switch(enemy_type) {
		case KOOPA:
			return location + movingWhichWay() + "Koopa1.png";
		}
		return null;
	}
	
	/* This method returns which way the enemy is moving
	 * based on his velocity
	 * 
	 * returns 'R' or 'L'
	 */
	private String movingWhichWay() {
		if (velx < 0)
			return "L";
		return "R";
	}
	
	/* This method sets the monster's velocity based on
	 * what sort of creature it is
	 */
	private void setVelocity() {
		switch(enemy_type) {
		case KOOPA:
			velx = .1;
			if (enemyGen.nextBoolean(.5))
				velx *= -1;
			break;
		}
		shouldSlide = true;
	}
	
	/* This method updates the enemy,
	 * which includes frame updates, etc, etc.
	 */
	public void updateEnemy(int heightTillDead) {
		setImage("EnemySprites/" + movingWhichWay() + 
				imageFromState() + frame + ".png");

		//Check if enemy died.
		if (getY() > heightTillDead)
			alive = false;
		
		if (sync++ > 10) {
			if (frame++ >= 4)
				frame = 1;
			sync = 0;
		}
	}
	
	/* This method returns the image based upon the class' state */
	private String imageFromState() {
		switch(enemy_type) {
		case KOOPA:
			return "Koopa";
		}
		return null;
	}
}
