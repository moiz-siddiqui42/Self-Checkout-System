package com.thelocalmarketplace.software.UI.user.screens;

import java.awt.GridBagLayout;

import javax.swing.JLabel;

public class DisabledScreen extends AbstractUserScreen {

	private static final long serialVersionUID = -7828286725519674696L;

	public DisabledScreen(int machineID) {
		super(machineID);
		setLayout(new GridBagLayout());
		redraw();
	}

	@Override
	public void redraw() {
		add(new JLabel("Lane Closed"));
	}

}
