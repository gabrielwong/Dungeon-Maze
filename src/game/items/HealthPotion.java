package game.items;

import game.MazeGame;
import game.Player;
import game.inventory.Inventory;
import java.awt.Image;
import java.io.Serializable;

public class HealthPotion extends Item implements Serializable{

	public static String name = "Health Potion";

	static Image img = loadItemImage(objPath + "redpotion.png");

	@Override
	public boolean doPickupAction(MazeGame maze, Player player) {
		maze.getInventory().getItem(Inventory.HEALTH).incrementQuantity();
		return true;
	}

	public HealthPotion(int x, int y){
		super(x,y);
	}
	
	@Override
	public Image getImage() {
		return img;
	}

}
