package display;

import game.map.Map;
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/** A class for accessing all of the game's resources (i.e. images and sounds) */
public final class R {

	public static final String resPath = "res/", audPath = resPath + "aud/", imgPath = resPath, mapPath = imgPath + "map/", objPath = imgPath
			+ "obj/", charPath = imgPath + "character/", guiPath = resPath + "gui/", itemPath = mapPath + "items/";

	public static final BufferedImage dungeon = i(guiPath + "dungeon.jpg"), title = i(guiPath + "title.png"), door = i(mapPath + "door1.png");
	public static final Image barrier = loadItemImage(itemPath + "barrier.png");

	public static final Image[] img = null, samStills = loadImageArray(charPath + "sam/", 4, "gif"), dexterStills = loadImageArray(charPath
			+ "dexter/", 4, "gif"), bowserStills = loadImageArray(charPath + "bowser/", 4, "gif");
	public static final Sprite[] samSprites = loadSprites(charPath + "sam/", 4, "gif"), dexterSprites = loadSprites(charPath + "dexter/", 4, "gif"),
			bowserSprites = loadSprites(charPath + "bowser/", 4, "gif");

	/** contains audio resources */
	public static final class audio {
		//public static final AudioClip candle = a("candle"), ding1 = a("ding1"), fireball = a("fireball"), pickUpItem = a("pickUpItem"),
			//	pickUpItem1 = a("pickUpItem1"), pickUpKey = a("pickUpKey"), pickUpKey1 = a("pickUpKey1"), pilotDie = a("pilotDie");
	}

	/** Tries to load an AudioClip using Applet.newAudioClip */
	public static AudioClip a(String path) {
		try {
			return Applet.newAudioClip(new URL(audPath + path + ".wav"));
		} catch (Exception e) {
			System.err.println("Couldn't load " + audPath + path + ".wav");
			return null;
		}
	}

	/** Loads a BufferedImage from a sub-directory of resource path */
	public static BufferedImage i(String path) {
		try {
			return ImageIO.read(new File(path));
		} catch (Exception e) {
			System.err.println("Couldn't load " + path);
			e.printStackTrace();
			return createImage(40, 40, Color.RED);
		}
	}

	/**
	 * Loads an Image array from a folder.
	 * File names must be just a number specifying its order in the sprite.
	 * 
	 * @param path The path to the folder containing the sprite
	 */
	public static Image[] loadImageArray(String path, int numInSprite, String ext) {
		return loadImageArray(path, 0, numInSprite, ext);
	}

	public static Image[] loadImageArray(String path, int start, int end, String ext) {
		Image[] images = new Image[end - start];
		for (int i = 0; i < end - start; i++) {
			images[i] = i(path + i + "." + ext);
		}
		return images;
	}

	public static Sprite[] loadSprites(String path, int numInSprite, String ext) {
		Sprite[] sprites = new Sprite[numInSprite];
		for (int i = 0; i < numInSprite; i++) {
			sprites[i] = new Sprite(loadAnimatedGIF(path + i + "." + ext));
		}
		return sprites;
	}

	/** @return A random double within the range (inclusive) */
	public static int random(int min, int max) {
		return (int) (min + Math.random() * ((max - min) + 1));
	}

	/** Loads a proportionately scaled image from a path. */
	public static Image loadItemImage(String path) {
		Image img = i(path);
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
		scale = (double) maxAllowed / max;
		return img.getScaledInstance((int) (img.getWidth(null) * scale), (int) (img.getHeight(null) * scale), Image.SCALE_SMOOTH);
	}

	public static Image[] getScaledInstances(Image[] images, int size) {
		Image[] resizedImages = new Image[images.length];
		for (int i = 0; i < images.length; i++) {
			resizedImages[i] = getProportionatelyScaledInstance(images[i], size);
		}
		return resizedImages;
	}

	public static Sprite[] getScaledInstances(Sprite[] sprites, int size) {
		for (Sprite s : sprites) {
			s.setImage(getProportionatelyScaledInstance(s.getImage(), size));
		}
		return sprites;
	}

	public static Image loadItemGIF(String path) {
		Image img = loadAnimatedGIF(path);
		System.out.println("Width: " + img.getWidth(null) + " Hieght: " + img.getHeight(null));
		return getProportionatelyScaledInstance(img, Map.MAX_ITEM_SIZE);
	}

	/** Returns the GIF image, including all the frames in the animation */
	public static Image loadAnimatedGIF(String path) {
		// return Toolkit.getDefaultToolkit().getImage(Item.class.getResource(path));
		return new ImageIcon(path).getImage();
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
