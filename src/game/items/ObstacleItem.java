package game.items;

import game.MazeGame;
import game.Player;
import game.inventory.Inventory;
import java.awt.Image;
import java.io.Serializable;
import display.R;

public class ObstacleItem extends Item implements Serializable {
	public static String name = "ObstacleItem"; // same as folder location

	public ObstacleItem(int x, int y) { // item for picking up obstacles
		super(x, y);
	}

	@Override
	public boolean doPickupAction(MazeGame maze, Player player) { // increase number of obstacles you have
		maze.getInventory().getItem(Inventory.OBSTACLE).incrementQuantity();
		return true;
	}

	@Override
	public Image getImage() {
		return R.barrier;
	}
}
