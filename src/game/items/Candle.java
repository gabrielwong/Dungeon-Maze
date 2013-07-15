package game.items;

import game.MazeGame;
import game.Player;
import game.map.Lighting;
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Image;
import java.io.Serializable;

public class Candle extends Item implements Lighting, Serializable {

	// Default item variables
	public static String name = "candle"; // same as folder location
	private static int numInSprite = 6;
	private static Image[] images = loadSprite(objPath + "candle/", numInSprite, "png"); // sprite images
	private static Image offImage = loadItemImage(objPath + "candle/off.png");
	private static long delay = 100; // wait delay for sprite in m.s.
	private static AudioClip soundEffect = Applet.newAudioClip(Item.class.getResource("/res/aud/candle.wav"));

	//private static Image image = loadAnimatedGIF(objPath + "candle/candle.gif");

	private int radius; // the radius of the gradient paint

	public static final int WIDTH = 60;
	public static final int HEIGHT = 60;

	public Candle() {
		this(DEFAULT_X, DEFAULT_Y, 150);
	}

	public Candle(int x, int y) {
		this(x, y, 150);
	}

	public Candle(int x, int y, int radius) {
		super(x, y);
		this.radius = radius;
		curSprite = random(0, images.length - 1);
	}

	@Override
	public Image getImage() {
		//return image;
		if (isOn)
			return images[getImageIndex(numInSprite, delay)];
		else
			return offImage;
	}

	@Override
	public boolean doPickupAction(MazeGame maze, Player player) {
		isOn = true;
		return false;
	}

	public boolean isOn() {
		return isOn;
	}

	public void playSoundEffect() {
		if(!isOn)
			soundEffect.play();
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public int getIntensity() {
		return (int) (Math.random() * 30 + 225);
	}

	public int getCenterX() {
		return x + getWidth() / 2;
	}

	public int getCenterY() {
		return y + getHeight() / 2;
	}
}
