package com.thelocalmarketplace.software.UI.user.screens;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;

import javax.swing.JButton;

import com.thelocalmarketplace.software.Software;
import com.thelocalmarketplace.software.session.UIHandler;

public class WaitingForBaggingScreen extends AbstractMainScreen {

	private static final long serialVersionUID = 6147707410164322045L;
	
	public WaitingForBaggingScreen(int machineID) {
		super(machineID, false);

		setLayout(new GridBagLayout());

		statusbar.setInfoStatus("Place item in the bagging area.");

		redraw();
	}

	@Override
	public void redraw() {
		removeAll();
		
		drawMain();
		
		revalidate();
		repaint();
	}
	
	@Override
	public void onScreenRemoved() {
		view.disconnect(Software.getInstance().getCurrentSession(machineID).getTransaction());
	}

	@Override
	public JButton[] getActionButtons() {
		// TODO Auto-generated method stub
		JButton skipBaggingButton = new JButton("Skip Bagging");
		skipBaggingButton.addActionListener(this::skipBagging);
		return new JButton[] {
				skipBaggingButton
		};
	}

	@Override
	public void onSelectAddItemManually(ActionEvent e) {}
	
	public void skipBagging(ActionEvent e) {
		UIHandler handler = Software.getInstance().getCurrentSession(machineID).getUIHandler();
		handler.skipBaggingSelected();
	}
}
