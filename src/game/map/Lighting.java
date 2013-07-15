package game.map;

public interface Lighting {
	public int getCenterX();
	public int getCenterY();
	public boolean isOn();
	public int getIntensity(); //unit?
	public float getRadius();
}
