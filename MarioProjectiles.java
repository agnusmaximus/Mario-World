/* File: MarioProjectiles.java
 * 
 * Represents the projectiles that mario and other entities shoot.
 * 
 * NOTE: This isn't complete. With more time, cool projectiles can be added.
 */


public class MarioProjectiles extends MarioObjects {
	
	//Projectile defines
	private static final int FIREBALL = 1; 
	
	private static int projectileType;
	
	//Create the class
	public MarioProjectiles(String projectileImage) {
		super(projectileImage);
		projectileTypeFromImage(projectileImage);
	}
	
	//Set the projectileType
	private void projectileTypeFromImage(String image) {
		if (image.equals("Items/fireball.png"))
			projectileType = FIREBALL;
	}
}
