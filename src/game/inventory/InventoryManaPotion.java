package game.inventory;

import game.MazeGame;
import game.items.Item;

import java.awt.Image;
import java.awt.event.KeyEvent;

public class InventoryManaPotion extends AbstractInventoryItem {
	private MazeGame maze;
	private int amount = 50;
	private static Image img = Item.loadItemImage(Item.objPath + "bluepotion.png");
	
	public InventoryManaPotion(MazeGame maze){
		this.maze = maze;
		setKeyCode(KeyEvent.VK_E);
	}
	@Override
	protected void setHasTimeLeft(boolean hasTimeLeft) {
		if (hasTimeLeft){
			maze.getPlayer().restore(amount);
		}
	}

	@Override
	protected Image getBaseImage() {
		return img;
	}

	@Override
	protected String getBaseText() {
		return "Restores " + amount + " MP";
	}

}
