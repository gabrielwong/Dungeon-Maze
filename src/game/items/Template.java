package game.items;

import game.MazeGame;
import game.Player;

import java.awt.Image;
import java.io.Serializable;

public class Template extends Item implements Serializable {

	public static String name = "Template";

	// for sprites:
	static int numInSprite = 1;
	static Image[] sprite = loadSprite(objPath + "folder/", numInSprite, "png");
	static long delay = 1000; // wait delay for sprite in m.s.

	// for a single image:
	static Image img = loadItemImage(objPath + "redpotion.png"); // sprite images

	public Template(int x, int y) {
		super(x, y);
	}

	@Override
	// do whatever the action of this item is on the maze
	// return true for delete after pickup, false for leave on map
	public boolean doPickupAction(MazeGame maze, Player player) {
		maze.getPlayer().heal(50);
		return true;
	}

	@Override
	public Image getImage() {
		// for sprites:
		return sprite[getImageIndex(numInSprite, delay)];

		// for simple images:
		// return img;
	}
}
