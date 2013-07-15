package game;

import java.awt.CardLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

public class Menu extends JPanel implements ActionListener {
	/**
	 * 
	 */

	//private static final String newGameBtnName = "New Game", continueBtnName = "Continue", instructionsBtnName = "Instructions";
	
	private JButton newGame, continueBtn, instructionsBtn;
	private InstructionsPane instructionsPane = new InstructionsPane();
	private Box horizontalBox, verticalBox;

	public MazeGame mazeGame;
	
	private static BufferedImage background = loadImage("", "jpg");

	public Menu() {
		super(new CardLayout());
		newGame = setupButton("New Game");
		continueBtn = setupButton("Continue");
		instructionsBtn = setupButton("Instructions");

		add(instructionsPane, "instructions");
		
		setSize(1000, 700);
	}

	public JButton setupButton(String name) {
		JButton btn = new JButton(name);
		btn.addActionListener(this);
		return btn;
	}

	/* public void run () {
	 * setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	 * 
	 * //Centered on the screen
	 * horizontalBox = Box.createHorizontalBox();
	 * horizontalBox.add(Box.createHorizontalGlue());
	 * horizontalBox.add(btns);
	 * horizontalBox.add(Box.createHorizontalGlue());
	 * verticalBox = Box.createVerticalBox();
	 * for (int i = 0 ; i < 25; i++)
	 * verticalBox.add(Box.createVerticalGlue());
	 * 
	 * verticalBox.add(horizontalBox);
	 * for (int i = 0 ; i < 18; i++)
	 * verticalBox.add(Box.createVerticalGlue());
	 * 
	 * this.add(verticalBox);
	 * btns.add(newGame);
	 * btns.add(continueBtn);
	 * } */

	public void paintComponent(Graphics g) {
		g.drawImage(background, 0, 0, null);
	}

	private static BufferedImage loadImage(String path, String format) {
		BufferedImage image = new BufferedImage(1000, 700, BufferedImage.TYPE_INT_RGB);

		try {
			image = ImageIO.read(Menu.class.getResource("/res/img/screenpictures/dungeon.jpg"));
		} catch (IOException e) {
			image = null;
			e.printStackTrace();
		}

		return image;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if (src.equals(newGame)) {
		} else if (src.equals(continueBtn)) {

		} else if (src.equals(instructionsBtn)) {
			((CardLayout) getLayout()).show(this, "instructions");
		}
	}
}
