package game.skill;

import game.MazeGame;
import game.Projectile;
import game.Projectile.ProjectileAttribute;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.Serializable;

public class SkillManager implements Serializable {
	private int x = 0;
	private int y = 0;
	private AbstractSkill[] skills = new AbstractSkill[NUM_ITEMS];
	
	private static final Color TEXT_COLOR = new Color(200, 200, 200);

	public static final int BUFF_GAP = 4;
	public static final int SHOT = 0;
	public static final int ALT_SHOT = 1;
	public static final int BLUE_PORTAL = 2;
	public static final int ORANGE_PORTAL = 3;
	public static final int NUM_ITEMS = 4;

	public SkillManager(MazeGame maze){
		skills[SHOT] = new ProjectileSkill(maze);
		skills[ALT_SHOT] = new ProjectileSkill(maze);
		skills[BLUE_PORTAL] = new BluePortalSkill(maze);
		skills[ORANGE_PORTAL] = new OrangePortalSkill(maze);
		for (AbstractSkill item : skills){
			maze.mouseObjects.add(item);
		}
		((ProjectileSkill)skills[ALT_SHOT]).setProjectile(new Projectile(ProjectileAttribute.MEGAMAN));
		skills[ALT_SHOT].setKeyCode(KeyEvent.VK_SHIFT);
		((ProjectileSkill)skills[ALT_SHOT]).setMinimumLevel(5);
		updateSkillCoordinates();
	}

	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
		updateSkillCoordinates();
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
		updateSkillCoordinates();
	}
	private void updateSkillCoordinates(){
		for (int i = 0; i < skills.length; i++){
			skills[i].setX(x + i * (AbstractSkill.WIDTH + BUFF_GAP));
			skills[i].setY(y);
		}
	}

	public BufferedImage getImage(){
		BufferedImage image = new BufferedImage(
				skills.length * (AbstractSkill.WIDTH + BUFF_GAP) - BUFF_GAP,
				AbstractSkill.HEIGHT,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();

		g.setColor(Color.BLACK);
		FontMetrics metrics = g.getFontMetrics();
		for (AbstractSkill skill : skills){
			g.drawImage(skill.getImage(), skill.getX() - x, skill.getY() - y, null);
			int keyCode = skill.getKeyCode();
			if (keyCode != -1){
				String keyString = KeyEvent.getKeyText(keyCode);
				MazeGame.outlineText(g, keyString, skill.getX() - x + skill.getWidth() - metrics.stringWidth(keyString) - 1, skill.getY() - y + metrics.getAscent(), TEXT_COLOR, Color.BLACK);
			}
		}
		return image;
	}
	
	public AbstractSkill getSkill(int index){
		return skills[index];
	}
	
	public int getWidth(){
		return NUM_ITEMS * (AbstractSkill.WIDTH + BUFF_GAP) - BUFF_GAP;
	}
}
