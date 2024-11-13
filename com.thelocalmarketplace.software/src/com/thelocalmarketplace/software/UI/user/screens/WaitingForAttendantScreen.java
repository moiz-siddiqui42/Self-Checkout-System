package com.thelocalmarketplace.software.UI.user.screens;

import java.awt.event.ActionEvent;

import javax.swing.JButton;

public class WaitingForAttendantScreen extends AbstractMainScreen {
	private static final long serialVersionUID = 6147707410164322045L;

	public WaitingForAttendantScreen(int machineID) {
		super(machineID, false);
		statusbar.setErrorStatus("Please wait for assistance");
		
		redraw();
	}

	@Override
	public JButton[] getActionButtons() {
		// TODO Auto-generated method stub
		return new JButton[0];
	}

	@Override
	public void redraw() {
		removeAll();
		
		drawMain();
		
		revalidate();
		repaint();
	}

	@Override
	public void onSelectAddItemManually(ActionEvent e) {
	}
}
