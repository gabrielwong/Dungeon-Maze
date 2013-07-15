package game.items;

import game.MazeGame;
import game.Player;
import game.inventory.Inventory;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Image;
import java.io.Serializable;

public class Key extends Item  implements Serializable{

	public static String name = "Key"; // same as folder location
	private static Image img = loadItemImage(objPath + "key.png");
	private static AudioClip soundEffect = Applet.newAudioClip(Item.class.getResource("/res/aud/PickUpKey1.wav"));

	public Key(){
		super();
	}
	
	public Key(int x, int y) {
		super(x, y);
	}
	

	@Override
	public boolean doPickupAction(MazeGame maze, Player player) {
		maze.getInventory().getItem(Inventory.KEY).incrementQuantity();
		return true;
	}

	@Override
	public Image getImage() {
		return img;
	}

	public void playSoundEffect() {
		soundEffect.play();
	}

}
