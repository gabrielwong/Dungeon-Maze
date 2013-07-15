package game.map;

import java.awt.Image;
import java.awt.Rectangle;
import java.io.Serializable;

/** essentially an object with an x,y,width, and height */
public abstract class MapObject implements Serializable {
	protected int x; // Coordinate in maze in pixels
	protected int y;

	protected static int DEFAULT_X = 0;
	protected static int DEFAULT_Y = 0;
	
	public static final String resPath = "/res/", audPath = resPath + "aud/", imgPath = resPath + "img/", mapPath = imgPath + "map/",
			objPath = imgPath + "obj/", charPath = imgPath + "character/";

	public MapObject() {
		this(DEFAULT_X, DEFAULT_Y);
	}

	public MapObject(int x, int y) {
		this.x = x;
		this.y = y;
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

	public int getCenterX() {
		return x + getWidth() / 2;
	}

	public int getCenterY() {
		return y + getHeight() / 2;
	}

	public void setCenterX(int x){
		this.x = x - getWidth()/2;
	}
	public void setCenterY(int y){
		this.y = y - getHeight()/2;
	}
	/**
	 * Returns whether an object is intersected with this object.
	 * 
	 * @param obj
	 * @return
	 */
	public boolean intersects(MapObject obj) {
		// These are the regions occupied by the objects
		Rectangle thatObject = new Rectangle(obj.getX(), obj.getY(), obj.getWidth(), obj.getHeight());

		return intersects(thatObject);
	}

	public boolean intersects(Rectangle r) {
		Rectangle thisObject = getBounds();

		return thisObject.intersects(r);
	}

	public boolean contains(int x, int y) {
		Rectangle thisObject = getBounds();
		return thisObject.contains(x, y);
	}
	public Rectangle getBounds(){
		return new Rectangle(getX(), getY(), getWidth(), getHeight());
	}
	public Rectangle getBounds(int border){
		return new Rectangle(getX() - border, getY() - border, getWidth() + border * 2, getHeight() + border * 2);
	}
	public abstract int getWidth();

	public abstract int getHeight();

	public abstract Image getImage();
}
