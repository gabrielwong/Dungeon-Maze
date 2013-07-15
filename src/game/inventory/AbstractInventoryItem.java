package game.inventory;

import game.AbstractCountdownObject;
import game.MazeGame;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.Serializable;

/** A representation of an inventory item (like potions) */
public abstract class AbstractInventoryItem extends AbstractCountdownObject implements Serializable{
	protected int quantity = 0;
	protected int cooldown = 1000;
	
	public AbstractInventoryItem(){
		setDrawDirection(true);
	}
	
	@Override
	public void actionPerformed(InputEvent evt){
		if (quantity > 0 && !hasTimeLeft()){
			quantity--;
			setDuration(cooldown);
		}
	}
	
	public int getQuantity(){
		return quantity;
	}
	public void setQuantity(int quantity){
		this.quantity = quantity;
	}
	public void incrementQuantity(){
		quantity++;
	}
	public int getCooldown(){
		return cooldown;
	}
	public void setCooldown(int cooldown){
		this.cooldown = cooldown;
	}
	public String[] getToolTipText(){
		String[] s = {getBaseText(),
				"Quantity: " + quantity,
				"Cooldown: " + cooldown + " ms",
				"Hotkey: " + (getKeyCode() == - 1 ? "None" : KeyEvent.getKeyText(getKeyCode()))};
		return s;
	}
	protected abstract String getBaseText();
}
