package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;

public abstract class AbstractBar implements ClickableObject {		// used to create the health and mana bars
	private int x = 0;
	private int y = 0;
	private int width = 0;
	private int height = MazeGame.BAR_HEIGHT;
	private LinearGradientPaint paint;
	private static final float[] fractions = { 0.0F, 0.3F, 0.5F, 0.8F };
	
	public AbstractBar(){
		paint = createGradient();
	}
	@Override
	public void actionPerformed(InputEvent evt) {}
	@Override
	public int getKeyCode() {
		return -1;
	}
	@Override
	public int getWidth() {
		return width;
	}
	public void setWidth(int width){
		this.width = width;
	}
	@Override
	public int getHeight() {
		paint = createGradient();
		return height;
	}
	public void setHeight(int height){
		this.height = height;
	}
	@Override
	public int getX(){
		return x;
	}
	@Override
	public int getY(){
		return y;
	}
	public void setX(int x){
		this.x = x;
	}
	public void setY(int y){
		this.y = y;
	}
	
	private LinearGradientPaint createGradient(){		// creates gradient effect
		return new LinearGradientPaint(0, 0, 0, height, fractions, getBarColors());
	}
	public BufferedImage getImage(){
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		int outlineWidth = 2;
		g.setColor(Color.BLACK);
		g.fillRoundRect(0, 0, width, height, 7, 7);

		// Exp bar
		g.setPaint(paint);
		g.fillRoundRect(outlineWidth, outlineWidth, (width - 2 * outlineWidth) * getValue() / getMaxValue(), height - 2 * outlineWidth, 3, 3);


		// Draw bar text
		g.setFont(new Font("segoe ui", Font.BOLD, 14));
		FontMetrics metrics = g.getFontMetrics();
		g.setColor(Color.BLACK);
		MazeGame.outlineText(g, getBarText(), (width - metrics.stringWidth(getBarText())) / 2,
				height / 3 + metrics.getHeight() / 2, Color.WHITE, Color.BLACK);
		
		return image;
	}
	public abstract String getBarText();
	protected abstract Color[] getBarColors();
	protected abstract int getValue();
	protected abstract int getMaxValue();
}
