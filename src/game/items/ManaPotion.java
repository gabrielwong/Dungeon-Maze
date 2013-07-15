package game.items;

import game.MazeGame;
import game.Player;
import game.inventory.Inventory;
import java.awt.Image;
import java.io.Serializable;

public class ManaPotion extends Item implements Serializable{
	public static String name = "Blue Potion";

	// for sprites:
	// static int numInSprite = 1;
	// static Image[] sprite = loadSprite(objPath + "redpotion/", numInSprite, "png"); // sprite images
	// static long delay = 1000; // wait delay for sprite in m.s.

	// for a single image:
	static Image img = loadItemImage(objPath + "bluepotion.png"); // sprite images

	public ManaPotion(int x, int y) {
		super(x, y);
		// for resizing the loaded image:
		// img = img.getScaledInstance(40, 40, Image.SCALE_FAST);
	}

	@Override
	// do whatever the action of this item is on the maze
	// return true for delete after pickup, false for leave on map
	public boolean doPickupAction(MazeGame maze, Player player) {
		maze.getInventory().getItem(Inventory.MANA).incrementQuantity();
		return true;
	}

	@Override
	public Image getImage() {
		return img;
		// for sprites: return sprite[getImageIndex(numInSprite, delay)];

	}
}
