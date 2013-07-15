package game.skill;

import game.MazeGame;
import game.items.Item;

import java.awt.Image;
import java.awt.event.KeyEvent;

public class OrangePortalSkill extends AbstractSkill {
	private static Image img = Item.loadItemImage(Item.mapPath + "OrangePortal.gif");
	
	public OrangePortalSkill(MazeGame maze){
		super(maze);
		setCost(75);
		setCooldown(15000);
		setUsable(true);
		setKeyCode(KeyEvent.VK_Y);
	}

	@Override
	protected void setHasTimeLeft(boolean hasTimeLeft) {
		if (hasTimeLeft){
			maze.placePortal(false);
		}
	}

	@Override
	protected Image getBaseImage() {
		return img;
	}
	
	protected String getBaseText(){
		return "Shoot an orange portal.";
	}
}
