package game.map;

import game.items.Item;
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Image;
import java.io.Serializable;

/** A portal only works if you use the setEndPortal method to associate it with another portal. */
public class Portal extends MapObject implements Lighting, Serializable {

	public static Image blue = Item.loadItemImage(Item.mapPath + "BluePortal.gif");
	public static Image orange = Item.loadItemImage(Item.mapPath + "OrangePortal.gif");
	public static Image grey = Item.loadItemImage(Item.mapPath + "GreyPortal.gif");

	protected static AudioClip soundEffect = Applet.newAudioClip(Item.class.getResource(Item.audPath + "portalgun/portal_open1.wav"));

	public static AudioClip blueSound = Applet.newAudioClip(Item.class.getResource(Item.audPath + "portalgun/portalgun_shoot_blue1.wav"));
	public static AudioClip orangeSound = Applet.newAudioClip(Item.class.getResource(Item.audPath + "portalgun/portalgun_shoot_red1.wav"));

	public Portal endPortal = this; // default

	/** Whether the portal should take effect or not. */
	public boolean isActive = true;

	public boolean waiting = false;

	/** Whether the portal should be lit or not */
	public boolean lit = false;

	public boolean isOrange;

	public Portal getEndPortal() {
		return endPortal;
	}

	/** the endPortal must be set for a portal to work */
	public void setEndPortal(Portal p) {
		endPortal = p;
	}

	public Portal(boolean isOrange) {
		this(0, 0, isOrange);
	}

	public Portal(int x, int y, boolean isOrange) {
		super(x, y);
		this.isOrange = isOrange;
	}

	@Override
	public Image getImage() {
		if (endPortal == this) return grey;
		return isOrange ? orange : blue;
	}

	@Override
	public int getWidth() {
		return isOrange ? orange.getWidth(null) : blue.getWidth(null);
	}

	@Override
	public int getHeight() {
		return isOrange ? orange.getHeight(null) : blue.getWidth(null);
	}

	@Override
	public boolean isOn() {
		return lit;
	}

	@Override
	public int getIntensity() {
		return 200;
	}

	public void playSoundEffect() {
		soundEffect.play();
	}

	@Override
	public float getRadius() {
		return getWidth() * 2;
	}

}
