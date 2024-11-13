package com.thelocalmarketplace.software.UI.user.screens;

import java.awt.GridBagLayout;

import javax.swing.JLabel;

public class SystemErrorScreen extends AbstractUserScreen {

	private static final long serialVersionUID = -3729847003509778643L;

	public SystemErrorScreen(int machineID) {
		super(machineID);
		setLayout(new GridBagLayout());
		redraw();
	}
	
	@Override
	public void redraw() {
		add(new JLabel("System Error: Out of ink/paper"));
	}

}
