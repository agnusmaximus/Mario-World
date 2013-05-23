/* File: MarioScreens.java
 * 
 * This class defines the overall state of the game.
 * 
 * It also provides the loadup screens and menu screens.
 */

import java.awt.Color;
import java.util.*;
import acm.program.*;
import acm.graphics.*;

public class MarioScreens {
	//Possible states of the game
	public static final int MENU_STATE = 0;
	public static final int CHOOSE_LEVEL_STATE = 1;
	public static final int CHOOSING_LEVEL_STATE = 2;
	public static final int START_LEVEL_STATE = 3;
	public static final int PLAYING_STATE = 4;
	
	//Images for the different states
	private static final String MENU_BACKGROUND = "ScreenPieces/menubg.jpeg";
	private static final String MENU_TITLE = "ScreenPieces/menuTitle.png";
	
	//Menu strings
	private static final String PLAY_STRING = "PLAY";
	
	//Level map
	private static final String LEVEL_MAP_FILE = "levelmap.txt";
	
	//Save file
	private static final String SAVE_FILE = "levelBeaten.txt";
	
	//The state of the game
	public int overall_state;
	
	//Current chosen level
	public int chosen_level = -1;
	
	//Array of buttons
	private static final int MAX_BUTTONS = 10;
	private ArrayList<MarioButton> buttons = new ArrayList<MarioButton>(MAX_BUTTONS);
	
	//Mario sounds (primarily the click sound)
	private MarioSounds sounds = new MarioSounds();
	
	//Mario levels
	private MarioLevelMap levels = new MarioLevelMap(LEVEL_MAP_FILE, SAVE_FILE);
	
	//Instantiate the class
	public MarioScreens() {
	}

	//Draw menu onto the screen
	public void loadMenu(GraphicsProgram screen) {
		overall_state = MENU_STATE;
		
		//Create the background
		screen.add(new GImage(MENU_BACKGROUND));
		
		//Create the menu title
		GImage title = new GImage(MENU_TITLE);
		title.setLocation(screen.getWidth()/2 - title.getWidth()/2, 0);
		screen.add(title);
		
		//Create some big buttons (Play)
		buttons.add(new MarioButton(screen.getWidth()/2, screen.getHeight() - 200, 
				100, PLAY_STRING, screen));
	}
	
	/* This is for aesthetic purposes: If the user drags the mouse over a button,
	 * the button will be highlighted
	 * 
	 * @param ptX and ptY specify mouse coordinates
	 * @param screen is the screen. This object is used from the getElementAt function
	 */
	public void pointerInfo(double ptX, double ptY) {
		//Highlight buttons
		highlightButtons(ptX, ptY);
		
		//Highlight locations
		levels.mouseHoverOver(ptX, ptY);
	}
	
	private void highlightButtons(double x, double y) {
		if (buttons == null)
			return;
		for (MarioButton but : buttons) {
			if (but.label.getBounds().contains(x, y - but.label.getHeight()/2))
				but.label.setColor(Color.red);
			else 
				but.label.setColor(Color.black);
		}
	}
	
	/* This method is called whenever the user clicks. The MouseEvent x y info
	 * will be passed to this method to determine which button was pressed.
	 * 
	 * Return true if the user clicked a button -- false if otherwise
	 */
	public void clickInfo(double ptX, double ptY) {
		//What state is the controller in?
		if (overall_state == MENU_STATE) {
			GLabel pressed = labelClicked(ptX, ptY);
			if (pressed != null) {
				//Check what the clicked label says...
				if (pressed.getLabel() == PLAY_STRING) 
					overall_state = CHOOSE_LEVEL_STATE;
			
				//Make a click sound to notify the user that he
				//clicked something
				sounds.clickSound();
			}
		}
		else if (overall_state == CHOOSING_LEVEL_STATE) {
			if ((chosen_level = levels.clickedLocation(ptX, ptY)) > 0) {
				//Start the level
				overall_state = START_LEVEL_STATE;
				sounds.clickSound();
			}
			else {
				//User chose a level that he hasn't gotten to yet
				sounds.negativeSound();
			}
		}
	}
	
	/* Return the label that was pressed -- null otherwise */
	private GLabel labelClicked(double x, double y) {
		for (MarioButton but : buttons) {
			if (but.label.getBounds().contains(x, y))
				return but.label;
		}
		return null;
	}
	
	/* This method loads the level map */
	public void loadLevelMap(GraphicsProgram screen) {
		//Clear the screen first
		screen.removeAll();
		
		//Display the map
		levels.displayLevelMap(screen);
	}
	
	/* This method is called whenever the user beats a level. */
	public void levelUp() {
		levels.levelUp(chosen_level, SAVE_FILE);
		levels.updateLevelOn(SAVE_FILE);
	}
}
