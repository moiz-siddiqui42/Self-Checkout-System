package com.thelocalmarketplace.software.UI.user.screens;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JLabel;

import com.thelocalmarketplace.software.Software;

public class AddingBagsScreen extends AbstractUserScreen {

	private static final long serialVersionUID = 7490499816820805571L;

	public AddingBagsScreen(int machineID) {
		super(machineID);
		
		setLayout(new GridBagLayout());
		redraw();
	}

	@Override
	public void redraw() {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.SOUTH;
		add(new JLabel("Place your bags in the bagging area."), gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 0;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.NORTH;
		JButton doneButton = new JButton("Done");
		doneButton.addActionListener(this::doneButtonPressed);
		add(doneButton, gbc);
	}
	
	public void doneButtonPressed(ActionEvent e) {
		Software.getInstance().getCurrentSession(machineID).getUIHandler().doneAddingBagsSelected();
	}

}
