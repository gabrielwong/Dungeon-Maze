package maze;

import java.awt.Point;
import java.util.Arrays;
import java.util.LinkedList;

import javax.swing.SwingWorker;

public class SolveMazeTask extends SwingWorker<int[][], Void> {
	private final Maze maze;
	private final int startX, startY, endX, endY;
	private int[][] pathLengthAtSquare;
	private int pathLength;

	public SolveMazeTask(Maze maze, int startX, int startY, int endX, int endY){
		this.maze = maze;
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
		pathLengthAtSquare = new int[maze.getColumns()][maze.getRows()];
		for (int i = 0; i < pathLengthAtSquare.length; i++)
			Arrays.fill(pathLengthAtSquare[i], -1);
	}
	public SolveMazeTask(Maze maze){
		this(maze, maze.getStartX(), maze.getStartY(), maze.getEndX(), maze.getEndY());
	}
	
	@Override
	/**
	 * Solves the maze using an algorithm that implements the recursive maze solving algorithm as a loop.
	 * It will start from the end and work backwards because there are less forks in the maze working backwards.
	 * 
	 * 1. Get a coordinate from the stack.
	 * 2. Mark the square as visited.
	 * 3. If the last cell was part of the path
	 * 	a. Make it part of the path
	 * 	b. Remove it from the stack
	 *  c. Repeat from step 1
	 * 4. If it is the target square
	 * 	a. Make it part of the path
	 * 	b. Clear directional information from stack
	 * 	c. Repeat from step 1
	 * 5. Get direction from stack and increment direction in the stack
	 * 6. If neighbour in the old direction is unvisited and not separated by a wall
	 * 	a. Add the neighbour to the stack with the default direction of north
	 * 7. If there are no neighbours, remove the cell from the stack and remove the direction information
	 */
	protected int[][] doInBackground() throws Exception {
		int nVisited = 0; // Number of squares visited
		int nTotal = maze.getColumns() * maze.getRows();

		boolean[][] visited = new boolean[maze.getColumns()][maze.getRows()];
		LinkedList<Point> stack = new LinkedList<Point>();

		// Make all squares not visited
		for (int i = 0; i < visited.length; i++)
			Arrays.fill(visited[i], false);

		// Stores information about the last direction processed
		LinkedList<Integer> directionStack = new LinkedList<Integer>();

		boolean isPathLastCell = false; // If the last cell processed is part of the path.

		// Clear stack and add the starting coordinate.
		stack.clear();
		stack.add(new Point(startX, startY));
		directionStack.add(0);

		// Make all squares unvisited
		for (int i = 0; i < visited.length; i++)
			Arrays.fill(visited[i], false);

		// Loop to process all cells
		while (stack.size() > 0){
			// 1. Get coordinate from stack
			int x = stack.getLast().x;
			int y = stack.getLast().y;

			// 2. If the last cell was part of the path
			if (isPathLastCell){
				nVisited++;
				setProgress((5 * nVisited) / pathLength + 95);
				// a. Make it part of the path
				pathLengthAtSquare[x][y] = stack.size();
				
				// b. Remove it from the stack
				stack.removeLast();
				
				continue;
			}

			if (! visited[x][y]){
				// Update the progress
				nVisited++;
				setProgress((95 * nVisited) / nTotal);
			}

			// 3. Mark cell as visited
			visited[x][y] = true;

			// 4, If it is the target cell
			if (x == endX && y == endY){
				pathLength = stack.size();
				nVisited = 0;
				// a. Make it part of the path
				isPathLastCell = true;
				pathLengthAtSquare[x][y] = stack.size();

				// b. Remove it from the stack
				stack.removeLast();
				
				// c. Clear directional information
				directionStack.clear();
				directionStack = null;
				
				continue;
			}

			// 5. Get direction from the stack
			int direction = directionStack.getLast();

			// Increment direction in the stack
			directionStack.set(directionStack.size() - 1, direction + 1);
			switch (direction){
			case Maze.NORTH:
				// 6. If unvisited and not separated by a wall
				if (y > 0){
					if ( ! maze.getHorizontalWall(x, y - 1) && ! visited[x][y - 1]){
						// a. Add the neighbour to the stack and add a default direction
						stack.add(new Point(x, y - 1));
						directionStack.add(0);
					}
				}
				break;
			case Maze.SOUTH:
				if (y < maze.getRows() - 1){
					if ( ! maze.getHorizontalWall(x, y) && ! visited[x][y + 1]){
						stack.add(new Point(x, y + 1));
						directionStack.add(0);
					}
				}
				break;
			case Maze.EAST:
				if (x < maze.getColumns() - 1){
					if ( ! maze.getVerticalWall(x, y) && ! visited[x + 1][y]){
						stack.add(new Point(x + 1, y));
						directionStack.add(0);
					}
				}
				break;
			case Maze.WEST:
				if (x > 0){
					if ( ! maze.getVerticalWall(x - 1, y) && ! visited[x - 1][y]){
						stack.add(new Point(x - 1, y));
						directionStack.add(0);
					}
				}
				break;
			default:
				// 7. If there are no neighbours, remove it from the stack
				stack.removeLast();
				directionStack.removeLast();
			}
		}

		setProgress(100);
		return pathLengthAtSquare;
	}
	
	public int getPathLength() {
		return pathLength;
	}
}
