import java.io.BufferedReader;
import java.io.FileReader;

/* File: MarioLevelMap.java
 * 
 * This class specifies the map that the player sees when he 
 * selects a new level. This class differs from MarioMap in that it doesn't
 * display the level obstacles, rather the overall route that Mario takes
 * to get from level to level.
 * 
 * Users will click on a location to start that level.
 */

import acm.program.*;
import acm.graphics.*;
import java.util.*;
import java.awt.*;
import java.io.*;

public class MarioLevelMap {	
	//Background image
	private static final String BG_IMAGE = "levelMapBackground.gif";
	
	//Dimensions of a location
	private static final int LOCATION_RADIUS = 32;
	
	//How big can a level map be?
	private static final int MAX_LEVEL_DATA = 256;
	
	//How many possible locations can there be
	private static final int MAX_LOCATIONS = 9;
	
	private int [][] levelData = new int[MAX_LEVEL_DATA][MAX_LEVEL_DATA];
	private ArrayList<GOval> locations = new ArrayList<GOval>(MAX_LOCATIONS);
	
	//What level is the user on
	private int level_on = MAX_LOCATIONS;

	//Instantiation method
	public MarioLevelMap(String levelMap, String levelFile) {
		updateLevelOn(levelFile);
		
		//Read the file and scan its' data
		readFileContents(levelMap);
	}
	
	/* This method updates what level the user is on */
	public void updateLevelOn(String levelFile) {
		level_on = getLevelOn(levelFile);
	}
	
	/* This method returns the level the player is on
	 * given a data file 
	 */
	int getLevelOn(String dataFile) {
		try {
			//Open data file and read it line by line into the map array
			BufferedReader data = new BufferedReader(new FileReader(dataFile));

			//Convert char to int
			int returnValue = data.read() - 48;
			
			//close file
			data.close();
			
			return returnValue;
		}
		catch(IOException e) {
			//Err output
		}
		return -1;
	}
	
	//This method scans the information within a data file into
	//the levelData array
	private void readFileContents(String dataFile) {
		//Set the background first
		
		try {
			//Open data file and read it line by line into the map array
			BufferedReader data = new BufferedReader(new FileReader(dataFile));
			
			int y = 0;
			
			String line;
			while ((line = data.readLine()) != null) {
				for (int x = 0; x < line.length(); x++) {
					char element = line.charAt(x);
					levelData[y][x] = (int)element - 48;	//Convert char to int
				}
				y++;
			}
						
			//Close the file
			data.close();
		}
		catch(Exception e) {
			System.out.println("Error reading level map " + e.getMessage());
		}
	}
	
	//This method displays the data within the levelData array
	public void displayLevelMap(GraphicsProgram screen) {
		loadBGImage(BG_IMAGE, screen);
		
		//Loop through whole array, and evaluate each element
		for (int y = 0; y < MAX_LEVEL_DATA; y++) {
			for (int x = 0; x < MAX_LEVEL_DATA; x++) {
				GOval element = null;
				
				if (levelData[x][y] > 0) {
					element = new GOval(0, 0, LOCATION_RADIUS, LOCATION_RADIUS);
					
					//This is a level (or used to be)
					//The next level is the lowest level
					//if ((levelData[x][y] > 0) && 
					//		(levelData[x][y] < level_on))
					//	level_on = levelData[x][y];

					element.setColor(Color.black);
					
					//New levels will be red
					element.setFilled(true);
					element.setFillColor(Color.red);
				}
				
				if (element != null) {
					//Set the image location
					element.setLocation(y * LOCATION_RADIUS, x * LOCATION_RADIUS);
				
					//Add the location to the array list
					locations.add(element);
					
					//Draw the image
					screen.add(element);
				}
			}
		}
	}
	
	//Loads the background image file
	private void loadBGImage(String fileName, GraphicsProgram screen) {
		GImage newBG = new GImage(fileName);
		newBG.setSize(screen.getWidth(), screen.getHeight());
		screen.add(newBG);
	}
	
	//This method highlights whichever location that the passed
	//x,y coordinates specify. This is usually when the 
	//mouse hovers over a location.
	public void mouseHoverOver(double x, double y) {
		y -= LOCATION_RADIUS * 2;
		x -= LOCATION_RADIUS / 2;
		
		//Loop through all locations checking if bounds contains
		//point
		for (GOval location : locations) {			
			if (location.contains(x, y) && 
					location.getFillColor().equals(Color.red)) 
				location.setFillColor(Color.green);
			else  
				location.setFillColor(Color.red);
		}
	}
	
	//This method takes an x, y coordinate that the user clicked.
	//It returns the level that the coordinates specifies on 
	//the level map
	public int clickedLocation(double x, double y) {

		double locationXPos = -1;
		double locationYPos = -1;
		
		for (GOval location : locations) {
			if (location.contains(x,y)) {
				locationXPos = location.getX();
				locationYPos = location.getY();
				break;
			}
		}
		
		if (locationXPos == -1 ||
			locationYPos == -1)
			return -1;
		
		//Index of the location in the map
		int indexX, indexY;
		indexX = (int)locationXPos / LOCATION_RADIUS;
		indexY = (int)locationYPos / LOCATION_RADIUS;
		
		//System.out.println(levelData[indexY][indexX]);
		
		//Return the level that the player selected
		//if the the level chosen was greater than level_on
		//This makes it so that the player can replay levels,
		//but cant go beyond what level he is on.
		if(levelData[indexY][indexX] <= level_on)
			return levelData[indexY][indexX];
		return -1;
	}
	
	//This method edits the file that stores levels.
	//levelBeaten is the level that was just beaten.
	public void levelUp(int levelBeaten, String saveFile) {
		if (levelBeaten < level_on)
			return;
		
		//Open file stream
		try {
			FileWriter write = new FileWriter(saveFile);
			BufferedWriter dataWrite = new BufferedWriter(write);

			//Write level on to the save file
			dataWrite.write(levelBeaten + 1 + 48);
			
			dataWrite.close();
		}
		catch(IOException e) {
			System.err.println("Error saving: " + e.getMessage());
		}
	}
}
