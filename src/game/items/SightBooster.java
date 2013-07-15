package game.items;

import game.MazeGame;
import game.Player;

import java.awt.Image;
import java.io.Serializable;

public class SightBooster extends Item implements Serializable {

	// Default item variables
	public static String name = "Sight Booster";
	static Image image = loadItemImage(objPath + "sightbooster.png");

	public float adjustment;

	public SightBooster(int x, int y) {
		this(x, y, 25);
	}

	public SightBooster(int x, int y, float adjustment) {
		super(x, y);
		this.adjustment = adjustment;
	}

	@Override
	public boolean doPickupAction(MazeGame maze, Player player) {
		maze.getBuffManager().setSightDuration(20000);
		return true;
	}

	@Override
	public Image getImage() {
		return image;
	}

}
