package game.inventory;

import game.MazeGame;
import game.items.Item;
import game.map.Door;
import game.map.Map;

import java.awt.Image;
import java.awt.event.KeyEvent;

public class InventoryKey extends AbstractInventoryItem {
	private MazeGame maze;
	private static Image img = Item.loadItemImage(Item.objPath + "key.png");
	public InventoryKey(MazeGame maze){
		this.maze = maze;
		setCooldown(5000);
		setKeyCode(KeyEvent.VK_L);
	}

	@Override
	protected String getBaseText() {
		return "Unlocks a door.";
	}

	@Override
	protected void setHasTimeLeft(boolean hasTimeLeft) {
		if (hasTimeLeft){
			int x = maze.getPlayer().getX() / Map.SQUARE_SIZE;
			int y = maze.getPlayer().getY() / Map.SQUARE_SIZE;
			// Try to unlock a door next to the player if there is any
			for (int i = 0; i < maze.getMap().doors.size(); i++){
				Door d = maze.getMap().doors.get(i);
				if (d.isClosed()){
					if ((d.getGridX() == x || d.getGridX() == x - 1) && (d.getGridY() == y || d.getGridY() == y - 1)){
						d.setClosed(false);
						return;
					}
				}
			}
			incrementQuantity(); // Increase quantity so that key is not used if it has not been used
		}
	}

	@Override
	protected Image getBaseImage() {
		return img;
	}

}
