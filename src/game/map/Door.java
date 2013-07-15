package game.map;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import javax.imageio.ImageIO;
import maze.DrawMazeTask;

public class Door extends MapObject implements Serializable {
	private boolean isClosed = true;
	private boolean isVertical = true;
	private int gridX, gridY;

	private static final BufferedImage[] vDoor = loadImages("/res/img/map/door", ".png");
	private static final BufferedImage[] hDoor = DrawMazeTask.rotate(vDoor);

	public static final int VERTICAL_HEIGHT = 60;
	public static final int VERTICAL_WIDTH = 10;

	public Door() {
		this(DEFAULT_X, DEFAULT_Y);
	}

	public Door(int x, int y) {
		super(x, y);
	}

	@Override
	public int getWidth() {
		return isVertical ? VERTICAL_WIDTH : VERTICAL_HEIGHT;
	}

	@Override
	public int getHeight() {
		return isVertical ? VERTICAL_HEIGHT : VERTICAL_WIDTH;
	}

	@Override
	public Image getImage() {
		int index = isClosed ? 0 : 1;
		return isVertical ? vDoor[index] : hDoor[index];
	}

	public void setVertical(boolean isVertical) {
		this.isVertical = isVertical;
	}

	public boolean isVertical() {
		return isVertical;
	}

	public boolean isClosed() {
		return isClosed;
	}

	public void setClosed(boolean isClosed) {
		this.isClosed = isClosed;
	}

	public void setGridX(int x) {
		gridX = x;
	}

	public void setGridY(int y) {
		gridY = y;
	}

	public int getGridX() {
		return gridX;
	}

	public int getGridY() {
		return gridY;
	}

	private static BufferedImage[] loadImages(String path, String format) {
		BufferedImage[] vDoor = new BufferedImage[2];
		for (int i = 0; i < 2; i++) {
			try {
				vDoor[i] = ImageIO.read(Door.class.getResource(path + i + format));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return vDoor;
	}

}
