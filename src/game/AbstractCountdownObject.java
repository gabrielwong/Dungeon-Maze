package game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;

public abstract class AbstractCountdownObject implements ClickableObject, Serializable {
	private int x = 0;
	private int y = 0;
	private long startTime = System.currentTimeMillis();
	private int duration = 1;
	private transient Thread delayThread;
	private CountdownListener listener;
	protected boolean hasTimeLeft = false;
	// Whether the countdown indicator is fully shaded when the time is set
	protected boolean isFullAtStart = false;

	private int keyCode = -1;
	
	private static final Color maskColor = new Color(0, 0, 0, 150);

	public static final int WIDTH = 32;
	public static final int HEIGHT = 32;
	private static final int CLIP_ARC_SIZE = 5;

	@Override
	public int getX() {
		return x;
	}
	public void setX(int x){
		this.x = x;
	}
	@Override
	public int getY() {
		return y;
	}
	public void setY(int y){
		this.y = y;
	}
	@Override
	public int getWidth() {
		return WIDTH;
	}
	@Override
	public int getHeight() {
		return HEIGHT;
	}
	public void setDuration(final int duration){
		this.duration = duration;
		if (delayThread != null)
			delayThread.interrupt();
		if (duration > 0){
			if  (!hasTimeLeft){
				setHasTimeLeft(true);
			}
			hasTimeLeft = true;
			final AbstractCountdownObject obj = this;
			// This thread just sets the buff as inactive after the required time
			delayThread = new Thread(new Runnable(){
				public void run(){
					try {
						Thread.sleep(duration);
					} catch (InterruptedException e) {
						return;
					}
					setHasTimeLeft(false);
					hasTimeLeft = false;
					if (listener != null)
						listener.statusChanged(obj);
				}
			});
			delayThread.start();
			startTime = System.currentTimeMillis();
		} else{
			if (hasTimeLeft){
				setHasTimeLeft(false);
			}
			hasTimeLeft = false;
		}
		if (listener != null)
			listener.statusChanged(this);
	}
	public int getDuration(){
		return duration;
	}
	public int getTimeLeft(){
		return Math.max(0, (int)((startTime + duration) - System.currentTimeMillis()));
	}
	public boolean hasTimeLeft(){
		return hasTimeLeft;
	}
	public BufferedImage getImage(double percentage){
		Image base = getBaseImage();
		BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		int height = image.getHeight() - (int)(image.getHeight() * percentage);
		Graphics2D g = image.createGraphics();
		g.setClip(new RoundRectangle2D.Float(0, 0, image.getWidth(), image.getHeight(), CLIP_ARC_SIZE, CLIP_ARC_SIZE));
		
		// draw background
		g.setColor(Color.WHITE);
		g.fillRoundRect(0, 0, image.getWidth() - 1, image.getHeight() - 1, CLIP_ARC_SIZE, CLIP_ARC_SIZE);
		
		// draw icon
		if (base.getWidth(null) > WIDTH || base.getHeight(null) > HEIGHT){
			int x, y, w, h;
			int bW = base.getWidth(null);
			int bH = base.getHeight(null);
			if (base.getWidth(null) > base.getHeight(null)){
				x = 0;
				w = WIDTH;
				h = bW * HEIGHT / WIDTH;
				y = (HEIGHT - h) / 2;
			} else{
				y = 0;
				h = HEIGHT;
				w = bH * WIDTH / HEIGHT;
				x = (WIDTH - w) / 2;
			}
			g.drawImage(base, x, y, w, h, null);
		} else{
			g.drawImage(base, (WIDTH - base.getWidth(null)) / 2, (HEIGHT - base.getHeight(null)) / 2, null);
		}
		
		// Draw cooldown indicator
		g.setColor(maskColor);
		g.fillRect(0, image.getHeight() - height, image.getWidth(), height);
		
		// Draw outline
		g.setColor(Color.BLACK);
		g.drawRoundRect(0, 0, image.getWidth() - 1, image.getHeight() - 1, CLIP_ARC_SIZE, CLIP_ARC_SIZE);
		return image;

	}
	public BufferedImage getImage(){
		double percentage = isFullAtStart ? (double)(duration - getTimeLeft()) / duration :
			(double)getTimeLeft() / duration;
		return getImage(percentage);
	}
	public void setCountdownListener(CountdownListener listener){
		this.listener = listener;
	}
	public void setDrawDirection(boolean isFullAtStart){
		this.isFullAtStart = isFullAtStart;
	}
	
	public int getKeyCode(){
		return keyCode;
	}
	public void setKeyCode(int keyCode){
		this.keyCode = keyCode;
	}
	protected abstract void setHasTimeLeft(boolean hasTimeLeft);
	/**
	 * Get the image of the buff withouth the time left indicator.
	 * @return the image of the buff
	 */
	protected abstract Image getBaseImage();
}
