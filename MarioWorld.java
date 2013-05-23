/*
 * File: MarioWorld.java
 * ---------------------
 * Name : Max Lam
 * Section leader : Alex 
 *
 * This is a simple mario game
 * 
 */

import acm.program.*;

import java.awt.MouseInfo;
import java.awt.event.*;

public class MarioWorld extends GraphicsProgram {
	//Interval
	private static final double UPD_INTERVAL = .5;
	private static final double LVL_INTERVAL = 4500;
	
	//Instance variables
	private MarioScreens controller;
	private MarioAvatar mario;
	private MarioMap map;
	private MarioLogic world;
	private MarioSounds sounds;
	
	public void run() {
		//Need key and mouse listeners
		addKeyListeners();	
		addMouseListeners();
		
		//Create a new controller to control the state
		//of the whole game
		controller = new MarioScreens();
		controller.loadMenu(this);
		
		//The essential game loop
		while (true) {
			//Controller says to start playing the level
			if (controller.overall_state == MarioScreens.START_LEVEL_STATE) {
				//Now playing the game
				controller.overall_state = MarioScreens.PLAYING_STATE;
							
				//start the level
				if (level("Levels/" + controller.chosen_level + ".txt"))
					controller.levelUp();
				
				//Set the state back to the level select state
				controller.overall_state = MarioScreens.CHOOSE_LEVEL_STATE;
			}
			
			//Controller says to choose the level
			if (controller.overall_state == MarioScreens.CHOOSE_LEVEL_STATE) {
				//Load the level map
				controller.loadLevelMap(this);
				
				//Now chossing state
				controller.overall_state = MarioScreens.CHOOSING_LEVEL_STATE;
			}
			
			//Pass pointer position to the controller.
			controller.pointerInfo(MouseInfo.getPointerInfo().getLocation().getX(), 
					MouseInfo.getPointerInfo().getLocation().getY());
		}
	}
	
	/* Initialize everything (for a level)*/
	private void initializeLevel(String fileMap) {
		//Mario map
		map = new MarioMap(fileMap, "MapPieces/MarioBackground.jpg");
		map.drawMap(this);
		
		//Mario avatar
		mario = new MarioAvatar();
		mario.createMario(this, map.marioPosition());
	
		//Mario physics/logic
		world = new MarioLogic(map.getAllBlocks());
		world.addToGravity(mario);
		world.addEnemies(map.getAllEnemies());
		
		//Mario sounds
		sounds = new MarioSounds();
		sounds.playThemeMusic();
	}
	
	/* De-initializes level so that the game is ready for next level */
	private void deinitializeLevel() {
		map = null;
		mario = null;
		world = null;
		sounds = null;
	}
	
	/* Level loop 
	 * 
	 * 
	 * @return true if the user beat the level; false otherwise
	 */
	private boolean level(String mapForLevel) {
		//Remove everything from screen
		this.removeAll();
		
		//Inititialize the level
		initializeLevel(mapForLevel);
		
		//Flag that determines if the user beat level
		boolean didBeatLevel = false;
		
		//Level loop
		while(true) {
			//Update objects
			world.updateGravityAffectedObjs(this);
			
			//Check if mario picked up items
			if (mario.pickItemsUp(this))
				sounds.powerupSound();
			
			//Check if mario killed enemies
			if (mario.marioVsEnemies(map.getAllEnemies())) {
				sounds.smoosh();
			}
			
			//Check if mario died
			if (mario.dead(this)) {
				sounds.deadSound();
				break;
			}
			
			//Check if mario completed the level
			if (mario.complete(this)) {
				//Player beat level
				didBeatLevel = true;
				sounds.completeSound();
				break;
			}
			
			//Pause for a moment
			pause(UPD_INTERVAL);
		}
		
		//Game over. Pause extra seconds
		pause(LVL_INTERVAL);
		
		//After level, clean up
		cleanLevelUp();
		
		//Did the user beat the level
		return didBeatLevel;
	}
	
	/* Clear screen */
	private void cleanLevelUp() {
		//Deinitialize
		deinitializeLevel();
		
		//Clear screen	
		this.removeAll();
	}
	
	/* Handle moving mario */
	public void keyPressed(KeyEvent e) {
		//Key controlls available only when user is playing
		if (controller.overall_state == MarioScreens.PLAYING_STATE) {
			
			switch(e.getKeyCode()) {
			case KeyEvent.VK_RIGHT:
				mario.move(MarioAvatar.MOVE_RIGHT);
				mario.animateMario(MarioAvatar.MOVE_RIGHT);
				break;
			case KeyEvent.VK_LEFT:
				mario.move(MarioAvatar.MOVE_LEFT);
				mario.animateMario(MarioAvatar.MOVE_LEFT);
				break;
			case KeyEvent.VK_UP:
				if (mario.jump())
					sounds.jumpSound();
				break;
			}
		}
		
		//Key controls for menu management
		if (controller.overall_state == MarioScreens.CHOOSING_LEVEL_STATE) {
			//Check if the user wants to go back to the menu
			if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
				//Clear screen
				removeAll();
				controller.loadMenu(this);
			}
		}
	}
	
	/* Handle mouse clicks */
	public void mousePressed(MouseEvent e) {
		controller.clickInfo(e.getX(), e.getY());
	}
}

