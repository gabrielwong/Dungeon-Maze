package game.items;

import game.MazeGame;
import game.Player;
import game.map.Map;
import game.map.MapObject;
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/** The abstract class representing any item. */
public abstract class Item extends MapObject {

	protected static AudioClip soundEffect = Applet.newAudioClip(Item.class.getResource("/res/aud/ding1.wav")); // sound effect when picked up
	protected boolean isOn = false; // If items that do not get picked up is on

	public int getWidth() {
		return getImage().getWidth(null);
	}

	public int getHeight() {
		return getImage().getHeight(null);
	}

	/**
	 * All items must implement their pickup action here.
	 * 
	 * @return Whether or not the item should be removed from the maze.
	 */
	abstract public boolean doPickupAction(MazeGame maze, Player player);

	protected int curSprite = 0; // current sprite index
	protected long previousTime = System.currentTimeMillis();

	/** @return The appropriate image in the sprite, for smooth animations. */
	public int getImageIndex(int numInSprite, long delay) {
		long curTime = System.currentTimeMillis();
		if (curTime - previousTime > delay) { // if delay is passed
			// update current sprite
			if (++curSprite == numInSprite) curSprite = 0;
			previousTime = curTime; // reset previous time
		}
		return curSprite;
	}

	/** @return The appropriate image in the sprite, accurate to the current time (won't be smooth) */
	public int getAccurateImageIndex(int numInSprite, long delay) {
		long curTime = System.currentTimeMillis();
		long dif = curTime - previousTime;
		if (dif > delay) { // if delay is passed
			curSprite += dif % delay;
			// update current sprite
			if (curSprite >= numInSprite) curSprite %= numInSprite;
			previousTime = curTime; // reset previous time
		}
		previousTime = curTime; // reset previous time
		return curSprite;
	}

	public Item() {
		this(DEFAULT_X, DEFAULT_Y);
	}

	public Item(int x, int y) {
		super(x, y);
	}

	/** @return A random item */
	public static Item randomItem(int x, int y) {
		int rand = random(1, 12);
		switch (rand) {// different probabilities
		case 1:
			return new TimeFreeze(x, y);
		case 2:
		case 3:
			return new SpeedBooster(x, y);
		case 4:
		case 5:
			return new SightBooster(x, y);
		case 6:
		case 7:
			return new HealthPotion(x, y);
		case 8:
			return new ObstacleItem(x, y);
		case 9:
		case 10:
		case 11:
		default:
			return new ManaPotion(x, y);
		}
	}

	public void playSoundEffect() {
		soundEffect.play();
	}

	/***** SEVERAL METHODS BELOW FOR IMAGE LOADING ******/
	/**
	 * Loads an Image array from a folder.
	 * File names must be just a number specifying its order in the sprite.
	 * 
	 * @param path The path to the folder containing the sprite
	 */
	public static Image[] loadSprite(String path, int numInSprite, String ext) {
		return loadSprite(path, 0, numInSprite, ext);
	}

	public static Image[] loadSprite(String path, int start, int end, String ext) {
		Image[] images = new Image[end - start];
		for (int i = 0; i < end - start; i++) {
			images[i] = loadItemImage(path + i + "." + ext);
		}
		return images;
	}

	/** @return A random double within the range (inclusive) */
	public static int random(int min, int max) {
		return (int) (min + Math.random() * ((max - min) + 1));
	}

	/** Loads a proportionately scaled image from a path. */
	public static Image loadItemImage(String path) {
		Image img = loadImage(path);
		if (img == null)
			return null;
		else
			return getProportionatelyScaledInstance(img, Map.MAX_ITEM_SIZE);
	}

	/**
	 * If the image is bigger than the maximum item size,
	 * it resizes it to fit while keeping the original image ratio.
	 * 
	 * @param img Image to be resized
	 * @param maxAllowed maximum allowed width or height
	 * @return A scaled image
	 */
	public static Image getProportionatelyScaledInstance(Image img, int maxAllowed) {
		int max = Math.max(img.getWidth(null), img.getHeight(null));
		double scale;
		if (max > maxAllowed) // if either width or height is bigger than maximum allowed
			scale = (double) maxAllowed / max;
		else
			scale = (double) maxAllowed / max;
		return img.getScaledInstance((int) (img.getWidth(null) * scale), (int) (img.getHeight(null) * scale), Image.SCALE_SMOOTH);
	}

	public static Image[] getScaledInstances (Image [] images, int size){
		Image[] resizedImages = new Image[images.length];
		for (int i = 0; i < images.length; i++){
			resizedImages [i] = getProportionatelyScaledInstance (images[i], size); 
		}
		return resizedImages;
	}
	/** Loads a BufferedImage from a path */
	public static BufferedImage loadImage(String path) {
		try {
			return ImageIO.read(Item.class.getResource(path));
		} catch (Exception e) {
			System.err.println("Couldn't load " + path);
			e.printStackTrace();
			return null;
		}
	}

	public static Image loadItemGIF(String path) {
		Image img = loadAnimatedGIF(path);
		System.out.println("Width: " + img.getWidth(null) + " Hieght: " + img.getHeight(null));
		return getProportionatelyScaledInstance(img, Map.MAX_ITEM_SIZE);
	}

	/** Returns the GIF image, including all the frames in the animation */
	public static Image loadAnimatedGIF(String path) {
		// return Toolkit.getDefaultToolkit().getImage(Item.class.getResource(path));
		return new ImageIcon(Item.class.getResource(path)).getImage();
	}

	public static Image[] loadImageArray(String path, int numInSprite, String ext, boolean gif) {
		Image[] images = new Image[numInSprite];
		for (int i = 0; i < numInSprite; i++) {
			if (gif)
				images[i] = loadAnimatedGIF(path + i + "." + ext);
			else
				images[i] = loadItemImage(path + i + "." + ext);
		}
		return images;
	}

	/**
	 * @param width width of desired image
	 * @param height height of desired image
	 * @return a red buffered image
	 */
	public static BufferedImage createImage(int width, int height, Color color) {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = image.getGraphics();
		g.setColor(color);
		g.fillRect(0, 0, width, height);
		return image;
	}
}
