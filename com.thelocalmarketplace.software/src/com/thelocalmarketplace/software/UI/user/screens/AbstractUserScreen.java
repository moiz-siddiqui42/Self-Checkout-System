package com.thelocalmarketplace.software.UI.user.screens;

import javax.swing.JPanel;

public abstract class AbstractUserScreen extends JPanel {

	private static final long serialVersionUID = 322611724600810476L;

	protected int machineID;
	
	public AbstractUserScreen(int machineID) {
		this.machineID = machineID;
	}
	
	
	public abstract void redraw();
	
	/**
	 * Called when the screen is removed from the display
	 */
	public void onScreenRemoved() {};
}
