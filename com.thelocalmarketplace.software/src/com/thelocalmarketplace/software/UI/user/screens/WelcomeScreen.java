package com.thelocalmarketplace.software.UI.user.screens;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.JButton;

import com.thelocalmarketplace.software.Software;

public class WelcomeScreen extends AbstractUserScreen {

	private static final long serialVersionUID = 7502139702763571623L;
	
	public WelcomeScreen(int machineID) {
		super(machineID);
		
		redraw();
	}
	
	public void onPressStart(ActionEvent e) {
		Software.getInstance().startNewSession(machineID);
	}

	@Override
	public void redraw() {
		setLayout(new GridLayout(1,1));
		JButton startButton = new JButton("Touch anywhere to start!");
		startButton.setAlignmentX(CENTER_ALIGNMENT);
		startButton.addActionListener(this::onPressStart);
		add(startButton);
		
		revalidate();
		repaint();
	}
	
}
