package display;

import game.MazeGame;
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

/** A JPanel that contains a maze game and sends it timer events */
public class MazePanel extends JPanel implements MouseListener, ActionListener, KeyListener, ComponentListener {

	private MazeGame maze; // Maze object
	private Timer keyTimer;
	boolean mute = false; // whether the song should be muted

	public MazePanel(MazeGame maze) {
		this.maze = maze;
		keyTimer = new Timer(32, this);

		// song.loop();

		/* Another way to implement audio
		 * try {
		 * AudioInputStream inputStream = AudioSystem.getAudioInputStream(MazePanel.class.getResource("/res/aud/Adad's descent.wav"));
		 * 
		 * // load the sound into memory (a Clip)
		 * AudioFormat format = inputStream.getFormat();
		 * DataLine.Info info = new DataLine.Info(Clip.class, format);
		 * Clip audio = (Clip) AudioSystem.getLine(info);
		 * 
		 * audio.open(inputStream); //open file
		 * 
		 * audio.addLineListener(new LineListener() {
		 * public void update(LineEvent event) {
		 * if (event.getType() == LineEvent.Type.STOP) {
		 * event.getLine().close();
		 * }
		 * }
		 * });
		 * 
		 * audio.start(); //start
		 * } catch (LineUnavailableException e) {
		 * e.printStackTrace();
		 * } catch (IOException e) {
		 * e.printStackTrace();
		 * } catch (UnsupportedAudioFileException e) {
		 * e.printStackTrace();
		 * } */

		addMouseListener(this); // add in various listeners
		addKeyListener(this);
		addComponentListener(this);
		requestFocus();
		setBackground(Color.BLACK);
		setForeground(Color.BLACK);

		initializeMaze();
		setSizes();
	}

	/** registers listeners with the maze */
	private void initializeMaze() {
		addMouseListener(maze);
		addMouseMotionListener(maze);
		addKeyListener(maze);
	}

	public MazeGame getGame() {
		return maze;
	}

	public void startNewGame() {
		maze.createNewLevel();
		keyTimer.start();
		requestFocus();
	}

	public void paintComponent(Graphics g) {
		g.fillRect(0, 0, getWidth(), getHeight());
		g.drawImage(maze.getImage(), 0, 0, null);
	}

	private void setSizes() {
		setPreferredSize(new Dimension(maze.getWidth(), maze.getHeight()));
	}

	public void mouseClicked(MouseEvent e) {
		requestFocus();
	}

	public void mousePressed(MouseEvent e) {}

	public void mouseReleased(MouseEvent e) {}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}

	public void actionPerformed(ActionEvent e) {
		maze.update();
		repaint();
	}

	public void componentHidden(ComponentEvent arg0) {}

	public void componentMoved(ComponentEvent arg0) {}

	@Override
	public void componentResized(ComponentEvent arg0) {
		maze.setSize(getWidth(), getHeight());
	}

	public void save() {
		maze.save("save.wobwob");
	}

	/** loads a new maze game to this panel */
	public void load() {
		// remove listeners from the old maze (which would be null)
		removeMouseListener(maze);
		removeMouseMotionListener(maze);
		removeKeyListener(maze);
		
		maze = MazeGame.load("save.wobwob");
		initializeMaze();
		System.out.println ("The maze game:" + maze);
		keyTimer.start();
		requestFocus();
	}

	public void componentShown(ComponentEvent arg0) {}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyChar() == 'n') {
			save();
			System.out.println("Saved.");
		} else if (e.getKeyChar() == 'm') {
			load();
			System.out.println("Loaded.");
		}

	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}
}
