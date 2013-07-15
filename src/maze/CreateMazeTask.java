package maze;

import java.awt.Point;
import java.util.Arrays;
import java.util.LinkedList;
import javax.swing.SwingWorker;

/** Creates a maze in the background */
public class CreateMazeTask extends SwingWorker<Maze, Void> {

	public final Maze maze;
	private boolean[][] visited;

	public CreateMazeTask(Maze maze) {
		this.maze = maze;
	}

	@Override
	/**
	 * Creates the maze using an algorithm that implements the recursive maze generating algorithm as a loop.
	 * It will generate the maze from the end square to the starting square to add complexity to the maze.
	 * Using this algorithm, the maze has more branches near the end square than the start square.
	 * 
	 * 1. Get a coordinate from the stack.
	 * 2. Mark the square as visited.
	 * 3. If it is the end square, remove it from the stack and repeat from step 1.
	 * 4. Select a random unvisited neighbour.
	 * 	a. Remove the wall between the current square and the neighbour.
	 * 	b. Add the neighbour to the stack.
	 * 5. If there are no neighbours, remove the current cell from the stack.
	 */
	protected Maze doInBackground() {
		int nVisited = 0; // Number of squares visited
		int nTotal = maze.getColumns() * maze.getRows();

		LinkedList<Point> stack = new LinkedList<Point>();
		stack.add(new Point(maze.getEndX(), maze.getEndY()));

		// Make all the walls present
		maze.populateWalls();

		maze.clearPath();

		// Make all squares not visited
		visited = new boolean[maze.getColumns()][maze.getRows()];
		for (int i = 0; i < visited.length; i++)
			Arrays.fill(visited[i], false);

		// Loop to remove walls
		while (stack.size() > 0) {
			// 1. Get a coordinate from the stack
			int x = stack.getLast().x;
			int y = stack.getLast().y;

			if (!visited[x][y]) {
				// Update the progress
				nVisited++;
				setProgress((100 * nVisited) / nTotal);
			}

			// 2. Mark square as visited
			visited[x][y] = true;

			// 3. Checks whether this square is the target (end) square.
			// If it is, remove it from the stack and continue with the loop.
			if (x == maze.getStartX() && y == maze.getStartY()) {
				stack.removeLast();
				continue;
			}

			// 4. Select random neighbour
			int neighbour = randomNeighbour(x, y);
			switch (neighbour) {
			case Maze.NORTH:
				// 5. Remove wall between the 2 cells
				maze.setHorizontalWall(x, y - 1, false);

				// 6. Add the cell to the stack
				stack.add(new Point(x, y - 1));
				break;
			case Maze.SOUTH:
				maze.setHorizontalWall(x, y, false);
				stack.add(new Point(x, y + 1));
				break;
			case Maze.EAST:
				maze.setVerticalWall(x, y, false);
				stack.add(new Point(x + 1, y));
				break;
			case Maze.WEST:
				maze.setVerticalWall(x - 1, y, false);
				stack.add(new Point(x - 1, y));
				break;
			case -1:
				// 7. If there are no neighbours, remove the current cell from the stack
				stack.removeLast();
				break;
			}
		}
		return maze;
	}

	/**
	 * Checks whether a given square has any unvisited neighbours.
	 * 
	 * @param x the x coordinate of the square
	 * @param y the y coordinate of the square
	 * @return if the square has an unvisited neighbour
	 */
	protected boolean hasNeighbour(int x, int y) {
		// Look in all four directions
		for (int direction = 0; direction <= 3; direction++) {
			// If the neighbour in one direction is not visited, then there is a neighbour
			switch (direction) {
			case Maze.NORTH:
				if (y > 0) if (!visited[x][y - 1]) return true;
				break;
			case Maze.SOUTH:
				if (y < maze.getRows() - 1) if (!visited[x][y + 1]) return true;
				break;
			case Maze.WEST:
				if (x > 0) if (!visited[x - 1][y]) return true;
				break;
			case Maze.EAST:
				if (x < maze.getColumns() - 1) if (!visited[x + 1][y]) return true;
			}
		}
		return false;
	}

	/**
	 * Returns a random direction in which a given square has an unvisited neighbour. Returns -1 if there is none.
	 * 
	 * @param x the x coordinate of the square
	 * @param y the y coordinate of the square
	 * @return a random direction that has an unvisited neighbour
	 */
	protected int randomNeighbour(int x, int y) {
		// Check if there is a neighbour to avoid infinite loop
		if (!hasNeighbour(x, y)) return -1;

		while (true) {
			// Randomly selected a direction
			int direction = (int) (Math.random() * 4);

			// Return that direction if the neighbour has not been visited
			switch (direction) {
			case Maze.NORTH:
				if (y > 0) if (!visited[x][y - 1]) return Maze.NORTH;
				break;
			case Maze.SOUTH:
				if (y < maze.getRows() - 1) if (!visited[x][y + 1]) return Maze.SOUTH;
				break;
			case Maze.WEST:
				if (x > 0) if (!visited[x - 1][y]) return Maze.WEST;
				break;
			case Maze.EAST:
				if (x < maze.getColumns() - 1) if (!visited[x + 1][y]) return Maze.EAST;
			}
		}
	}
}
