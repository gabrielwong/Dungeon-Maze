package game;

import game.map.Lighting;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import display.Sprite;

// TODO: turn into enum to make player addition easy
public class Player extends Character implements Lighting, Serializable {
	private float radius = 150;

	private int exp = 0;
	private int level = 0;
	private int maxLevel = 20;
	private double attackMultiplier = 1.0;
	private double defenceMultiplier = 1.0;

	private Sprite[] sprites;
	private transient Image[] stills;

	private long lastCombat = 0L;
	private static final int BAR_DURATION = 2500;

	public static final int WIDTH = 24;
	public static final int HEIGHT = 32;

	protected static final int DEFAULT_HEALTH = 200; // default values
	protected static final int DEFAULT_MANA = 200;
	protected static final int DEFAULT_ATTACK = 20;
	protected static final int DEFAULT_SPEED = 200;
	protected static final int NUM_FRAMES = 7;
	protected static final String IMAGE_PATH = "megaman/";

	private static final int HEALTH_INCREMENT = 20;
	private static final int MANA_INCREMENT = 20;
	private static final int RADIUS_INCREMENT = 10;
	private static final int SPEED_INCREMENT = 5;

	public Player() {
		this(DEFAULT_X, DEFAULT_Y, DEFAULT_DIRECTION, DEFAULT_SPEED, DEFAULT_HEALTH, DEFAULT_MANA, DEFAULT_ATTACK);
	}

	public Player(int x, int y, int direction, int speed, int totalHealth, int totalMana, int attack) {
		super(x, y, direction, speed, totalHealth, totalMana, attack);
		setManaRegen(5); // regenerates health and mana
		setHealthRegen(5);
		setRegenDelay(3000);

		sprites = Character.loadSprites(NUM_DIRECTIONS, NUM_FRAMES, IMAGE_PATH);
		stills = Character.loadStills(NUM_DIRECTIONS, IMAGE_PATH);
		for (Sprite s : sprites)
			s.setSpeed(20000 / getSpeed());
	}
	
	/** Replenishes the player's mana and health */
	public void revive() {
		setHealth(getMaxHealth());
		setMana(getMaxMana());
		addExp(-50);
	}

	/** The default deserialization is overidden to include image initialization */
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		sprites = Character.loadSprites(NUM_DIRECTIONS, NUM_FRAMES, IMAGE_PATH);
		stills = Character.loadStills(NUM_DIRECTIONS, IMAGE_PATH);
		for (Sprite s : sprites)
			s.setSpeed(20000 / getSpeed());
	}

	@Override
	public int getWidth() { // get player's characteristics
		return WIDTH;
	}

	@Override
	public int getHeight() {
		return HEIGHT;
	}

	@Override
	public boolean isOn() {
		return true; // player is always lit
	}

	@Override
	public float getRadius() {
		return radius;
	}

	@Override
	public int getIntensity() {
		return 255; // always full intensity for lights
	}

	@Override
	public int getCenterX() {
		return getX() + WIDTH / 2;
	}

	@Override
	public int getCenterY() {
		return getY() + HEIGHT / 2;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	public int getExp() {
		return exp;
	}

	public int getMaxExp() {
		return (level == maxLevel) ? 1 : (int) (30 * Math.pow(1.45, getLevel()));
	}

	public int getLevel() {
		return level + 1;
	}

	public void addExp(int amount) { // increases player's exp level
		// Check if player is at max level
		if (level < maxLevel) {
			exp += amount; // increment exp
			// Level up if max exp exceeded
			if (exp < 0) exp = 0;
			
			// Level up
			while (exp >= getMaxExp()) {
				if (level == maxLevel) {
					exp = 0;
					return;
				}
				exp -= getMaxExp();
				level++;
				setMaxHealth(getMaxHealth() + HEALTH_INCREMENT);// increase player's abilities
				setRadius((float)(getRadius()+ RADIUS_INCREMENT));
				setSpeed(getSpeed() + SPEED_INCREMENT);
				setMaxMana(getMaxMana() + MANA_INCREMENT);
				attackMultiplier *= 1.1;
				defenceMultiplier *= 0.9;
				setMana(getMana()+50);
			}
		}
	}

	public int getMaxLevel() {
		return maxLevel;
	}

	protected Sprite[] getSprites() {
		return sprites;
	}

	protected Image[] getStills() {
		return stills;
	}

	@Override
	public Image getImage() {
		if (System.currentTimeMillis() < lastCombat + BAR_DURATION) {
			BufferedImage image = new BufferedImage(getWidth(), getHeight() + 2 * HEALTH_BAR_GAP + 2 * HEALTH_BAR_HEIGHT, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = image.createGraphics();
			g.drawImage(super.getImage(), 0, 0, null);
			g.setColor(Color.BLACK);
			// draws the health and mana bars underneath the player
			g.fillRect(0, image.getHeight() - HEALTH_BAR_HEIGHT, getWidth(), HEALTH_BAR_HEIGHT);
			g.fillRect(0, image.getHeight() - 2 * HEALTH_BAR_HEIGHT - HEALTH_BAR_GAP, getWidth(), HEALTH_BAR_HEIGHT);
			g.setColor(Color.RED);
			g.fillRect(0, image.getHeight() - 2 * HEALTH_BAR_HEIGHT - HEALTH_BAR_GAP, (getWidth() * getHealth()) / getMaxHealth(), HEALTH_BAR_HEIGHT);
			g.setColor(Color.BLUE);
			g.fillRect(0, image.getHeight() - HEALTH_BAR_HEIGHT, (getWidth() * getMana()) / getMaxMana(), HEALTH_BAR_HEIGHT);
			return image;
		}
		return super.getImage();
	}

	@Override
	public void damage(int amount) { // damage a player
		super.damage(amount);
		lastCombat = System.currentTimeMillis();
	}

	@Override
	public void drain(int amount) {
		super.drain(amount);
		lastCombat = System.currentTimeMillis();
	}
	
	public double getAttackMultiplier(){
		return attackMultiplier;
	}
	public double getDefenceMultiplier(){
		return defenceMultiplier;
	}
}
