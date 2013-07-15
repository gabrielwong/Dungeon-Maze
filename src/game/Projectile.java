package game;

import game.items.Item;
import game.map.Lighting;
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.Serializable;

public class Projectile extends MoveableObject implements Lighting, Serializable {
	protected boolean isCollided;
	protected long collisionTime;
	private ProjectileAttribute type;

	protected static final int DEFAULT_SPEED = 200; // default stats
	protected static final int DEFAULT_ATTACK = 20;
	protected static final int DEFAULT_KNOCKBACK = 20;
	protected static final int DEFAULT_COST = 10;
	protected static final int DEFAULT_COLLISION_TIME = 50;

	private long collisionStart = System.currentTimeMillis();

	private static AudioClip soundEffect = Applet.newAudioClip(Projectile.class.getResource("/res/aud/fireball.wav"));

	public static enum ProjectileAttribute {
		FIREBALL(300, 20, 20, 10, 1, 16, 16, loadImages("/res/img/projectile/fireball/1/", ".png"), Item.getProportionatelyScaledInstance(
				Item.loadImage("/res/img/projectile/fireball/0.png"), 16)),

		FIREBALL2(200, 50, 20, 10, 1, 24, 24, Item.getScaledInstances(loadImages("/res/img/projectile/fireball/1/", ".png"), 24), Item
				.getProportionatelyScaledInstance(Item.loadImage("/res/img/projectile/fireball/0.png"), 24)),

		MEGAMAN(500, 120, 50, 50, 1, 24, 32, Item.getScaledInstances(loadImages("/res/img/projectile/megaman/", ".png"), 32), new BufferedImage(1, 1,
				BufferedImage.TYPE_INT_ARGB));

		public final int speed;
		public final int attack;
		public final int knockback;
		public final int cost;
		public final int numMonstersHit;
		public final int width;
		public final int height;
		public final Image[] images;
		public final Image collisionImage;

		private ProjectileAttribute(int speed, int attack, int knockback, int cost, int numMonstersHit, int width, int height, Image[] images,
				Image collisionImage) { // attributes of projectiles
			this.speed = speed;
			this.attack = attack;
			this.knockback = knockback;
			this.cost = cost;
			this.numMonstersHit = numMonstersHit;
			this.width = width;
			this.height = height;
			this.images = images;
			this.collisionImage = collisionImage;
		}
	}

	public Projectile() {
		this(ProjectileAttribute.FIREBALL, 0, 0, DEFAULT_DIRECTION);
	}

	public Projectile(ProjectileAttribute type){
		this(type, DEFAULT_X, DEFAULT_Y, DEFAULT_DIRECTION);
	}
	public Projectile(int x, int y, int direction) {
		this(ProjectileAttribute.FIREBALL2, 0, 0, DEFAULT_DIRECTION);
	}

	public Projectile(ProjectileAttribute type, int x, int y, int direction) {
		super(x, y, direction, type.speed);
		this.type = type;
	}

	public int getAttack() {
		return type.attack;
	}

	public int getKnockback() {
		return type.knockback + (int) (Math.random() * 4) - 2;
	}

	public int getCost() {
		return type.cost;
	}

	public void playSoundEffect() {
		soundEffect.play();
	}

	@Override
	public int getCenterX() {
		return getX() + getWidth() / 2;
	}

	@Override
	public int getCenterY() {
		return getY() + getHeight() / 2;
	}

	public boolean isCollided() {
		return isCollided;
	}

	public void setIsCollided(boolean isCollided) {
		this.isCollided = isCollided;
		if (isCollided) collisionStart = System.currentTimeMillis();
	}

	public boolean shouldRemove() { // erase the projectile if it collides
		return isCollided && System.currentTimeMillis() > collisionTime + collisionStart;
	}

	public Projectile getCopy() {
		return new Projectile(type, getX(), getY(), getDirection());
	}

	@Override
	public boolean isOn() {
		return true;
	}

	@Override
	public int getIntensity() {
		return 180;
	}

	@Override
	public float getRadius() {
		return type.width * 1.5F;
	}

	@Override
	public int getWidth() {
		return type.width;
	}

	@Override
	public int getHeight() {
		return type.height;
	}

	@Override
	public Image getImage() {
		if (isCollided()) return type.collisionImage;
		return type.images[getDirection()];
	}
}
