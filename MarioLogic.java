/* File: MarioLogic.java
 * 
 * This class defines mario physics, which includes mario-object collision
 * and mario fall speed.
 * 
 * It also handles camera physics, such as panning and the such
 */

import acm.program.*;
import acm.graphics.*;
import java.util.*;

public class MarioLogic {
	//Max amount of objects affected by gravity
	private static final int MAX_OBJ_AF_GRAV = 1024;
	
	//Block size
	private static final int BLOCK_SZ = 32;
	
	//Max obstacles
	private static final int MAX_OBSTACLES = 1024;
	
	//How far away from the ground is on ground?
	private static final int ON_GROUND_OFFSET = 3;
	private static final int NOT_BLOCKED_OFFSET = 3;
	private static final int STOP_OFFSET = 3;
	
	//Fall rate of objects
	private static final double FALL_RATE = .001;
	
	//Bounce degree of objects
	private static final double BOUNCE_DEG = 15;
	
	//The array holding objects affected by gravity
	private ArrayList<MarioObjects> gravityAffected = new ArrayList<MarioObjects>(MAX_OBJ_AF_GRAV);
	
	//Array holding obstacles
	private ArrayList<MarioBlock> obstacles = new ArrayList<MarioBlock>(MAX_OBSTACLES);
	private boolean [] obstacleBounces = new boolean[MAX_OBSTACLES];
	
	//Instantiation method
	public MarioLogic(ArrayList<MarioBlock> obst){
		//Initialize obstacles array list
		obstacles = obst;
	}
	
	//Add object to gravity
	public void addToGravity(MarioObjects obj) {
		//Add to array list
		gravityAffected.add(obj);
	}
	
	//Add enemies to array list
	public void addEnemies(ArrayList<MarioObjects> enem) {
		for (MarioObjects enemy : enem)
			addToGravity(enemy);
	}
	
	//Updates the y velocity of the avatars in the array list
	public void updateGravityAffectedObjs(GraphicsProgram screen) {
		//Unbounce obstacles
		unBounce(screen);
		
		for (MarioObjects obj : gravityAffected) {
			//If the object is an enemy, update it
			if (obj.getClass() == MarioEnemy.class) {
				MarioEnemy enemy = (MarioEnemy)obj;

				//Update enemy
				enemy.updateEnemy(screen.getHeight());
				
				if (!enemy.alive) {
					gravityAffected.remove(enemy);
					break;
				}
			}
			
			//Move y value
			obj.move(0, obj.vely);
			
			//Movement and collisions (as well as item spawnings)
			movementAndCollisions(obj);
			
			//camera panning
			if (obj.getClass() == MarioAvatar.class)
				cameraPanningHorizontal(obj, screen);
			
			
			//Dont go off the map.
			if (obj.getClass() == MarioAvatar.class &&
				obj.velx > 0 && 
				(obj.getX() + obj.getWidth()) >= screen.getWidth() - STOP_OFFSET)
				obj.velx = 0;
			
			//Move x value
			obj.move(obj.velx, 0);
			
			//Dont do a continuous move if the object doesn't slide
			if (!obj.shouldSlide)
				obj.velx = 0;
		}
	}
	
	/* This function handles panning cameras horizontally */
	private void cameraPanningHorizontal(MarioObjects obj, GraphicsProgram screen) {
		//Check if the object is beyond half of the screen width and mario is moving right.
		//If so, just pan the camera right instead of moving mario
		if (!atEndOfMap(screen) && obj.velx > 0 && obj.getX() > screen.getWidth()/2) {
			
			//Move every object the other way so that they stay stationary
			for (MarioObjects other : gravityAffected) {
				if (other != obj) {
					other.move(-obj.velx, 0);
				}
			}
			
			//Only pan the camera if the object is mario
			//Pan the map by velocity
			panCameraHor(obj.velx);
			obj.velx = 0;
		}
		
		//If mario is beyond half of the screen width and going left,
		//pan the screen left instead of moving mario left
		if (!atBeginningOfMap(screen) && obj.velx < 0 && obj.getX() < screen.getWidth()/2) {
			
			//Move every object the other way so that they stay stationary
			for (MarioObjects other : gravityAffected) {
				if (other != obj) {
					other.move(-obj.velx, 0);
				}
			}
			
			//Pan the camera if the object is mario
			panCameraHor(obj.velx);
			obj.velx = 0;
		}
	}
	
	/* This function handles movement and collisions */
	private void movementAndCollisions(MarioObjects obj) {
		if (obj.getClass() == MarioEnemy.class) {
			MarioEnemy enemy = (MarioEnemy)obj;
	
			//If the object is in the air, make it fall
			if (enemy.dying || !objCollideDown(enemy)) {
				obj.vely += FALL_RATE * 3;
			}
			
			//If the object hits the ground, stop it from falling more
			if (objCollideDown(enemy) && !enemy.dying) {
				obj.vely = 0;
				obj.onGround = true;
			}
			
			//If the object is moving right and hitting a wall, stop it
			//from moving right
			if (obj.velx > 0 && objCollideRight(enemy)) {
				obj.velx = -obj.velx;
			}
		
			//If the object is moving left and hitting a wall, stop it
			//from moving more left
			if (obj.velx < 0 && objCollideLeft(enemy)) {
				obj.velx = -obj.velx;
			}
		}
		else {
			//Check if the obj head-butted a block and
			//if mario has spawned an item
			if (objCollideUp(obj)) {
				obj.vely = 0;
			}
			
			//If the object is in the air, make it fall
			if (!objCollideDown(obj)) {
				obj.vely += FALL_RATE;
			}
			
			//If the object hits the ground, stop it from falling more
			if (objCollideDown(obj)) {
				obj.vely = 0;
				obj.onGround = true;
			}
			
			//If the object is moving right and hitting a wall, stop it
			//from moving right
			if (obj.velx > 0 && objCollideRight(obj)) {
				obj.velx = 0;
			}
		
			//If the object is moving left and hitting a wall, stop it
			//from moving more left
			if (obj.velx < 0 && objCollideLeft(obj)) {
				obj.velx = 0;
			}
		}
	}
	
	/* Determines if the obj collided with an obstacle to the right */
	private boolean objCollideRight(MarioObjects obj) {
		//Convert the Avatars object to a line object
		GLine objRect = new GLine(obj.getX() + obj.getWidth(),
				                  obj.getY(),
				                  obj.getX() + obj.getWidth(),
				                  obj.getY() + obj.getHeight() - ON_GROUND_OFFSET);
		GRectangle objBounds = objRect.getBounds();
		
		for (GImage obs : obstacles) {
			GRectangle obstBounds = obs.getBounds();
			if (objBounds.intersects(obstBounds)) {
				//If mario hits wall, knock him back
				obj.setLocation(obj.getX() - NOT_BLOCKED_OFFSET, obj.getY());
				return true;
			}
		}
		return false;
	}
	
	/* Determines if the obj collided with an obstacle to the left */
	private boolean objCollideLeft(MarioObjects obj) {
		//Convert the Avatars object to a line object
		GLine objRect = new GLine(obj.getX(),
                obj.getY(),
                obj.getX(),
                obj.getY() + obj.getHeight() - ON_GROUND_OFFSET);
		GRectangle objBounds = objRect.getBounds();
		
		for (GImage obs : obstacles) {
			GRectangle obstBounds = obs.getBounds();
			if (objBounds.intersects(obstBounds)) {
				//If mario hits wall, knock him back
				obj.setLocation(obj.getX() + NOT_BLOCKED_OFFSET, obj.getY());
				return true;
			}
		}
		return false;
	}
	
	/* Determines if the obj collided with an obstacle upward */
	private boolean objCollideUp(MarioObjects obj) {
		//Convert the Avatars object to a point object
		GPoint objPoint = new GPoint(obj.getX() + obj.getWidth()/2,
				                     obj.getY());
		
		for (GImage obs : obstacles) {
			GRectangle obstBounds = obs.getBounds();
			if (obstBounds.contains(objPoint)) {
				//Make the block bounce.
				bounce(obs);
								
				return true;
			}
		}
		return false;
	}
	
	/* Determines if the obj collided with an obstacle downward */
	private boolean objCollideDown(MarioObjects obj) {
		//Convert the Avatars object to a point object
		GPoint objPoint = new GPoint(obj.getX() + obj.getWidth()/2,
				                     obj.getY() + obj.getHeight());
		
		for (GImage obs : obstacles) {
			GRectangle obstBounds = obs.getBounds();
			if (obstBounds.contains(objPoint)) 
				return true;
		}
		return false;
	}
	
	/* This method determines if the whole right of the map is visible.
	 * 
	 * If so, panning the camera stops so that mario can get to the end of the map.
	 */
	private boolean atEndOfMap(GraphicsProgram screen) {
		return screen.getElementAt(screen.getWidth() - BLOCK_SZ, screen.getHeight()) == (obstacles.get(obstacles.size()-1));
	}
	
	/* This method determines if the whole left of the map is visible.
	 * 
	 * If so panning the camera to the left stops.
	 */
	private boolean atBeginningOfMap(GraphicsProgram screen) {
		return screen.getElementAt(BLOCK_SZ/2, screen.getHeight() - BLOCK_SZ/2) == (obstacles.get(1));
	}
	
	/* This method pans the camera right or left
	 */ 
	private void panCameraHor(double amount) {
		//Loop through all blocks and move by amount
		for (GImage obs : obstacles) {
			obs.move(amount * -1, 0);
		}
	}
	
	/* This method bounces an obstacle updward */
	private void bounce(GImage obj) {
		obj.move(0, -BOUNCE_DEG);
		obstacleBounces[obstacles.indexOf(obj)] = true;
		//Check if the obstacle hit an enemy
		obstacleHitEnemy(obj, -BOUNCE_DEG);
	}
	
	/* This method returns an obstacle to its unbounced state */
	private void unBounce(GraphicsProgram screen) {
		for (int i = 0; i < obstacles.size(); i++) {
			if (obstacleBounces[i] == true) {
				obstacleBounces[i] = false;
				//Bounce the block back
				obstacles.get(i).move(0, BOUNCE_DEG);
				
				//Check if mario headbutted an item block
				if (obstacles.get(i).type == MarioBlock.ITEM_BLOCK &&
					obstacles.get(i).containsItem)  {
					double x, y;
					x = obstacles.get(i).getX();
					y = obstacles.get(i).getY() - obstacles.get(i).getHeight() * 2;
					//If so, spawn an item at the obstacles' location
					addToGravity(new MarioItems(x, y, screen));
					obstacles.get(i).containsItem = false;
				}
			}
		}
	}
	
	/* This method checks if an obstacle bumped into an enemy.
	 * If so, the enemy dies.
	 */
	private void obstacleHitEnemy(GImage obst, double objVel) {
		for (MarioObjects posEnemy : gravityAffected) {
			if (posEnemy.getClass() == MarioEnemy.class) {
				//Check if the enemy intersects with the obstacle
				MarioEnemy enemy = (MarioEnemy)posEnemy;
				GRectangle enemyBounds = enemy.getBounds();
				if (enemyBounds.intersects(obst.getBounds())) {
					//Kill the enemy
					enemy.vely = objVel/10;
					enemy.dying = true;
				}
			}
		}
	}
}
