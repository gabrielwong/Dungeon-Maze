package game.map;

import game.Character;
import game.items.Item;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.Serializable;

public class Obstacle extends MapObject implements Lighting, Serializable {

	public static String name = "Obstacle";
	private static Image deployImage = Item.loadItemImage(objPath + "barrier.png");

	private int maxLife = 200;
	private int life;
	private long lastDamaged = System.currentTimeMillis();
	private static final int DAMAGE_DELAY = 1000; // how often it can be damaged

	public static final int WIDTH = 32;
	public static final int HEIGHT = 32;

	public Obstacle() {			// temporarily stalls monsters
		this(0, 0);
	}

	public Obstacle(int x, int y) {
		super(x, y);
		this.life = maxLife;
	}

	@Override
	public int getWidth() {
		return WIDTH;
	}

	@Override
	public int getHeight() {
		return HEIGHT;
	}

	@Override
	public BufferedImage getImage() {
		BufferedImage image = new BufferedImage(getWidth(), getHeight() + Character.HEALTH_BAR_GAP + Character.HEALTH_BAR_HEIGHT,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.drawImage(deployImage, 0, 0, null);
		g.setColor(Color.BLACK);
		g.fillRect(0, image.getHeight() - Character.HEALTH_BAR_HEIGHT, getWidth(), Character.HEALTH_BAR_HEIGHT);
		g.setColor(Color.RED);
		g.fillRect(0, image.getHeight() - Character.HEALTH_BAR_HEIGHT, (getWidth() * life) / maxLife, Character.HEALTH_BAR_HEIGHT);
		return image;
	}

	@Override
	public boolean isOn() {
		return true;
	}

	@Override
	public int getIntensity() {
		return 100;
	}

	@Override
	public float getRadius() {
		return 50;
	}

	public void damage(int amount) {		// gets damaged by monsters and fireballs
		if (System.currentTimeMillis() > DAMAGE_DELAY + lastDamaged) {
			life -= amount;
			if (life < 0) life = 0;
			lastDamaged = System.currentTimeMillis();
		}
	}

	public boolean isAlive() {		// if it still has a useful life
		return life > 0;
	}
}
