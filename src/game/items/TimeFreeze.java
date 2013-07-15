package game.items;

import game.MazeGame;
import game.Player;

import java.awt.Image;
import java.io.Serializable;

public class TimeFreeze extends Item implements Serializable {

	private static Image image = loadItemImage(objPath + "hourglass.png");

	@Override
	public boolean doPickupAction(MazeGame maze, Player player) {
		maze.getBuffManager().setTimeFreezeDuration(10000);
		return true;
	}

	public TimeFreeze(int x, int y) {
		super(x, y);
	}

	@Override
	public Image getImage() {
		return image;
	}
}
