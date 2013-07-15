package game;

import java.awt.Color;
import java.io.Serializable;

public class ManaBar extends AbstractBar implements Serializable {
	private MazeGame maze;
	public ManaBar(MazeGame maze){
		this.maze = maze;
	}
	@Override
	public String[] getToolTipText() {
		String[] s = {"Mana: " + getBarText()};
		return s;
	}

	@Override
	public String getBarText() {
		return maze.getPlayer().getMana() + " / " + maze.getPlayer().getMaxMana();
	}

	@Override
	protected Color[] getBarColors() {
		Color[] colors = { new Color(0, 0, 245), new Color(20, 105, 255), new Color(0, 0, 245), new Color(0, 0, 200) };
		return colors;
	}

	@Override
	protected int getValue() {
		return maze.getPlayer().getMana();
	}

	@Override
	protected int getMaxValue() {
		return maze.getPlayer().getMaxMana();
	}

}
