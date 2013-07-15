package game;

import game.items.Item;
import java.awt.Image;
import java.io.Serializable;
import java.util.ArrayList;
import maze.Maze;
import display.Sprite;

public abstract class Character extends MoveableObject implements Serializable {
	private int maxHealth;
	private int maxMana;
	private int health;
	private int mana;
	private int attack;
	private int regenDelay;
	private int healthRegen;
	private int manaRegen;
	private int maxSpeed;
	private long lastRegen = System.currentTimeMillis();

	protected ArrayList<Projectile> shots = new ArrayList<Projectile>();

	protected static final int DEFAULT_HEALTH = 100;
	protected static final int DEFAULT_MANA = 100;
	protected static final int DEFAULT_ATTACK = 25;
	protected static final int DEFAULT_REGEN_DELAY = 3000;
	protected static final int DEFAULT_HEALTH_REGEN = 0;
	protected static final int DEFAULT_MANA_REGEN = 0;
	protected static final int DEFAULT_MAX_SPEED = 400;
	protected static final int NUM_DIRECTIONS = 4;
	public static final int HEALTH_BAR_GAP = 2;
	public static final int HEALTH_BAR_HEIGHT = 5;

	public Character() {
		this(DEFAULT_X, DEFAULT_Y, DEFAULT_DIRECTION, DEFAULT_SPEED, DEFAULT_HEALTH, DEFAULT_MANA, DEFAULT_ATTACK);
	}

	public Character(int x, int y, int direction, int speed, int maxHealth, int maxMana, int attack) {
		super(x, y, direction, speed);
		this.speed = speed;
		this.maxHealth = maxHealth;
		health = maxHealth;
		this.maxMana = maxMana;
		mana = maxMana;
		this.attack = attack;

		this.regenDelay = DEFAULT_REGEN_DELAY;
		this.healthRegen = DEFAULT_HEALTH_REGEN;
		this.manaRegen = DEFAULT_MANA_REGEN;
		this.maxSpeed = DEFAULT_MAX_SPEED;
		shots = new ArrayList<Projectile>();
	}

	/**
	 * @return the maximum health
	 */
	public int getMaxHealth() {
		return maxHealth;
	}

	/**
	 * @param maxHealth the maximium health to set
	 */
	protected void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
	}

	public int getMaxMana() {
		return maxMana;
	}

	public void setMaxMana(int maxMana) {
		this.maxMana = maxMana;
	}

	/**
	 * @return the current health
	 */
	public int getHealth() {
		return health;
	}

	/**
	 * @param health the current health to set
	 */
	public void setHealth(int health) {
		this.health = health;
	}

	public void heal(int amount) {
		health += amount;

		// Ensure it is not over the maximum
		if (health > maxHealth) health = maxHealth;
	}

	public void damage(int amount) {
		health -= amount;

		// Ensure it is not under 0
		if (health < 0) health = 0;
	}

	public int getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(int maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public int getMana() {
		return mana;
	}

	public void setMana(int mana) {
		this.mana = mana;
	}

	public void drain(int amount) {
		mana -= amount;
		if (mana < 0) mana = 0;
	}

	public void restore(int amount) {
		mana += amount;
		if (mana > maxMana) mana = maxMana;
	}

	public boolean isAlive() {
		return health > 0;
	}

	public int getAttack() {
		return attack;
	}

	public void setAttack(int attack) {
		this.attack = attack;
	}

	public Projectile shoot(Projectile projectile) {
		if (projectile == null) return null;

		projectile.setDirection(getDirection());

		// Center the projectile with player
		switch (getDirection()) {
		case Maze.NORTH:
		case Maze.SOUTH:
			projectile.setX(getX() + (getWidth() - projectile.getWidth()) / 2);
			projectile.setY(getY());
			break;
		case Maze.EAST:
		case Maze.WEST:
			projectile.setY(getY() + (getHeight() - projectile.getHeight()) / 2);
			projectile.setX(getX());
			break;
		}

		return projectile;
	}

	/**
	 * @return the regenDelay
	 */
	public int getRegenDelay() {
		return regenDelay;
	}

	/**
	 * @param regenDelay the regenDelay to set
	 */
	public void setRegenDelay(int regenDelay) {
		this.regenDelay = regenDelay;
	}

	/**
	 * @return the healthRegen
	 */
	public int getHealthRegen() {
		return healthRegen;
	}

	/**
	 * @param healthRegen the healthRegen to set
	 */
	public void setHealthRegen(int healthRegen) {
		this.healthRegen = healthRegen;
	}

	/**
	 * @return the manaRegen
	 */
	public int getManaRegen() {
		return manaRegen;
	}

	/**
	 * @param manaRegen the manaRegen to set
	 */
	public void setManaRegen(int manaRegen) {
		this.manaRegen = manaRegen;
	}

	public boolean tryRegen() {
		if (lastRegen + regenDelay <= System.currentTimeMillis()) {
			lastRegen = System.currentTimeMillis();
			heal(healthRegen);
			restore(manaRegen);
			return true;
		}
		return false;
	}

	/** loads sprites from the default character path */
	public static Sprite[] loadSprites(int numSprites, int numFramesEach, String imgPath) {
		Sprite[] sprites = new Sprite[numSprites];
		String path = Item.charPath + imgPath;
		for (int i = 0; i < numSprites; i++) {
			sprites[i] = new Sprite(Item.loadSprite(path + i + "/", 1, numFramesEach, "png"));
		}
		return sprites;
	}

	public static Image[] loadStills(int numSprites, String imgPath) {
		Image[] stills = new Image[numSprites];
		String path = Item.charPath + imgPath;
		for (int i = 0; i < numSprites; i++) {
			stills[i] = Item.loadImage(path + i + "/0.png");
		}
		return stills;
	}

	public Image getImage() {
		if (!isMoving()) return getStills()[direction];
		return getSprites()[direction].getImage();
	}

	public void setSpeed(int speed) {
		super.setSpeed(speed);
		for (Sprite s : getSprites())
			s.setSpeed(20000 / speed);

	}

	protected abstract Sprite[] getSprites();

				protected abstract Image[] getStills();
}
