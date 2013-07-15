package game.buff;

import game.AbstractCountdownObject;
import game.CountdownListener;
import game.MazeGame;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.Serializable;

public class BuffManager implements CountdownListener, Serializable{
	private MazeGame maze;
	private int x = 0;
	private int y = 0;
	private AbstractBuff[] buffs = new AbstractBuff[3];
	
	private static final float[] scaleFactors = {1.0F, 1.0F, 1.0F, 0.6F};
	private static final float[] offsets = {0.0F, 0.0F, 0.0F, 0.0F};
	private static final RescaleOp rescaleOp = new RescaleOp(scaleFactors, offsets, null);
	
	private static final int BUFF_GAP = 3;
	private static final int SIGHT = 0;
	private static final int SPEED = 1;
	private static final int TIME_FREEZE = 2;
	
	public BuffManager(MazeGame maze){
		this.maze = maze;
		buffs[SIGHT] = new SightBuff(maze);
		buffs[SPEED] = new SpeedBuff(maze);
		buffs[TIME_FREEZE] = new TimeFreezeBuff(maze);
		for (AbstractBuff b : buffs){
			b.setCountdownListener(this);
			maze.mouseObjects.add(b);
		}
	}
	
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	private void updateBuffCoordinates(){
		int nActive = 0;
		for (AbstractBuff b : buffs){
			if (b.hasTimeLeft()){
				b.setX(x + nActive * (AbstractBuff.WIDTH + BUFF_GAP));
				nActive++;
			} else{
				b.setX(-AbstractBuff.WIDTH);
			}
		}
	}
	public void buffChanged(AbstractBuff source){
		updateBuffCoordinates();
		maze.updateToolTipObject();
	}
	
	public BufferedImage getImage(){
		int nActive = 0;
		for (AbstractBuff b : buffs){
			if (b.hasTimeLeft()){
				nActive++;
			}
		}
		BufferedImage image = new BufferedImage(Math.max(1, nActive * (AbstractBuff.WIDTH + BUFF_GAP) - BUFF_GAP), AbstractBuff.HEIGHT, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		
		for (AbstractBuff b : buffs){
			if (b.hasTimeLeft()){
				g.drawImage(b.getImage(), b.getX() - x, b.getY() - y, null);
			}
		}
		return rescaleOp.filter(image, image);
	}
	
	public void setSightAmount(int amount){
		((SightBuff)buffs[SIGHT]).setAmount(amount);
	}
	public void setSpeedAmount(int amount){
		((SpeedBuff)buffs[SPEED]).setAmount(amount);
	}
	public void setSightDuration(int duration){
		buffs[SIGHT].setDuration(duration);
	}
	public void setSpeedDuration(int duration){
		buffs[SPEED].setDuration(duration);
	}
	public void setTimeFreezeDuration(int duration){
		buffs[TIME_FREEZE].setDuration(duration);
	}

	@Override
	public void statusChanged(AbstractCountdownObject obj) {
		updateBuffCoordinates();
	}
}
