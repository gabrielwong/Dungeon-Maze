package display;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/** The pane containing the instructions */
public class InstructionsPane extends GUIPane implements ActionListener {
	private JButton back = new JButton("Back");
	private JTextArea text = new JTextArea(40, 85);
	private JScrollPane scroll = new JScrollPane(text);

	public InstructionsPane(Menu m) {
		super(m);
		text.setEditable(false);
		text.setLineWrap(true);
		text.setText("How To Play \n" +
				"\nWelcome to the world of Dungeon Maze! You are a fearless superhero on a perilous quest to save" +
				" Princess Daisy from the evil Bowser. She is locked 15 levels below MR.EVIL's castle of mazes. " +
				"Hundreds of heroes have pursued this quest, but none have yet to come out of the castle alive. " +
				"You must use strategy to solve all the mazes and save the Princess. You move by pressing W, A, S, D." +
				"\n\nThe mazes are patrolled by dozens, if not hundreds, of evil monsters. You can destroy them by using your" +
				" gifted power, the ability to throw fireballs (press Spacebar). However, doing so exhausts your Mana so " +
				"you should only use it when necessary. Destroying monsters will earn you experience points that will help" +
				" level up your skills (i.e. speed, health, and attack). Your Mana bar, Health bar , and Experience bar are" +
				" located on the bottom of the screen. There are Mana and Health potions that you can pick up in the maze that" +
				" will give you a boost (press Q and E to activate). \n" +
				"\n\nThere are also various other items left by previous brave heroes that you can find in the maze to assist" +
				" you on accomplishing this quest. Some items that you find will be added to your inventory for future use." +
				" A more detailed explanation of each item can be found in our item breakdown. You can also hover over items" +
				" to see a quick tool tip. Your inventory is shown in the bottom left corner and the items can be used by" +
				" clicking on the image or pressing the hotkey shown on the top right corner of the image. There will be doors" +
				" that will block your way and you must find the golden keys to open them. Remember there will be a trail of" +
				" permanently lit candles to help guide your way to the exit! Once you successfully complete 15 levels you will" +
				" have saved the princess! If you wish to take a break in the middle but do not want to lose your progress, you" +
				" can save your game by pressing N and load a previous saved game by pressing M. The game will also be automatically" +
				" saved when you close it. Sound can be disabled by pressing J. You will start at the beginning of the level you" +
				" were at. I wish you good luck on your quest!" +
				"\n\nSpecial Items" +
				"\nThere are various items in this game to help you reach the objective. These items are scattered randomly" +
				" in the maze and are picked up or activated upon contact. Certain items will give you a temporary boost in" +
				" some aspect while others can be saved and used in the future. The following are the items in the game." + 
				"\n\nHealth Potion" +
				"\nThis increases your Health by a certain amount. This item can be saved and used when needed. This can be " +
				"activated by pressing Q." +
				"\n\nMana Potion" +
				"\nThis increases your Mana by a certain amount. This item can be saved and used when needed. This can be activated" +
				" by pressing E." +
				"\n\nSpeed Booster" +
				"\nThis increases your speed for a certain amount of time. The time for this item does not stack up when you pick " +
				"up a new one before the current one runs out." +  
				"\n\nSight Booster " +
				"\nThis increases your sight for a certain amount of time. The time for this item does not stack up when you pick " +
				"up a new one before the current one runs out." +  
				"\n\nCandle" +
				"\nThis illuminates the area around it permanently. It is found only on the correct path." +
				"\n\nTime Freeze" +
				"\nThis freezes the monsters for a certain amount of time. The time for this item does not stack up when you" +
				" pick up a new one before the current one runs out." +  
				"\n\nKey" +
				"\nThis is used to unlock doors. This item can be saved and used when needed. Keys found in a level can only be" +
				" used to open the doors in that level. There are always the same number of keys as doors. Keys are usually found" +
				" in locations that are not on the correct route." + 
				"\n\nObstacle"+
				"\nThis is used to create a temporary obstacle that will last for about 60 seconds. You can walk through it" +
				" but the monsters are blocked. It can be destroyed by fireballs and monsters as well. This item can be saved" +
				" and used when needed. This can be activated by pressing B."+
				"\n\nDoor" +
				"\nThese are found on the correct path and it requires the key to open it. After acquiring the key, clicking" +
				" on the key icon while facing a door will unlock the door." + 
				"\n\nPortal" +
				"\nThis is a skill that will be unlocked as you level up. You can deploy two portals, one orange and one blue," +
				" and use them to travel from one place to another instantaneously. It requires 75 Mana to place them. You can" +
				" deploy them by pressing Y and T.\n"
				);
		add(scroll, BorderLayout.NORTH);
		add(back, BorderLayout.SOUTH);
		back.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(back)) rootMenu.show("HOME SCREEN");
	}
}