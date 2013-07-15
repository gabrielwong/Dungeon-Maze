package display;

import game.MazeGame;
import java.applet.AudioClip;
import java.awt.CardLayout;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import javax.swing.JFrame;
import javax.swing.Timer;

/** The main JFrame for the game */
public class MainFrame extends JFrame implements WindowListener, WindowFocusListener {

	private Menu menu;

	public MainFrame() {
		super("Dungeon Maze"); // title it Dungeon Maze

		menu = new Menu();
		setContentPane(menu);

		// listen to window changes (for saving)
		addWindowListener(this);
		addWindowFocusListener(this);

		setSize(1024, 760);
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	public static void main(String[] args) {
		new MainFrame();
	}

	@Override
	/** save before the window closes */
	public void windowClosing(WindowEvent arg0) {
		menu.mazePanel.getGame().save("save.wobwob");
		System.exit(0);
	}

	@Override
	public void windowActivated(WindowEvent arg0) {}

	@Override
	public void windowClosed(WindowEvent arg0) {}

	@Override
	public void windowDeactivated(WindowEvent arg0) {}

	@Override
	public void windowDeiconified(WindowEvent arg0) {}

	@Override
	public void windowIconified(WindowEvent arg0) {}

	@Override
	public void windowOpened(WindowEvent arg0) {}

	@Override
	public void windowGainedFocus(WindowEvent arg0) {}

	@Override
	public void windowLostFocus(WindowEvent arg0) {}

}
