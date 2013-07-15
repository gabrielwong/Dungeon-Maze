package maze;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

/** a simple 2d maze representation */
public class Maze implements Serializable {
	protected int columns; // number of columns
	protected int rows; // number of rows
	private boolean[][] vWalls; // vertical walls (true if there is a wall to the right of a given square)
	private boolean[][] hWalls; // horizontal walls (true if there is a wall below a given square)
	protected int[][] pathLengthAtSquare; // The length of the path up to that square

	protected int startX;
	protected int startY; // starting square

	protected int endX, endY; // ending square

	public int test = 5;

	// To identify direction
	public static final int NORTH = 0;
	public static final int EAST = 1;
	public static final int SOUTH = 2;
	public static final int WEST = 3;

	public static boolean revealPath = false;

	public Maze() {
		this(10, 10);
	}

	public String toString() {
		String out = String.format("test:%s rows:%s", test, rows);
		return out;
	}

	public Maze(int columns, int rows) {
		// Set the width and height of the maze
		this.columns = columns;
		this.rows = rows;

		// Create grid for walls and whether a square was visited
		vWalls = new boolean[columns - 1][rows];
		hWalls = new boolean[columns][rows - 1];
		pathLengthAtSquare = new int[columns][rows];

		// Initialize starting and ending squares
		startX = 0;
		startY = 0;
		endX = columns - 1;
		endY = rows - 1;

		createMaze();
	}

	public Maze(File f) {

	}

	public void createMaze() {
		CreateMazeTask task = new CreateMazeTask(this);
		task.execute();
		try {
			task.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	/** Makes all the walls present */
	public void populateWalls() {
		for (int i = 0; i < vWalls.length; i++)
			Arrays.fill(vWalls[i], true);
		for (int i = 0; i < hWalls.length; i++)
			Arrays.fill(hWalls[i], true);
	}

	/**
	 * Removes walls based on the accuracy given.
	 * Can be used to make maze simpler by removing some walls.
	 */
	public void removeWalls(double accuracy) {
		for (int x = 0; x < vWalls.length; x++)
			for (int y = 0; y < vWalls[0].length; y++) {
				if (vWalls[x][y] && Math.random() < accuracy) vWalls[x][y] = false;
			}
		for (int x = 0; x < hWalls.length; x++)
			for (int y = 0; y < hWalls[0].length; y++) {
				if (hWalls[x][y] && Math.random() < accuracy) hWalls[x][y] = false;
			}
	}

	public void clearPath() {
		// Make all squares not part of path
		for (int i = 0; i < pathLengthAtSquare.length; i++)
			Arrays.fill(pathLengthAtSquare[i], -1);
	}

	public void setStart(int startX, int startY) {
		this.startX = startX;
		this.startY = startY;
	}

	public int getStartX() {
		return startX;
	}

	public int getStartY() {
		return startY;
	}

	public void setEnd(int endX, int endY) {
		this.endX = endX;
		this.endY = endY;
	}

	public int getEndX() {
		return endX;
	}

	public int getEndY() {
		return endY;
	}

	public int getColumns() {
		return columns;
	}

	public int getRows() {
		return rows;
	}

	public void setVerticalWall(int x, int y, boolean isWall) {
		vWalls[x][y] = isWall;
	}

	/**
	 * Returns whether there is a wall to the right of a given square.
	 * 
	 * @param x the x coordinate of the square
	 * @param y the y coordinate of the square
	 * @return whether there is a wall to the right of the square
	 */
	public boolean getVerticalWall(int x, int y) {
		if (x < 0 || x >= columns - 1 || y < 0 || y >= rows) return false;
		return vWalls[x][y];
	}

	public void setHorizontalWall(int x, int y, boolean isWall) {
		hWalls[x][y] = isWall;
	}

	/**
	 * Returns whether there is a wall below a given square.
	 * 
	 * @param x the x coordinate of the square
	 * @param y the y coordinate of the square
	 * @return whether there is a wall below the square
	 */
	public boolean getHorizontalWall(int x, int y) {
		if (x < 0 || x >= columns || y < 0 || y >= rows - 1) return false;
		return hWalls[x][y];
	}

	protected void setPathLengthAtSquare(int[][] pathLengthAtSquare) {
		this.pathLengthAtSquare = pathLengthAtSquare;
	}

	public int getPathLengthAtSquare(int x, int y) {
		return pathLengthAtSquare[x][y];
	}

	/** @return whether a square is part of the path */
	public boolean isPath(int x, int y) {
		return pathLengthAtSquare[x][y] > 0;
	}

	public boolean inRange(int x, int y) {
		return x >= 0 && x < columns && y >= 0 && y < rows;
	}

	public int getPathLength() {
		return pathLengthAtSquare[endX][endY];
	}
}
