package game;

import java.awt.Color;
import java.io.Serializable;

public class HealthBar extends AbstractBar implements Serializable {
	private MazeGame maze;
	
	public HealthBar(MazeGame maze){		// shows the amount of health
		this.maze = maze;
	}
	
	@Override
	public String[] getToolTipText() {
		String[] s = {"Health: " + getBarText()};
		return s;
	}

	@Override
	public String getBarText() {			// shows amount of health vs max
		return maze.getPlayer().getHealth() + " / " + maze.getPlayer().getMaxHealth();
	}

	@Override
	public Color[] getBarColors() {
		Color[] colors = {new Color(230, 30, 0), new Color(255, 100, 20), new Color(230, 30, 0), new Color(180, 20, 0)};
		return colors;
	}

	@Override
	protected int getValue() {
		return maze.getPlayer().getHealth();
	}

	@Override
	protected int getMaxValue() {
		return maze.getPlayer().getMaxHealth();
	}
}
