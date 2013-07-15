package game.buff;

import game.AbstractCountdownObject;
import game.MazeGame;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.io.Serializable;

/** Game events that last for a while (like a sight boost that lasts for several seconds */
public abstract class AbstractBuff extends AbstractCountdownObject implements Serializable {
	public static final int WIDTH = 32;
	public static final int HEIGHT = 32;

	public AbstractBuff() {
		setDrawDirection(false);
	}

	@Override
	public void actionPerformed(InputEvent evt) {
		if ((evt.getModifiers() & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK) setDuration(0);
	}

	public String[] getToolTipText() {
		if (!hasTimeLeft()) return null;
		String[] s = { getBaseText(), "Time left: " + (getTimeLeft() / 1000 + 1) + " s" };
		return s;
	}

	protected abstract String getBaseText();
}
