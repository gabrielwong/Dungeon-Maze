package game.map;

import game.MazeGame;
import game.Monster;
import game.MoveableObject;
import game.Player;
import game.Projectile;
import game.items.Candle;
import game.items.Item;
import game.items.Key;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ByteLookupTable;
import java.awt.image.LookupOp;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import maze.DrawMazeTask;
import maze.Maze;
import maze.SolveMazeTask;
import display.R;

/** Contains items, portals, lights, and doors. */
public class Map extends Maze implements Serializable {
	public ArrayList<Item> items;
	public ArrayList<Obstacle> obstacles;
	public Portal[] portals; // 0 is blue, 1 is orange
	public ArrayList<Lighting> lights;
	public ArrayList<Door> doors;
	public ArrayList<Monster> monsters;
	public ArrayList<Projectile> projectiles; // transient means don't serialize

	public transient BufferedImage mazeImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB); // image of path and walls
	private transient BufferedImage maskImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB); // image of mask (hidden areas)

	private static LookupOp lookupOp = createLookupOp();

	public boolean checkPortalsForMonsters = false;
	public boolean checkPortalsForProjectiles = true;

	public static LookupOp createLookupOp() {
		byte[][] data = new byte[4][256];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 256; j++)
				data[i][j] = (byte) j;
		}
		for (int i = 0; i < 256; i++)
			data[3][i] = (byte) (255 - i);
		ByteLookupTable lookup = new ByteLookupTable(0, data);
		return new LookupOp(lookup, null);
	}

	// Variables for the mask
	private float[] maskFractions = { 0.0F, 1.0F }; // Fractions in radial paint gradient

	public static final int SQUARE_SIZE = 60; // the px width and height of the path (including walls)
	public static final int WALL_WIDTH = 10; // width of the wall(derp)

	public static final int MAX_ITEM_SIZE = 32;
	public static final int ITEM_PADDING = (SQUARE_SIZE - MAX_ITEM_SIZE) / 2;
	private static final int SPAWN_FROM_PLAYER = 5;

	public Map() {
		this(10, 10, 5, 0.02, 0, 5, 1);
	}

	public Map(int columns, int rows, int numOfDoors, double itemDensity, int numOfPortals, int numOfMonsters, int difficultyLevel) {
		super(columns, rows);

		lights = new ArrayList<Lighting>(); // initialize array list of map objects
		items = new ArrayList<Item>();
		obstacles = new ArrayList<Obstacle>();
		doors = new ArrayList<Door>();
		monsters = new ArrayList<Monster>();
		projectiles = new ArrayList<Projectile>();

		SolveMazeTask solve = new SolveMazeTask(this); // find the path to the exit
		solve.execute();
		try {
			setPathLengthAtSquare(solve.get());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		redraw();

		generateDoors(numOfDoors); // create the doors, items, and portals
		generateItems(itemDensity);
		spawnMonsters(numOfMonsters, difficultyLevel);
		portals = new Portal[2];

	}

	public void spawnMonsters(int n, int strength) {
		// spawns monsters with different health
		for (int i = 0; i < n; i++) {
			monsters.add(Monster.randomMonster(Map.SQUARE_SIZE * (int) (Math.random() * (getColumns() - SPAWN_FROM_PLAYER) + SPAWN_FROM_PLAYER),
					Map.SQUARE_SIZE * (int) (Math.random() * (getRows() - SPAWN_FROM_PLAYER) + SPAWN_FROM_PLAYER), strength * 1.1, 1));
		}
	}

	public boolean move(MoveableObject obj, int direction) {
		return move(obj, direction, obj.getSpeed(), (Rectangle[][]) null);
	}

	/**
	 * Moves an object in a direction.
	 * 
	 * @param obj the object to be moved
	 * @param direction the direction to move the object
	 * @deprecated use move(object, direction, totalDistance, obstacles)
	 */
	public void move(MoveableObject obj, int direction, int totalDistance) {
		int x = obj.getX();
		int y = obj.getY();
		boolean primary = false;
		boolean secondary = false;

		if (totalDistance < 0) {
			totalDistance *= -1;
			direction = (direction + 2) % 4;
		}

		for (int i = 0; i < totalDistance; i += SQUARE_SIZE - WALL_WIDTH) {
			int distance = Math.min(SQUARE_SIZE / 2, totalDistance - i);
			switch (direction) {
			case Maze.NORTH:
				if (y - SQUARE_SIZE < 0)
					secondary = true;

				// Horizontal wall above and left of object
				else if (getHorizontalWall(x / SQUARE_SIZE, y / SQUARE_SIZE - 1))
					primary = true;

				// Horizontal wall above and right of object
				else if (x % SQUARE_SIZE + obj.getWidth() > SQUARE_SIZE && getHorizontalWall(x / SQUARE_SIZE + 1, y / SQUARE_SIZE - 1))
					primary = true;

				// Vertical wall above and right of object
				else if (((x % SQUARE_SIZE) + obj.getWidth() > SQUARE_SIZE - WALL_WIDTH / 2 && getVerticalWall(x / SQUARE_SIZE, y / SQUARE_SIZE - 1)))
					secondary = true;

				// Vertical wall above and left of object
				else if (x % SQUARE_SIZE < WALL_WIDTH / 2 && getVerticalWall(x / SQUARE_SIZE - 1, y / SQUARE_SIZE - 1)) secondary = true;

				// Calculate new y value
				if (primary)
					y -= Math.min(distance, (y % SQUARE_SIZE) - (WALL_WIDTH / 2));
				else if (secondary)
					y -= Math.min(distance, y % SQUARE_SIZE);
				else
					y -= distance;

				break;
			case Maze.SOUTH:
				// Avoid out of bounds
				if (y / SQUARE_SIZE >= getRows() - 1)
					secondary = true;

				// Horizontal wall below and left of object
				else if (getHorizontalWall(x / SQUARE_SIZE, y / SQUARE_SIZE))
					primary = true;

				// Horizontal wall below and right of object
				else if (x % SQUARE_SIZE + obj.getWidth() > SQUARE_SIZE && getHorizontalWall(x / SQUARE_SIZE + 1, y / SQUARE_SIZE))
					primary = true;

				// Vertical wall below and right of object
				else if (((x % SQUARE_SIZE) + obj.getWidth() > SQUARE_SIZE - WALL_WIDTH / 2 && getVerticalWall(x / SQUARE_SIZE, y / SQUARE_SIZE + 1)))
					secondary = true;

				// Vertical wall below and left of object
				else if (x % SQUARE_SIZE < WALL_WIDTH / 2 && getVerticalWall(x / SQUARE_SIZE - 1, y / SQUARE_SIZE + 1)) secondary = true;

				// Calculate new y value
				if (primary)
					y += Math.min(distance, SQUARE_SIZE - (WALL_WIDTH / 2) - ((y + obj.getHeight()) % SQUARE_SIZE));
				else if (secondary)
					y += Math.min(distance, SQUARE_SIZE - 1 - (y + obj.getHeight() - 1) % SQUARE_SIZE);
				else
					y += distance;

				break;
			case Maze.EAST:
				// Avoid out of bounds
				if (x / SQUARE_SIZE >= getColumns() - 1)
					secondary = true;

				// Vertical wall above and right of object
				else if (getVerticalWall(x / SQUARE_SIZE, y / SQUARE_SIZE))
					primary = true;

				// Vertical wall below and right of object
				else if (y % SQUARE_SIZE + obj.getHeight() > SQUARE_SIZE && getVerticalWall(x / SQUARE_SIZE, y / SQUARE_SIZE + 1))
					primary = true;

				// Horizontal wall below and right of object
				else if (((y % SQUARE_SIZE) + obj.getHeight() > SQUARE_SIZE - WALL_WIDTH / 2 && getHorizontalWall(x / SQUARE_SIZE + 1, y
						/ SQUARE_SIZE)))
					secondary = true;

				// Horizontal wall above and right of object
				else if (y % SQUARE_SIZE < WALL_WIDTH / 2 && getHorizontalWall(x / SQUARE_SIZE + 1, y / SQUARE_SIZE - 1)) secondary = true;

				// Calculate new x value
				if (primary)
					x += Math.min(distance, SQUARE_SIZE - (WALL_WIDTH / 2) - ((x + obj.getWidth()) % SQUARE_SIZE));
				else if (secondary)
					x += Math.min(distance, SQUARE_SIZE - 1 - ((x + obj.getWidth() - 1) % SQUARE_SIZE));
				else
					x += distance;

				break;
			case Maze.WEST:
				// Avoid out of bounds
				if (x - SQUARE_SIZE < 0)
					secondary = true;

				// Vertical wall above and left of object
				else if (getVerticalWall(x / SQUARE_SIZE - 1, y / SQUARE_SIZE))
					primary = true;

				// Vertical wall below and left of object
				else if (y % SQUARE_SIZE + obj.getHeight() > SQUARE_SIZE && getVerticalWall(x / SQUARE_SIZE - 1, y / SQUARE_SIZE + 1))
					primary = true;

				// Horizontal wall below and left of object
				else if (((y % SQUARE_SIZE) + obj.getHeight() > SQUARE_SIZE - WALL_WIDTH / 2 && getHorizontalWall(x / SQUARE_SIZE - 1, y
						/ SQUARE_SIZE)))
					secondary = true;

				// Horizontal wall above and left of object
				else if (y % SQUARE_SIZE < WALL_WIDTH / 2 && getHorizontalWall(x / SQUARE_SIZE - 1, y / SQUARE_SIZE - 1)) secondary = true;

				// Calculate new x value
				if (primary)
					x -= Math.min(distance, (x % SQUARE_SIZE) - (WALL_WIDTH / 2));
				else if (secondary)
					x -= Math.min(distance, x % SQUARE_SIZE);
				else
					x -= distance;
			}
		}

		obj.setX(x);
		obj.setY(y);
	}

	public boolean move(MoveableObject obj, int direction, int distance, Rectangle[]... obstacles) {
		int objX = obj.getX();
		int objY = obj.getY();

		// Reverse direction and abs distance if distance is negative
		if (distance < 0) {
			distance *= -1;
			direction = (direction + 2) % 4;
		}

		switch (direction) {
		case NORTH:
			int minY = 0;
			// Check if the object will crash into a wall (check square by square)
			for (int y = objY; y > objY - distance; y -= SQUARE_SIZE) {
				if (y - SQUARE_SIZE < 0) {
					minY = y - (y % SQUARE_SIZE);
					break;
				}
				// Horizontal wall above and left of object
				else if (getHorizontalWall(objX / SQUARE_SIZE, y / SQUARE_SIZE - 1, true)) {
					minY = y - (y % SQUARE_SIZE) + (WALL_WIDTH / 2);
					break;
				}
				// Horizontal wall above and right of object
				else if (objX % SQUARE_SIZE + obj.getWidth() > SQUARE_SIZE && getHorizontalWall(objX / SQUARE_SIZE + 1, y / SQUARE_SIZE - 1, true)) {
					minY = y - (y % SQUARE_SIZE) + (WALL_WIDTH / 2);
					break;
				}
				// Vertical wall above and right of object
				else if (((objX % SQUARE_SIZE) + obj.getWidth() > SQUARE_SIZE - WALL_WIDTH / 2 && getVerticalWall(objX / SQUARE_SIZE, y / SQUARE_SIZE
						- 1))) {
					minY = y - (y % SQUARE_SIZE);
					break;
				}
				// Vertical wall above and left of object
				else if (objX % SQUARE_SIZE < WALL_WIDTH / 2 && getVerticalWall(objX / SQUARE_SIZE - 1, y / SQUARE_SIZE - 1)) {
					minY = y - (y % SQUARE_SIZE);
					break;
				}
			}
			// Check for obstacles
			if (obstacles != null && minY != objY) {
				for (Rectangle[] rectangles : obstacles) {
					for (Rectangle r : rectangles) {
						if (objX + obj.getWidth() > r.getX() && objX < r.getX() + r.getWidth() && objY >= r.getY() + r.getHeight()) {
							minY = Math.max(minY, (int) (r.getY() + r.getHeight()));
						}
					}
				}
			}

			// Move object
			if (minY > objY - distance && minY <= objY) {
				obj.setY(minY);
				return false;
			}
			obj.setY(objY - distance);
			return true;

		case SOUTH:
			int maxY = getHeight();
			for (int y = objY; y < objY + distance; y += SQUARE_SIZE) {
				// Avoid out of bounds
				if (y / SQUARE_SIZE >= getRows() - 1) {
					maxY = y + SQUARE_SIZE - 1 - (y + obj.getHeight() - 1) % SQUARE_SIZE;
					break;
				}

				// Horizontal wall below and left of object
				else if (getHorizontalWall(objX / SQUARE_SIZE, y / SQUARE_SIZE, true)) {
					maxY = y + SQUARE_SIZE - (WALL_WIDTH / 2) - ((y + obj.getHeight()) % SQUARE_SIZE) - 1;
					break;
				}

				// Horizontal wall below and right of object
				else if (objX % SQUARE_SIZE + obj.getWidth() > SQUARE_SIZE && getHorizontalWall(objX / SQUARE_SIZE + 1, y / SQUARE_SIZE, true)) {
					maxY = y + SQUARE_SIZE - (WALL_WIDTH / 2) - ((y + obj.getHeight()) % SQUARE_SIZE) - 1;
					break;
				}

				// Vertical wall below and right of object
				else if (((objX % SQUARE_SIZE) + obj.getWidth() > SQUARE_SIZE - WALL_WIDTH / 2 && getVerticalWall(objX / SQUARE_SIZE, y / SQUARE_SIZE
						+ 1))) {
					maxY = y + SQUARE_SIZE - 1 - (y + obj.getHeight() - 1) % SQUARE_SIZE;
					break;
				}

				// Vertical wall below and left of object
				else if (objX % SQUARE_SIZE < WALL_WIDTH / 2 && getVerticalWall(objX / SQUARE_SIZE - 1, y / SQUARE_SIZE + 1)) {
					maxY = y + SQUARE_SIZE - 1 - (y + obj.getHeight() - 1) % SQUARE_SIZE;
					break;
				}
			}
			// Check for obstacles
			if (obstacles != null && maxY != objY) {
				for (Rectangle[] rectangles : obstacles) {
					for (Rectangle r : rectangles) {
						if (objX + obj.getWidth() > r.getX() && objX < r.getX() + r.getWidth() && objY + obj.getHeight() <= r.getY()) {
							maxY = Math.min(maxY, (int) (r.getY() - obj.getHeight()));
						}
					}
				}
			}

			// Move object
			if (maxY >= objY && maxY < objY + distance) {
				obj.setY(maxY);
				return false;
			}
			obj.setY(objY + distance);
			return true;
		case WEST:
			int minX = 0;
			for (int x = objX; x > objX - distance; x -= SQUARE_SIZE) {
				// Avoid out of bounds
				if (x - SQUARE_SIZE < 0) {
					minX = x - (x % SQUARE_SIZE);
					break;
				}

				// Vertical wall above and left of object
				else if (getVerticalWall(x / SQUARE_SIZE - 1, objY / SQUARE_SIZE, true)) {
					minX = x - ((x % SQUARE_SIZE) - (WALL_WIDTH / 2));
					break;
				}

				// Vertical wall below and left of object
				else if (objY % SQUARE_SIZE + obj.getHeight() > SQUARE_SIZE && getVerticalWall(x / SQUARE_SIZE - 1, objY / SQUARE_SIZE + 1, true)) {
					minX = x - ((x % SQUARE_SIZE) - (WALL_WIDTH / 2));
					break;
				}

				// Horizontal wall below and left of object
				else if (((objY % SQUARE_SIZE) + obj.getHeight() > SQUARE_SIZE - WALL_WIDTH / 2 && getHorizontalWall(x / SQUARE_SIZE - 1, objY
						/ SQUARE_SIZE))) {
					minX = x - (x % SQUARE_SIZE);
					break;
				}

				// Horizontal wall above and left of object
				else if (objY % SQUARE_SIZE < WALL_WIDTH / 2 && getHorizontalWall(x / SQUARE_SIZE - 1, objY / SQUARE_SIZE - 1)) {
					minX = x - (x % SQUARE_SIZE);
					break;
				}
			}
			// Check for obstacles
			if (obstacles != null && minX != objX) {
				for (Rectangle[] rectangles : obstacles) {
					for (Rectangle r : rectangles) {
						if (objY + obj.getHeight() > r.getY() && objY < r.getY() + r.getHeight() && objX >= r.getX() + r.getWidth())
							minX = Math.max(minX, (int) (r.getX() + r.getWidth()));
					}
				}
			}

			// Move object
			if (minX <= objX && minX > objX - distance) {
				obj.setX(minX);
				return false;
			}
			obj.setX(objX - distance);
			return true;

		case EAST:
			int maxX = getWidth();
			for (int x = objX; x < objX + distance; x += SQUARE_SIZE) {
				// Avoid out of bounds
				if (x / SQUARE_SIZE >= getColumns() - 1) {
					maxX = x + (SQUARE_SIZE - 1 - ((x + obj.getWidth() - 1) % SQUARE_SIZE));
					break;
				}

				// Vertical wall above and right of object
				else if (getVerticalWall(x / SQUARE_SIZE, objY / SQUARE_SIZE, true)) {
					maxX = x + (SQUARE_SIZE - (WALL_WIDTH / 2) - ((x + obj.getWidth()) % SQUARE_SIZE));
					break;
				}

				// Vertical wall below and right of object
				else if (objY % SQUARE_SIZE + obj.getHeight() > SQUARE_SIZE && getVerticalWall(x / SQUARE_SIZE, objY / SQUARE_SIZE + 1, true)) {
					maxX = x + (SQUARE_SIZE - (WALL_WIDTH / 2) - ((x + obj.getWidth()) % SQUARE_SIZE));
					break;
				}

				// Horizontal wall below and right of object
				else if (((objY % SQUARE_SIZE) + obj.getHeight() > SQUARE_SIZE - WALL_WIDTH / 2 && getHorizontalWall(x / SQUARE_SIZE + 1, objY
						/ SQUARE_SIZE))) {
					maxX = x + (SQUARE_SIZE - 1 - ((x + obj.getWidth() - 1) % SQUARE_SIZE));
					break;
				}

				// Horizontal wall above and right of object
				else if (objY % SQUARE_SIZE < WALL_WIDTH / 2 && getHorizontalWall(x / SQUARE_SIZE + 1, objY / SQUARE_SIZE - 1)) {
					maxX = x + (SQUARE_SIZE - 1 - ((x + obj.getWidth() - 1) % SQUARE_SIZE));
					break;
				}
			}
			// Check for obstacles
			if (obstacles != null && maxX != objX) {
				for (Rectangle[] rectangles : obstacles) {
					for (Rectangle r : rectangles) {
						if (objY + obj.getHeight() > r.getY() && objY < r.getY() + r.getHeight() && objX + obj.getWidth() <= r.getX())
							maxX = Math.min(maxX, (int) (r.getX() - obj.getWidth()));
					}
				}
			}

			// Move object
			if (maxX < objX + distance) {
				if (maxX >= objX) obj.setX(maxX);
				return false;
			}
			obj.setX(objX + distance);
			return true;
		default:
			return false;
		}
	}

	public void refreshMaskImage(int width, int height, int cornerX, int cornerY, boolean useMask) {
		// Mask of what will be shown
		// Black is shown, white is hidden
		maskImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = maskImage.createGraphics();
		if (useMask) {
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
			g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
			g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
			g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);

			// Generate negative mask
			for (int i = 0; i < lights.size(); i++) {
				Lighting l = lights.get(i);
				if (l.isOn()) {
					// Check if it actually needs to be drawn
					int thisMinX = (int) (l.getCenterX() - l.getRadius() - cornerX);
					int thisMaxX = (int) (l.getCenterX() + l.getRadius() - cornerX);
					int thisMinY = (int) (l.getCenterY() - l.getRadius() - cornerY);
					int thisMaxY = (int) (l.getCenterY() + l.getRadius() - cornerY);

					if (thisMaxX >= 0 && thisMinX <= maskImage.getWidth() && thisMaxY >= 0 && thisMinY < maskImage.getWidth()) {

						Color[] colours = { new Color(0, 0, 0, l.getIntensity()), new Color(0, 0, 0, 0) };
						g.setPaint(new RadialGradientPaint(l.getCenterX() - cornerX, l.getCenterY() - cornerY, l.getRadius(), maskFractions, colours));
						g.fillRect((int) (l.getCenterX() - l.getRadius() - cornerX), (int) (l.getCenterY() - l.getRadius() - cornerY),
								(int) (l.getRadius() * 2), (int) (l.getRadius() * 2));

					}
				}
			}

			lookupOp.filter(maskImage, maskImage);

		}// Low quality
		/* Area clip = new Area(new Rectangle(0, 0, maskImage.getWidth(), maskImage.getHeight()));
		 * for (int i = 0; i < lights.size(); i++) {
		 * Lighting l = lights.get(i);
		 * if (l.isOn()) {
		 * // Subtract the light area from the clip
		 * clip.subtract(new Area(new Ellipse2D.Double((int) (l.getCenterX() - l.getRadius() * LOW_QUALITY_RADIUS_MULTIPLIER - cornerX),
		 * (int) (l.getCenterY() - l.getRadius() * LOW_QUALITY_RADIUS_MULTIPLIER - cornerY),
		 * (int) (2 * l.getRadius() * LOW_QUALITY_RADIUS_MULTIPLIER), (int) (2 * l.getRadius() * LOW_QUALITY_RADIUS_MULTIPLIER))));
		 * }
		 * }
		 * g.setClip(clip);
		 * g.setColor(Color.BLACK);
		 * // Draw black everywhere, except for where lit
		 * g.fillRect(0, 0, maskImage.getWidth(), maskImage.getHeight()); */
	}

	public void drawMask(Graphics2D g, int width, int height, int cornerX, int cornerY, boolean hq) {
		refreshMaskImage(width, height, cornerX, cornerY, hq);
		g.drawImage(maskImage, 0, 0, null);
	}

	/** Draws the part of the map that intersects the drawArea on the graphics context given */
	public void draw(Graphics2D g, Rectangle drawArea) {
		// draw start and end doors
		g.drawImage(R.door, startX, startY, null);
		g.drawImage(R.door, endX * SQUARE_SIZE + ITEM_PADDING, endY * SQUARE_SIZE + ITEM_PADDING, null);

		for (int i = 0; i < doors.size(); i++) {
			g.drawImage(doors.get(i).getImage(), doors.get(i).getX(), doors.get(i).getY(), null);
		}
		for (int i = 0; i < items.size(); i++) { // draw items
			Item item = items.get(i);
			if (item.intersects(drawArea)) g.drawImage(item.getImage(), item.getX(), item.getY(), null);
		}
		for (int i = 0; i < obstacles.size(); i++) { // draw deployed items
			Obstacle obstacle = obstacles.get(i);
			if (obstacle.intersects(drawArea)) g.drawImage(obstacle.getImage(), obstacle.getX(), obstacle.getY(), null);
		}

		for (Portal p : portals) {
			if (p != null) {
				if (p.intersects(drawArea)) g.drawImage(p.getImage(), p.getX(), p.getY(), null);
			}
		}

		for (int i = 0; i < monsters.size(); i++) { // draw monsters
			Monster m = monsters.get(i);
			if (m.intersects(drawArea)) g.drawImage(m.getImage(), m.getX(), m.getY(), m.getWidth(), m.getHeight(), null);
		}

		for (int i = 0; i < projectiles.size(); i++) { // draw projectiles
			Projectile p = projectiles.get(i);
			if (p.intersects(drawArea)) g.drawImage(p.getImage(), p.getX(), p.getY(), null);
		}
	}

	/** Redraws the basic image of the maze (walls and paths). */
	public void redraw() {
		final DrawMazeTask task = new DrawMazeTask(this, SQUARE_SIZE, WALL_WIDTH);

		try {
			task.execute();
			mazeImage = task.get();
		} catch (Exception ex) {
		}
	}

	private void generateDoors(int num) {
		doors.clear();
		int pixelX = -1, pixelY = -1, gridX = -1, gridY = -1, dir = 1; // gridX, gridY are based on array system
		boolean isVerticalDoor = false;
		boolean doorGenerated = false;

		for (int i = 0; i < num; i++) { // create random door on the right and bottom of sq
			doorGenerated = false;
			while (!doorGenerated) {
				boolean doorFound = false;
				int square = (int) (Math.random() * ((getPathLength() - 5) / num) + ((getPathLength() - 5) / num) * i + 5); // not the first 5 squares

				for (int x = Math.max(0, getStartX() - square); x < getStartX() + square && !doorFound; x++) {
					for (int y = Math.max(0, getStartY() - square); y <= getStartY() + square && y < getRows() && !doorFound; y++) {
						if (square == getPathLengthAtSquare(x, y)) { // find where door is
							doorFound = true;
							dir = findDirectionOfSquare(x, y, pathLengthAtSquare);
							if (dir == NORTH || dir == SOUTH)
								isVerticalDoor = false;
							else
								isVerticalDoor = true;

							if (dir == WEST)
								x--;
							else if (dir == NORTH) y--;

							if (!hasBarrier(x, y, isVerticalDoor)) { // can be placed
								gridX = x;
								gridY = y;
								if (isVerticalDoor) {
									pixelX = (x + 1) * SQUARE_SIZE - (WALL_WIDTH / 2);
									pixelY = y * SQUARE_SIZE;
								} else {
									pixelX = x * SQUARE_SIZE;
									pixelY = (y + 1) * SQUARE_SIZE - (WALL_WIDTH / 2);
								}

								doors.add(new Door(pixelX, pixelY));
								doors.get(doors.size() - 1).setVertical(isVerticalDoor);
								doors.get(i).setGridX(gridX);
								doors.get(i).setGridY(gridY);
								generateKeys(i);
								doorGenerated = true; // break loop
							}
						}
					}
				}// end search for square
			}
		}
	}

	/** Generate a key for a specific door that must be accessible */
	private void generateKeys(int doorNum) {
		SolveMazeTask findKey;
		Key key = new Key();
		int hPadding = (SQUARE_SIZE - key.getWidth()) / 2; // for centering
		int vPadding = (SQUARE_SIZE - key.getHeight()) / 2;

		int[][] keyPath = new int[getColumns()][getRows()]; // path from previous door (or start) to key
		int gridX = -1, gridY = -1, x = -1, y = -1;

		while (true) {
			gridX = (int) (Math.random() * getColumns());
			gridY = (int) (Math.random() * getRows());
			x = gridX * SQUARE_SIZE + hPadding;
			y = gridY * SQUARE_SIZE + vPadding;

			if (isPath(gridX, gridY) || hasItem(x + SQUARE_SIZE / 2, y + SQUARE_SIZE / 2)) continue;

			// System.out.println(" LOCATION OF KEY " + gridX + " " + gridY);
			if (doorNum != 0)
				findKey = new SolveMazeTask(this, doors.get(doorNum - 1).getGridX(), doors.get(doorNum - 1).getGridY(), gridX, gridY);
			else
				findKey = new SolveMazeTask(this, getStartX(), getStartY(), gridX, gridY);

			findKey.execute();
			try {
				keyPath = findKey.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}

			// Check if a door is in the path
			if (keyPath[doors.get(doorNum).getGridX()][doors.get(doorNum).getGridY()] == -1) {
				key.setX(x);
				key.setY(y);
				items.add(key);
				break;
			}
		}
	}

	/** Check if there is a wall or door at that location */
	private boolean hasBarrier(int x, int y, boolean isVertical) { // grid system
		if (isVertical && this.getVerticalWall(x, y))
			return true;
		else if (!isVertical && this.getHorizontalWall(x, y))
			return true;
		else
			for (int i = 0; i < doors.size(); i++)
				// check for doors
				if (x == doors.get(i).getGridX() && y == doors.get(i).getGridY()) return true;
		return false;
	}

	/** Find the direction of the next square in path */
	private int findDirectionOfSquare(int x, int y, int[][] path) { // grid system
		int sq2 = path[x][y] + 1;
		try {
			if (y > 0) if (sq2 == path[x][y - 1]) return NORTH;
			if (x < getColumns() - 1) if (sq2 == path[x + 1][y]) return EAST;
			if (y < getRows() - 1) if (sq2 == path[x][y + 1]) return SOUTH;
			if (x > 0) if (sq2 == path[x - 1][y]) return WEST;
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		return -1;
	}

	private void generateItems(double density) {
		double candleDensity = 0.05; // generates the candles to light the path
		for (int column = 0; column < getColumns(); column++) {
			for (int row = 0; row < getRows(); row++) {
				if (isPath(column, row) && Math.random() < candleDensity) {
					Candle c = new Candle();
					int hPadding = (SQUARE_SIZE - c.getWidth()) / 2;
					int vPadding = (SQUARE_SIZE - c.getHeight()) / 2;
					c.setX(column * SQUARE_SIZE + hPadding);
					c.setY(row * SQUARE_SIZE + vPadding);
					lights.add(c);
					items.add(c);
				}
			}
		}

		for (int x = SQUARE_SIZE; x < (columns - 1) * SQUARE_SIZE; x += SQUARE_SIZE) {
			for (int y = SQUARE_SIZE; y < (rows - 1) * SQUARE_SIZE; y += SQUARE_SIZE) {
				if (!hasItem(x + SQUARE_SIZE / 2, y + SQUARE_SIZE / 2) && Math.random() < density) {
					// for centering
					Item item = Item.randomItem(x, y);
					int hPadding = (SQUARE_SIZE - item.getWidth()) / 2;
					int vPadding = (SQUARE_SIZE - item.getHeight()) / 2;
					item.setX(x + hPadding);
					item.setY(y + vPadding);
					items.add(item);
				}
			}
		}
	}

	/**
	 * @deprecated
	 * @param numPortals
	 */
	public void generatePortals(int numPortals) {
		portals = new Portal[numPortals * 2];
		for (int i = 0; i < portals.length; i += 2) {
			int hPadding = (SQUARE_SIZE - Portal.orange.getWidth(null)) / 2;
			int vPadding = (SQUARE_SIZE - Portal.orange.getHeight(null)) / 2;
			Portal a = new Portal((int) (Math.random() * (columns - 1)) * SQUARE_SIZE + hPadding, (int) (Math.random() * (rows - 1)) * SQUARE_SIZE
					+ vPadding, true);
			Portal b = new Portal((int) (Math.random() * (columns - 1)) * SQUARE_SIZE + hPadding, (int) (Math.random() * (rows - 1)) * SQUARE_SIZE
					+ vPadding, false);
			a.setEndPortal(b);
			b.setEndPortal(a);
			portals[i] = a;
			portals[i + 1] = b;
		}
	}

	/** Check if there is an item at that location */
	private boolean hasItem(int x, int y) {
		for (int i = 0; i < items.size(); i++) {
			Item item = items.get(i);
			if (item.contains(x, y)) return true;
		}
		return false;
	}

	public boolean addLight(Lighting l) {
		return lights.add(l);
	}

	public boolean removeLight(Lighting l) {
		return lights.remove(l);
	}

	/** Checks if character is above an item, takes its action, and removes the item */
	public void checkForItems(Player player, MazeGame mazeGame) {
		for (int i = 0; i < items.size(); i++) {
			Item item = items.get(i);
			if (item.intersects(player)) {
				if (mazeGame.isSoundOn()) item.playSoundEffect();
				if (item.doPickupAction(mazeGame, player)) {
					items.remove(i);
				}
				break;
			}
		}
	}

	/**
	 * Handles portals. If player is above a portal, it will teleport the player to the portal's endpoint.
	 * 
	 * @return whether a portal took effect or not
	 */
	public boolean checkAllPortalsFor(MoveableObject object, MazeGame maze) {
		for (Portal portal : portals) {
			if (portal.intersects(object) && portal.isActive) {
				portal.lit = true;
				portal.getEndPortal().lit = true;
				if (!lights.contains(portal)) lights.add(portal);
				if (!lights.contains(portal.getEndPortal())) lights.add(portal.getEndPortal());
				object.setX(object.x + portal.getEndPortal().x - portal.x);
				object.setY(object.y + portal.getEndPortal().y - portal.y);
				portal.getEndPortal().isActive = false; // so that you're not automatically teleported back

				if(maze.isSoundOn())
					portal.playSoundEffect();
				return true;
			} else if (!portal.isActive && !portal.intersects(object)) portal.isActive = true; // reactivate portals once player exists it
		}
		return false;
	}

	public boolean checkForPortal(Portal portal, MoveableObject object, MazeGame maze) {
		if (portal.intersects(object)) {
			if (portal.isActive) {
				portal.lit = true;
				portal.getEndPortal().lit = true;
				if (!lights.contains(portal)) lights.add(portal);
				if (!lights.contains(portal.getEndPortal())) lights.add(portal.getEndPortal());
				object.setX(portal.getEndPortal().getCenterX() - object.getWidth() / 2);
				object.setY(portal.getEndPortal().getCenterY() - object.getHeight() / 2);
				portal.getEndPortal().isActive = false; // so that you're not automatically teleported back
				if (maze.isSoundOn())
					portal.playSoundEffect();
			}
			return true;
		} else
			return false;
	}

	public boolean checkForPortal(Portal p, MoveableObject[] objects, MazeGame maze) {
		boolean anyIntersects = false;
		for (MoveableObject o : objects) {
			if (checkForPortal(p, o, maze)) anyIntersects = true;
		}
		return anyIntersects;
	}

	public void checkForPortals(MazeGame maze) {
		checkForPortals(null, maze);
	}

	public void checkForPortals(Player player, MazeGame maze) {
		MoveableObject[] MO = new MoveableObject[0];
		for (Portal portal : portals) {
			if (portal != null) {
				boolean noneIntersects = true;
				if (player != null && checkForPortal(portal, player, maze)) noneIntersects = false;
				if (checkPortalsForMonsters) if (checkForPortal(portal, monsters.toArray(MO), maze)) noneIntersects = false;
				if (checkPortalsForProjectiles) if (checkForPortal(portal, projectiles.toArray(MO), maze)) noneIntersects = false;
				if (!portal.isActive && noneIntersects) portal.isActive = true;
			}
		}
	}

	/* public void checkForPortals(MoveableObject... moveableObjects) {
	 * for (Portal portal : portals) {
	 * if (portal != null) {
	 * boolean noneIntersects = true;
	 * for (MoveableObject object : moveableObjects) {
	 * if (portal.intersects(object)) {
	 * noneIntersects = false;
	 * if (portal.isActive) {
	 * portal.playSoundEffect();
	 * portal.lit = true;
	 * portal.getEndPortal().lit = true;
	 * if (!lights.contains(portal)) lights.add(portal);
	 * if (!lights.contains(portal.getEndPortal())) lights.add(portal.getEndPortal());
	 * object.setCenterX(portal.getEndPortal().getCenterX());
	 * object.setCenterY(portal.getEndPortal().getCenterY());
	 * portal.getEndPortal().isActive = false; // so that you're not automatically teleported back
	 * }
	 * }
	 * }
	 * if (!portal.isActive && noneIntersects) portal.isActive = true;
	 * }
	 * }
	 * } */

	/** Returns the first item it finds on a coordinate point, null if none was found */
	public Item getItem(int x, int y) {
		for (int i = 0; i < items.size(); i++) {
			Item item = items.get(i);
			if (item.contains(x, y)) return item;
		}
		return null;
	}

	public boolean getHorizontalWall(int x, int y, boolean checkForDoors) {
		if (super.getHorizontalWall(x, y)) {
			return true;
		}
		if (checkForDoors) {
			for (int i = 0; i < doors.size(); i++) {
				Door d = doors.get(i);
				if (d.isClosed() && !d.isVertical() && d.getGridX() == x && d.getGridY() == y) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean getVerticalWall(int x, int y, boolean checkForDoors) {
		if (super.getVerticalWall(x, y)) {
			return true;
		}
		if (checkForDoors) {
			for (int i = 0; i < doors.size(); i++) {
				Door d = doors.get(i);
				if (d.isClosed() && d.isVertical() && d.getGridX() == x && d.getGridY() == y) {
					return true;
				}
			}
		}
		return false;
	}

	public int getWidth() {
		return SQUARE_SIZE * getColumns();
	}

	public int getHeight() {
		return SQUARE_SIZE * getRows();
	}

	public int getColumns() {
		return super.getColumns();
	}

	public int getRows() {
		return super.getRows();
	}

	public Rectangle[] getUserObstacleBounds() {
		Rectangle[] rectangles = new Rectangle[obstacles.size()];
		for (int i = 0; i < obstacles.size(); i++) {
			rectangles[i] = obstacles.get(i).getBounds();
		}
		return rectangles;
	}
}
