package game.buff;

import game.MazeGame;
import game.items.Item;
import java.awt.Image;
import java.io.Serializable;

public class TimeFreezeBuff extends AbstractBuff implements Serializable{
	private MazeGame maze;
	private static Image image = Item.loadItemImage(Item.objPath + "hourglass.png");
	
	public TimeFreezeBuff(MazeGame maze){
		this.maze = maze;
	}
	@Override
	protected void setHasTimeLeft(boolean hasTimeLeft) {
		maze.setIsTimeFrozen(hasTimeLeft);
	}

	@Override
	protected Image getBaseImage() {
		return image;
	}

	@Override
	protected String getBaseText() {
		return "Freeze time";
	}

}
