package game.skill;

import game.AbstractCountdownObject;
import game.MazeGame;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.Serializable;

/** Representations of skills (like portal and projectile deployment) */
public abstract class AbstractSkill extends AbstractCountdownObject implements Serializable{
	protected boolean isUsable = false;
	private int cost = 0;
	/** time it takes for the player to be allowed to reuse it */
	protected int cooldown = 1000;
	public MazeGame maze;

	public AbstractSkill(MazeGame maze){
		setDrawDirection(true);
		this.maze = maze;
	}

	@Override
	public void actionPerformed(InputEvent evt){
		// Check for cooldown and if it is enabled
		if (!hasTimeLeft() && isUsable){
			// Check if enough mana
			if (maze.getPlayer().getMana() >= getCost()){
				setDuration(cooldown);
				maze.getPlayer().drain(getCost());
			}
		}
	}
	
	public int getCooldown(){
		return cooldown;
	}
	public void setCooldown(int cooldown){
		this.cooldown = cooldown;
	}
	public String[] getToolTipText(){
		String[] s = {getBaseText(),
				"Cooldown: " + cooldown + " ms",
				"Mana: " + cost + " MP",
				"Hotkey: " + (getKeyCode() == - 1 ? "None" : KeyEvent.getKeyText(getKeyCode()))};
		return s;
	}
	public boolean isUsable(){
		return isUsable;
	}
	public void setUsable(boolean isUsable){
		this.isUsable = isUsable;
	}
	public int getCost(){
		return cost;
	}
	public void setCost(int cost){
		this.cost = cost;
	}
	public BufferedImage getImage(){
		if (!isUsable)
			return super.getImage(0);
		return super.getImage();
	}

	protected abstract String getBaseText();
}
