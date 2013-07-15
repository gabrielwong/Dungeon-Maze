package display;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * A simple class to represent a GUIPane (i.e. welcome screen, instructions screen).
 * The point is to keep a copy of the root panel so you can navigate back.
 */
public class GUIPane extends JPanel {
	public Menu rootMenu;
	public Image bg = null;

	public GUIPane(Menu m) {
		rootMenu = m;
	}

	public void setBackground(Image img) {
		bg = img;
	}

	/** draw's the background image if set */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (bg != null) g.drawImage(bg, (getWidth() - bg.getWidth(null)) / 2, (getHeight() - bg.getHeight(null)) / 2, null); // draw background
	}
}
