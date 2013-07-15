package game.buff;

import game.MazeGame;
import game.items.Item;
import java.awt.Image;
import java.io.Serializable;

public class SpeedBuff extends AbstractBuff implements Serializable{
	private MazeGame maze;
	private int amount;
	
	private static Image image = Item.loadItemImage(Item.objPath + "lightning.png");
	private static final int DEFAULT_AMOUNT = 75;
	public SpeedBuff(MazeGame maze){
		this(maze, DEFAULT_AMOUNT);
	}
	public SpeedBuff(MazeGame maze, int amount){
		this.maze = maze;
		this.amount = amount;
	}
	@Override
	public String getBaseText() {
		return "Increase speed by " + amount;
	}

	@Override
	protected Image getBaseImage() {
		return image;
	}
	
	@Override
	protected void setHasTimeLeft(boolean hasTimeLeft) {
		if (hasTimeLeft){
			maze.getPlayer().setSpeed(maze.getPlayer().getSpeed() + amount);
		} else{
			maze.getPlayer().setSpeed(maze.getPlayer().getSpeed() - amount);
		}
	}
	public int getAmount(){
		return amount;
	}
	public void setAmount(int amount){
		// Update buff for latest value
		maze.getPlayer().setSpeed(maze.getPlayer().getSpeed() + amount - this.amount);
		this.amount = amount;
	}
}
