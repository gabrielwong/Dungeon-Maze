package game.buff;

import game.MazeGame;
import game.items.Item;
import java.awt.Image;
import java.io.Serializable;

public class SightBuff extends AbstractBuff implements Serializable{
	private MazeGame maze;
	private int amount;

	private static Image image = Item.loadItemImage(Item.objPath + "sightbooster.png");
	private static final int DEFAULT_AMOUNT = 75;

	public SightBuff(MazeGame maze) {
		this(maze, DEFAULT_AMOUNT);
	}

	public SightBuff(MazeGame maze, int amount) {
		this.maze = maze;
		this.amount = amount;
	}

	@Override
	public String getBaseText() {
		return "Increase sight by " + amount;
	}

	@Override
	protected Image getBaseImage() {
		return image;
	}

	@Override
	protected void setHasTimeLeft(boolean hasTimeLeft) {
		// Add to radius when active, subtract when buff ends
		if (hasTimeLeft) {
			maze.getPlayer().setRadius(maze.getPlayer().getRadius() + amount);
		} else {
			maze.getPlayer().setRadius(maze.getPlayer().getRadius() - amount);
		}
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		// Update buff for latest value
		maze.getPlayer().setRadius(maze.getPlayer().getRadius() + amount - this.amount);
		this.amount = amount;
	}
}
