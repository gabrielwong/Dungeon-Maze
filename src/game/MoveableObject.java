package game;

import game.map.MapObject;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import javax.imageio.ImageIO;
import maze.Maze;

public abstract class MoveableObject extends MapObject implements Serializable {
	protected int speed; // In pixels per second
	protected int direction; // the last direction the object is facing
	protected boolean isMoving = false;

	protected static final int DEFAULT_DIRECTION = Maze.EAST;
	protected static final int DEFAULT_SPEED = 100;

	public MoveableObject() {
		this(DEFAULT_X, DEFAULT_Y, DEFAULT_DIRECTION, DEFAULT_SPEED);
	}

	public MoveableObject(int x, int y) {
		this(x, y, DEFAULT_DIRECTION, DEFAULT_SPEED);
	}

	public MoveableObject(int x, int y, int direction, int speed) {
		super(x, y);
		this.speed = speed;
		this.direction = direction;
	}

	public boolean isMoving() {		// check if object is moving
		return isMoving;
	}

	public void setIsMoving(boolean isMoving) {		// set an object to move or not
		this.isMoving = isMoving;
	}

	/**
	 * @return the direction the object is facing
	 */
	public int getDirection() {
		return direction;
	}

	/**
	 * @param direction the direction to set
	 */
	public void setDirection(int direction) {
		this.direction = direction;
	}

	/**
	 * Sets the speed of the object (the number of pixels the object will move each update).
	 * 
	 * @return the speed of the object
	 */
	public int getSpeed() {
		return speed;
	}

	/**
	 * @param speed the speed to set
	 */
	public void setSpeed(int speed) {		// alter the speeds of an object
		this.speed = speed;
	}

	public void speedUp(int amount) {
		speed += amount;
	}

	public void slowDown(int amount) {
		speed -= amount;
	}

	/**
	 * @return the width of the object in pixels
	 */
	public abstract int getWidth();

	/**
	 * @return the height of the object in pixels
	 */
	public abstract int getHeight();

	/**
	 * Returns the object sprite in the current direction.
	 * 
	 * @return the current object sprite
	 */
	public abstract Image getImage();

	/**
	 * Loads the character images from the file system. It will load images 0 - 3 representing
	 * north, south, east and west directions as defined in Maze.
	 * 
	 * @param path the string representation of the folder containing the images
	 * @param format the file extension of the images
	 * @return an array of the character images
	 */
	protected static BufferedImage[] loadImages(String path, String format) {
		BufferedImage[] images = new BufferedImage[4];
		for (int i = 0; i < 4; i++) {
			try {
				images[i] = ImageIO.read(MoveableObject.class.getResource(path + i + format));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return images;
	}
}
