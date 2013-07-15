package game.inventory;

import game.MazeGame;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.Serializable;

/** a character's inventory */
public class Inventory implements Serializable {
	private int x = 0;
	private int y = 0;
	private AbstractInventoryItem[] items = new AbstractInventoryItem[4];
	
	private static final Color TEXT_COLOR = new Color(200, 200, 200);

	public static final int BUFF_GAP = 4;
	public static final int HEALTH = 0;
	public static final int MANA = 1;
	public static final int KEY = 2;
	public static final int OBSTACLE = 3;
	public static final int NUM_ITEMS = 4;

	public Inventory(MazeGame maze){
		items[HEALTH] = new InventoryHealthPotion(maze);
		items[MANA] = new InventoryManaPotion(maze);
		items[KEY] = new InventoryKey(maze);
		items[OBSTACLE] = new InventoryObstacle(maze);
		for (AbstractInventoryItem item : items){
			maze.mouseObjects.add(item);
		}
		updateItemCoordinates();
	}

	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
		updateItemCoordinates();
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
		updateItemCoordinates();
	}
	private void updateItemCoordinates(){
		for (int i = 0; i < items.length; i++){
			items[i].setX(x + i * (AbstractInventoryItem.WIDTH + BUFF_GAP));
			items[i].setY(y);
		}
	}

	public BufferedImage getImage(){
		BufferedImage image = new BufferedImage(
				items.length * (AbstractInventoryItem.WIDTH + BUFF_GAP) - BUFF_GAP,
				AbstractInventoryItem.HEIGHT,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();

		g.setColor(Color.BLACK);
		FontMetrics metrics = g.getFontMetrics();
		for (AbstractInventoryItem item : items){
			g.drawImage(item.getImage(), item.getX() - x, item.getY() - y, null);
			String quantity = Integer.toString(item.getQuantity());
			MazeGame.outlineText(g, quantity, item.getX() - x + 1, item.getY() - y + item.getHeight() - 1, TEXT_COLOR, Color.BLACK);
			int keyCode = item.getKeyCode();
			if (keyCode != -1){
				String keyString = KeyEvent.getKeyText(keyCode);
				MazeGame.outlineText(g, keyString, item.getX() - x + item.getWidth() - metrics.stringWidth(keyString) - 1, item.getY() - y + metrics.getAscent(), TEXT_COLOR, Color.BLACK);
			}
		}
		return image;
	}
	
	public AbstractInventoryItem getItem(int index){
		return items[index];
	}
}
