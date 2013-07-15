package display;

import java.awt.Image;
import java.io.Serializable;

/** a class to represent an animated image */
public class Sprite implements Serializable {

	// used for manual sprites
	private transient Image[] images = null;
	private int curFrame = 0;
	private int delay = 0;
	private long previousTime = System.currentTimeMillis();

	// used for GIF Sprites
	private transient Image animatedImage = null;
	private boolean useAnimatedImage;

	public Sprite(Image[] images) {
		this(images, 0);
	}

	/** use for manually animated sprites */
	public Sprite(Image[] images, int delay) {
		this.images = images;
		this.delay = delay;
		useAnimatedImage = false;
	}

	/** use for sprites from a GIF file */
	public Sprite(Image image) {
		this.animatedImage = image;
		useAnimatedImage = true;
	}

	/** set the animated image for the sprite */
	public void setImage(Image image) {
		animatedImage = image;
	}

	public Image getImage() {
		if (useAnimatedImage && animatedImage != null) // if supposed to use the GIF image
			return animatedImage;
		else {
			long curTime = System.currentTimeMillis();
			if (curTime - previousTime > delay) { // if delay is passed
				// update current sprite
				if (++curFrame == images.length) curFrame = 0;
				previousTime = curTime; // reset previous time
			}
			return images[curFrame];
		}
	}

	/** Speeds up the sprite by the amount given (m.s.). Returns whether delay is at its minimum or not */
	public boolean speedUp(int reduction) {
		delay -= reduction;
		if (delay < 0) {
			delay = 0;
			return true;
		} else
			return false;
	}

	/** set the delay between frames in milliseconds */
	public void setSpeed(int speed) {
		this.delay = speed;
	}
}
