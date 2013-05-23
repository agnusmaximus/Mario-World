/* MarioSounds.java
 * 
 * This class plays mario sounds
 */

import java.net.*;
import java.applet.*;
import acm.util.MediaTools;

public class MarioSounds {
	//Song files
	private static final String THEME_FILE = "sounds/theme.wav";
	private static final String JUMP_FILE = "sounds/jump.wav";
	private static final String POWER_FILE = "sounds/power.wav";
	private static final String DEAD_FILE = "sounds/death.wav";
	private static final String COMPLETE_FILE = "sounds/winstage.wav";
	private static final String CLICK_FILE = "sounds/click.wav";
	private static final String NEGATIVE_FILE = "sounds/neg.wav";
	private static final String SMOOSH_FILE = "sounds/smoosh.wav";
	
	//Theme music
	private static AudioClip themeClip;
	
	//Instantiate the class
	public MarioSounds() {
	}
	
	//Play the theme music
	public void playThemeMusic() {
		//Open an audio clip from the url from the theme song file. Then loop this song
		try {
			URL themeUrl = ClassLoader.getSystemResource(THEME_FILE);
			themeClip = java.applet.Applet.newAudioClip(themeUrl);
			themeClip.loop();
		}
		catch(Exception e){
			System.err.println("Error playing theme song" + e.getMessage());
		}
	}
	
	//Play jump sound
	public void jumpSound() {
		playSound(JUMP_FILE);
	}
	
	//Play power up sound
	public void powerupSound() {
		playSound(POWER_FILE);
	}
	
	//Play dead sound
	public void deadSound() {
		themeClip.stop();
		playSound(DEAD_FILE);
	}
	
	//Play complete level sound
	public void completeSound() {
		themeClip.stop();
		playSound(COMPLETE_FILE);
	}
	
	//Play smoosh sound
	public void smoosh() {
		playSound(SMOOSH_FILE);
	}
	
	//Play click sound
	public void clickSound() {
		playSound(CLICK_FILE);
	}
	
	//Play negative sound 
	public void negativeSound() {
		playSound(NEGATIVE_FILE);
	}
	
	//Play a sound
	private void playSound(String file) {
		try {
			AudioClip clip = MediaTools.loadAudioClip(file);
			clip.play();
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
}
