package game.inventory;

import game.MazeGame;
import game.items.Item;
import game.map.Obstacle;

import java.awt.Image;
import java.awt.event.KeyEvent;

public class InventoryObstacle extends AbstractInventoryItem {
	private MazeGame maze;
	private static Image img = Item.loadItemImage(Item.objPath + "barrier.png");

	public InventoryObstacle(MazeGame maze){
		this.maze = maze;
		setCooldown(5000);
		setKeyCode(KeyEvent.VK_B);
	}

	@Override
	protected String getBaseText() {
		return "Creates a temporary wall.";
	}

	@Override
	protected void setHasTimeLeft(boolean hasTimeLeft) {
		if(hasTimeLeft)
			maze.deployObstacle(new Obstacle());		
	}

	@Override
	protected Image getBaseImage() {
		return img;
	}
}
