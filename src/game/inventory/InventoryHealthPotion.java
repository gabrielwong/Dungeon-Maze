package game.inventory;

import game.MazeGame;
import game.items.Item;

import java.awt.Image;
import java.awt.event.KeyEvent;

public class InventoryHealthPotion extends AbstractInventoryItem {
	private MazeGame maze;
	private int amount = 50;
	
	private static Image img = Item.loadItemImage(Item.objPath + "redpotion.png");
	public InventoryHealthPotion(MazeGame maze){
		this.maze = maze;
		setKeyCode(KeyEvent.VK_Q);
	}

	@Override
	protected void setHasTimeLeft(boolean hasTimeLeft) {
		if (hasTimeLeft){
			maze.getPlayer().heal(amount);
		}
	}

	@Override
	protected Image getBaseImage() {
		return img;
	}

	@Override
	protected String getBaseText() {
		return "Heals " + amount + " HP";
	}

}
