package display;

import game.MazeGame;
import java.awt.CardLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

/** The menu for the game */
public class Menu extends JPanel {

	public MazePanel mazePanel = null; // the mazePanel
	private HomePane home; // home pane
	private InstructionsPane instructionsPane;
	public CardLayout cl; // this class's layout manager

	private static BufferedImage background = R.dungeon;

	public Menu() {
		super();

		// setup layout
		CardLayout cl = new CardLayout();
		setLayout(cl);
		this.cl = cl;

		// setup mazePanel
		mazePanel = new MazePanel(new MazeGame());
		add(mazePanel, "MAZEPANEL");

		// set up home screen
		home = new HomePane(this);

		add(home, "HOME SCREEN");

		// set up the instructions pane
		instructionsPane = new InstructionsPane(this);
		instructionsPane.setBackground(background);
		add(instructionsPane, "INSTRUCTIONS");

		show("HOME SCREEN");

		setSize(1000, 700);
	}

	/** show's the panel given by the name */
	public void show(String name) {
		cl.show(this, name);
		this.requestFocus();
	}
}
