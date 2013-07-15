package game.skill;

import game.MazeGame;
import game.items.Item;

import java.awt.Image;
import java.awt.event.KeyEvent;

public class BluePortalSkill extends AbstractSkill {
	private static Image img = Item.loadItemImage(Item.mapPath + "BluePortal.gif");
	
	public BluePortalSkill(MazeGame maze){
		super(maze);
		setCost(75);
		setCooldown(15000);
		setUsable(true);
		setKeyCode(KeyEvent.VK_T);
	}

	@Override
	protected void setHasTimeLeft(boolean hasTimeLeft) {
		if (hasTimeLeft){
			maze.placePortal(true);
		}
	}

	@Override
	protected Image getBaseImage() {
		return img;
	}
	
	protected String getBaseText(){
		return "Shoot a blue portal.";
	}
}
