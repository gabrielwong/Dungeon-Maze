package game;

import java.awt.Image;
import java.awt.event.InputEvent;

public interface ClickableObject{
	public int getX();
	public int getY();
	public int getWidth();
	public int getHeight();
	public String[] getToolTipText();
	public int getKeyCode();
	public Image getImage();
	public void actionPerformed(InputEvent evt);
}
