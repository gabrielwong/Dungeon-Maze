package game.skill;

import game.MazeGame;
import game.Projectile;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.io.Serializable;

public class ProjectileSkill extends AbstractSkill implements Serializable{
	private Projectile projectile;
	private int minimumLevel;
	
	public ProjectileSkill(MazeGame maze){
		super(maze);
		setUsable(true);
		setProjectile(new Projectile());
		setCooldown(250);
		setKeyCode(KeyEvent.VK_SPACE);
	}

	@Override
	protected String getBaseText() {
		return (maze.getPlayer().getLevel() >= minimumLevel)
		? "Attack: " + projectile.getAttack():
			"Unlock at level " + minimumLevel;
	}

	@Override
	protected void setHasTimeLeft(boolean hasTimeLeft) {
		if (hasTimeLeft){
			Projectile p = projectile.getCopy();
			maze.getPlayer().shoot(p);
			maze.getMap().projectiles.add(p);
			maze.getMap().lights.add(p);
			if (maze.isSoundOn())
				p.playSoundEffect();
		}
	}

	@Override
	protected Image getBaseImage() {
		return projectile.getImage();
	}
	
	public void setProjectile(Projectile projectile){
		this.projectile = projectile;
		setCost(projectile.getCost());
	}
	public Projectile getProjectile(){
		return projectile;
	}
	public void setMinimumLevel(int minimumLevel){
		this.minimumLevel = minimumLevel;
	}
	public boolean isUsable(){
		return maze.getPlayer().getLevel() >= minimumLevel;
	}
}
