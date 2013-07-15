package display;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

/** The main screen when the game is run */
public class HomePane extends GUIPane implements ActionListener {
	private JButton newGame, continueBtn, instructionsBtn; // nav buttons

	public HomePane(Menu m) {
		super(m);
		setBackground(R.dungeon);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(Box.createVerticalGlue()); // for vertical centering
		newGame = setupButton("New Game");
		continueBtn = setupButton("Continue");
		instructionsBtn = setupButton("Instructions");
		add(Box.createVerticalGlue()); // for vertical centering
	}

	/** adds this class as the action listener to the button, center-aligns it, and adds it to the panel */
	public JButton setupButton(String name) {
		JButton btn = new JButton(name);
		btn.addActionListener(this);
		btn.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		add(btn);
		return btn;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g); // draw background
		// draw title
		g.drawImage(R.title, (getWidth() - R.title.getWidth()) / 2, (getHeight() - bg.getHeight(null)) / 2, null); // draw title image
	}

	@Override
	/** listens to button clicks */
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if (src.equals(newGame)) {
			rootMenu.mazePanel.startNewGame();
			rootMenu.show("MAZEPANEL");
		} else if (src.equals(continueBtn)) {
			rootMenu.mazePanel.load();
			rootMenu.show("MAZEPANEL");
		} else if (src.equals(instructionsBtn)) {
			rootMenu.show("INSTRUCTIONS");
		}
	}
}
