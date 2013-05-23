/* File: MarioBlock.java
 * 
 * This class represents the blocks that act as obstacles for Mario.
 * These include brick blocks, item blocks, etc.
 */
public class MarioBlock extends MarioObjects{
	//Constants that determine what type of block this obstacle is
	public static final int NORMAL_BLOCK = 1;
	public static final int ITEM_BLOCK = 2;
	public static final int END_FLAGPOLE = 3;
	
	public int type;
	public boolean containsItem;
	
	public MarioBlock(String image) {
		super(image);
		
		//Determine the type of this object
		typeFromString(image);
	}
	
	//This method sets the type flag according to a passed string
	//(which references a block image file)
	private void typeFromString(String imageFile) {
		if (imageFile.equals("MapPieces/Block.png")) {
			type = NORMAL_BLOCK;
			containsItem = false;
		}
		else if (imageFile.equals("MapPieces/Itembox.PNG")) { 
			type = ITEM_BLOCK;
			containsItem = true;
		}
		else if (imageFile.equals("MapPieces/Flag.png")) {
			type = END_FLAGPOLE;
			containsItem = false;
		}
	}
}
