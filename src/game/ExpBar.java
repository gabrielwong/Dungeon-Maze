package game;

import java.awt.Color;
import java.io.Serializable;

public class ExpBar extends AbstractBar implements Serializable { 	// shows player's exp
	private MazeGame maze;
	public ExpBar(MazeGame maze){
		this.maze = maze;
	}
	@Override
	public String[] getToolTipText() {
		String[] s = {"Exp: " + getBarText()};
		return s;
	}

	@Override
	public String getBarText() {		// shows amount vs max
		return maze.getPlayer().getExp() + " / " + maze.getPlayer().getMaxExp();
	}

	@Override
	protected Color[] getBarColors() {
		Color[] colors = { new Color(160, 105, 15), new Color(225, 175, 7), new Color(160, 105, 15), new Color(125, 75, 0) };
		return colors;
	}

	@Override
	protected int getValue() {
		return maze.getPlayer().getExp();
	}

	@Override
	protected int getMaxValue() {
		return maze.getPlayer().getMaxExp();
	}

}
