package maze;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.SwingWorker;

/** Draws a maze in the background */
public class DrawMazeTask extends SwingWorker<BufferedImage, Void> {

	private final Maze maze;	
	private int squareSize;
	private int wallWidth;

	private static BufferedImage[] vWalls = loadImages("/res/img/map/vWalls/", "png", 6);
	private static BufferedImage[] hWalls = rotate(vWalls);

	private static BufferedImage[] backgroundTiles = loadImages("/res/img/map/tiles/", "png", 6);
	private BufferedImage background;

	public DrawMazeTask(Maze maze) {
		this(maze, 50, 5);
	}

	public DrawMazeTask(Maze maze, int squareSize, int wallWidth) {			// draws out a created maze
		this.maze = maze;
		this.squareSize = squareSize;
		this.wallWidth = wallWidth;

		background = generateBackground();
	}

	@Override
	protected BufferedImage doInBackground() throws Exception {
		BufferedImage image = new BufferedImage(Math.max(maze.getColumns() * squareSize, 1), Math.max(maze.getRows() * squareSize, 1),
				BufferedImage.TYPE_INT_ARGB);
		Graphics g = image.getGraphics();

		g.drawImage(background, 0, 0, null);
		setProgress(5);

		/* if(MazeGame.revealPath == true){
		 * g.setColor(pathColor); //draw path
		 * for (int x = 0; x < maze.getColumns(); x++) {
		 * for (int y = 0; y < maze.getRows(); y++) {
		 * if (maze.isPath(x, y)) {
		 * g.fillRect(x * squareSize, y * squareSize, squareSize, squareSize);
		 * }
		 * }
		 * setProgress((x * 15 / maze.getColumns()) + 5);
		 * }
		 * } */

		// Draw vertical walls
		for (int x = 0; x < maze.getColumns() - 1; x++) {
			for (int y = 0; y < maze.getRows(); y++) {
				if (maze.getVerticalWall(x, y)) {
					g.drawImage(vWalls[(int) (Math.random() * 6)], (x + 1) * squareSize - (wallWidth / 2), y * squareSize, null);
				}
			}
			setProgress((x * 35 / maze.getColumns()) + 20);
		}

		// Draw horizontal walls
		for (int x = 0; x < maze.getColumns(); x++) {
			for (int y = 0; y < maze.getRows() - 1; y++) {
				if (maze.getHorizontalWall(x, y)) {
					g.drawImage(hWalls[(int) (Math.random() * 6)], x * squareSize, (y + 1) * squareSize - (wallWidth / 2), null);
				}
			}
			setProgress((x * 35 / maze.getColumns()) + 55);
		}

		return image;
	}

	private BufferedImage generateBackground() {			// draw the background
		BufferedImage image = new BufferedImage(maze.getColumns() * squareSize, maze.getRows() * squareSize, BufferedImage.TYPE_INT_ARGB);
		Graphics g = image.getGraphics();
		for (int x = 0; x < image.getWidth(); x += 50) {
			for (int y = 0; y < image.getHeight(); y += 50) {
				g.drawImage(backgroundTiles[(int) (Math.random() * 6)], x, y, null);
			}
		}
		return image;
	}

	private static BufferedImage[] loadImages(String path, String format, int nFiles) {
		BufferedImage[] images = new BufferedImage[nFiles];

		for (int i = 0; i < nFiles; i++) {
			try {
				images[i] = ImageIO.read(DrawMazeTask.class.getResource(path + i + "." + format));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return images;
	}

	public static BufferedImage[] rotate(BufferedImage[] images) {		// rotate image, can be used by any class
		BufferedImage[] newImages = new BufferedImage[images.length];
		for (int i = 0; i < newImages.length; i++) {
			newImages[i] = new BufferedImage(images[i].getHeight(), images[i].getWidth(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = newImages[i].createGraphics();
			g.rotate(Math.PI / 2);
			g.drawImage(images[i], 0, -images[i].getHeight(), null);
		}
		return newImages;
	}
}
