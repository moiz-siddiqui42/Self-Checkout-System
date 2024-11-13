package com.thelocalmarketplace.software.UI.user.screens;

import javax.swing.JLabel;

public class PrintingScreen extends AbstractUserScreen {

	private static final long serialVersionUID = -1343678162947891819L;

	public PrintingScreen(int machineID) {
		super(machineID);
		redraw();
	}

	@Override
	public void redraw() {
		add(new JLabel("Printing...."));
	}

}
