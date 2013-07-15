package game.items;

import game.MazeGame;
import game.Player;

import java.awt.Image;
import java.io.Serializable;

public class SpeedBooster extends Item implements Serializable {

	public static String name = "Speed Booster";
	private static Image img = loadItemImage(objPath + "lightning.png");

	public SpeedBooster(int x, int y) {
		super(x, y);
	}

	@Override
	public boolean doPickupAction(MazeGame maze, Player player) {
		maze.getBuffManager().setSpeedDuration(20000);
		return true;
	}

	@Override
	public Image getImage() {
		return img;
	}

}
