/* File: MarioMap.java
 * 
 * Loads mario maps
 */

import acm.program.*;
import acm.graphics.*;
import java.util.*;
import java.io.*;


public class MarioMap {
	//Map max dimension
	private static final int MAX_MAP_DIMENSIONS = 1024;
	
	//Max enemies
	private static final int MAX_ENEMIES = 100;
	
	//Normal block dimensions
	private static final int BLOCK_DIMENSIONS = 32;
	
	//Constants that define what each number represents
	private static final int EMPTY = 0;
	private static final int BLOCK = 1;
	private static final int ITEM_BLOCK = 2;
	private static final int ENEMY = 3;
	private static final int MARIO = 4;
	private static final int FLAG = 5;
	
	//The map
	private int [][] map = new int[MAX_MAP_DIMENSIONS][MAX_MAP_DIMENSIONS];
	private ArrayList<MarioBlock> blocks = new ArrayList<MarioBlock>(MAX_MAP_DIMENSIONS);
	private int mapHeight = 0;
	
	//Enemies
	private ArrayList<MarioObjects> enemies = new ArrayList<MarioObjects>(MAX_ENEMIES);
	
	//The background
	private GImage background;
	
	//Instantiate the class
	public MarioMap(String mapFile, String backgroundFile) {
		//Load background image
		loadBGImage(backgroundFile);
		
		//Load map from filename
		loadMap(mapFile);
	}
	
	//Loads the map by reading the text file
	private void loadMap(String fileName) {
		try {
			//Open data file and read it line by line into the map array
			BufferedReader data = new BufferedReader(new FileReader(fileName));
			
			int y = 0;
			
			String line;
			while ((line = data.readLine()) != null) {
				for (int x = 0; x < line.length(); x++) {
					char element = line.charAt(x);
					map[y][x] = (int)element - 48;	//Convert char to int
				}
				y++;
			}
			
			//Get the map height
			mapHeight = y;
		}
		catch(IOException e) {
			System.err.println("IOException caught");
		}
	}
	
	//Loads the background image file
	private void loadBGImage(String fileName) {
		background = new GImage(fileName);
	}
	
	//Draws the map as well as the background onto the screen.
	public void drawMap(GraphicsProgram screen) {
		//Load background
		background.setSize(screen.getWidth(), screen.getHeight());
		screen.add(background);
		
		//Load tiles
		for (int x = 0; x < MAX_MAP_DIMENSIONS; x++) {
			for (int y = 0; y < MAX_MAP_DIMENSIONS; y++) {
				MarioBlock element = null;
				
				//Get the y offset
				int yOffs = screen.getHeight() - mapHeight * 32;
				
				//Draw the element at each index
				switch(map[x][y]) {
				case EMPTY:
					break;
				case BLOCK:
					element = new MarioBlock("MapPieces/Block.png");
					blocks.add(element);
					break;
				case ITEM_BLOCK:
					element = new MarioBlock("MapPieces/Itembox.PNG");
					blocks.add(element);
					break;
				case ENEMY:
					enemies.add(new MarioEnemy(y * BLOCK_DIMENSIONS - y,
				            (x + 1) * BLOCK_DIMENSIONS + yOffs + 2 * (mapHeight - x) - 35, screen));
					break;
				case FLAG:
					element = new MarioBlock("MapPieces/Flag.png");
					blocks.add(element);
					break;
				}
				if (element != null) {
					//This part below makes sure that the bottom blocks are 
					//at the bottom of the screen
					//(Below is deprecated)
					//element.setLocation(y * element.getWidth() - y,
				    //       x * element.getHeight() + yOffs + 2 * (mapHeight - x));
					element.setLocation(y * BLOCK_DIMENSIONS - y,
				            (x + 1) * BLOCK_DIMENSIONS + yOffs + 2 * (mapHeight - x) - element.getHeight());
	
					screen.add(element);
				}
			}
		}
	}
	
	//This method returns the starting position of mario
	//Assumes that the file is correct and has a mario position
	public GPoint marioPosition() {
		//Loop through whole map and look for mario position
		for (int x = 0; x < MAX_MAP_DIMENSIONS; x++) {
			for (int y = 0; y < MAX_MAP_DIMENSIONS; y++) {
				if (map[x][y] == MARIO) {
					return new GPoint(y * 32 - y, x * 32 + 14);
				}
			}
		}
		return null;
	}
	
	//This method returns all the item blocks and blocks in the map
	public ArrayList<MarioBlock> getAllBlocks() {
		return blocks;
	}
	
	//Returns all enemies on map
	public ArrayList<MarioObjects> getAllEnemies() {
		return enemies;
	}
}
