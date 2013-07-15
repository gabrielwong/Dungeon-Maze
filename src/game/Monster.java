package game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import display.Sprite;
import display.R;

public class Monster extends Character implements Serializable {
	private final MonsterAttributes type;
	private static final int MONSTER_GAP = 10;

	public enum MonsterAttributes {

		EVIL_MEGAMAN(70, 100, 0, 10, 30, 20, "evilmegaman/", 7, null), BAUS(40, 200, 0, 40, 60, 30, 48, 48, R.samSprites, R.samStills, null), DEXTER(
				100, 50, 0, 10, 10, 20, -1, -1, R.dexterSprites, R.dexterStills, null), BOWSER(40, 250, 0, 60, 200, 140, -1, -1, R.bowserSprites,
				R.bowserStills, null);

		public final int speed;
		public final int health;
		public final int mana;
		public final int attack;
		public final int knockback;
		public final int exp;
		public final int width;
		public final int height;
		public final Sprite[] sprites;
		public final Image[] stills;
		public final Projectile projectile;

		private MonsterAttributes(int speed, int health, int mana, int attack, int knockback, int exp, int width, int height, String spritePath,
				int numFramesEach, Projectile projectile) {
			this(speed, health, mana, attack, knockback, exp, width, height, Character.loadSprites(NUM_DIRECTIONS, numFramesEach, spritePath),
					Character.loadStills(NUM_DIRECTIONS, spritePath), projectile);
		}

		private MonsterAttributes(int speed, int health, int mana, int attack, int knockback, int exp, String spritePath, int numFramesEach,
				Projectile projectile) {
			this(speed, health, mana, attack, knockback, exp, -1, -1, Character.loadSprites(NUM_DIRECTIONS, numFramesEach, spritePath), Character
					.loadStills(NUM_DIRECTIONS, spritePath), projectile);
		}

		private MonsterAttributes(int speed, int health, int mana, int attack, int knockback, int exp, int width, int height, Sprite[] sprites,
				Image[] stills, Projectile projectile) {
			this.speed = speed;
			this.health = health;
			this.mana = mana;
			this.attack = attack;
			this.knockback = knockback;
			this.exp = exp;
			this.width = width;
			this.height = height;
			this.sprites = sprites;
			this.stills = stills;
			this.projectile = projectile;

			for (Sprite s : sprites)
				s.setSpeed(20000 / speed);
		}

		/** When width/height is not specified, width/height is initialized with an identifier value of -1 and the width/height of the image is used */
		private MonsterAttributes(int speed, int health, int mana, int attack, int knockback, int exp, Sprite[] sprites, Image[] stills,
				Projectile projectile) {
			this(speed, health, mana, attack, knockback, exp, -1, -1, sprites, stills, projectile);
		}
	}

	public Monster() {
		this(MonsterAttributes.EVIL_MEGAMAN, 0, 0);
	}

	public Monster(int x, int y) {
		this(MonsterAttributes.EVIL_MEGAMAN, x, y);
	}

	public Monster(MonsterAttributes type, int x, int y) {
		super(x, y, DEFAULT_DIRECTION, type.speed, type.health, type.mana, type.attack);
		this.type = type;
	}

	/** Returns a random monster */
	public static Monster randomMonster(int x, int y, double healthMultiplier, double attackMultiplier) {
		int rand = (int) (Math.random() * MonsterAttributes.values().length);
		Monster m = new Monster(MonsterAttributes.values()[rand], x, y);
		m.setHealth((int) (m.getHealth() * healthMultiplier)); // Set attributes for monsters
		m.setAttack((int) (m.getAttack() * attackMultiplier));
		return m;
	}

	@Override
	public int getWidth() {
		if (type.width < 0) return super.getImage().getWidth(null);

		return type.width;
	}

	@Override
	public int getHeight() {
		if (type.height < 0) return super.getImage().getHeight(null);
		return type.height;
	}

	public int getExp() {
		return type.exp;
	}

	protected Sprite[] getSprites() {
		return type.sprites;
	}

	protected Image[] getStills() {
		return type.stills;
	}

	public int getKnockback() {
		return type.knockback;
	}

	public Rectangle getProtectedArea() {
		return new Rectangle(getX() - MONSTER_GAP, getY() - MONSTER_GAP, MONSTER_GAP * 2, MONSTER_GAP * 2);
	}

	@Override
	public Image getImage() {
		if (getHealth() < getMaxHealth()) {
			BufferedImage image = new BufferedImage(getWidth(), getHeight() + HEALTH_BAR_GAP + HEALTH_BAR_HEIGHT, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = image.createGraphics();
			g.drawImage(super.getImage(), 0, 0, getWidth(), getHeight(), null);
			g.setColor(Color.BLACK);
			g.fillRect(0, image.getHeight() - HEALTH_BAR_HEIGHT, getWidth(), HEALTH_BAR_HEIGHT);
			g.setColor(Color.RED);
			g.fillRect(0, image.getHeight() - HEALTH_BAR_HEIGHT, (getWidth() * getHealth()) / getMaxHealth(), HEALTH_BAR_HEIGHT);
			return image;
		}
		return super.getImage();
	}
}
