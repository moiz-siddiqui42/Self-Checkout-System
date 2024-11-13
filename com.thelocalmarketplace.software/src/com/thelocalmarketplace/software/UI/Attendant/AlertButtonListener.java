package com.thelocalmarketplace.software.UI.Attendant;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class AlertButtonListener implements ActionListener {

	public AlertButtonListener() {}

	@Override
	public void actionPerformed(ActionEvent e) {
		// when the button is pressed display what the error is 
		//TODO set up so that the string displayed is different depending on the error 
		JFrame errorFrame = new JFrame("Exception");
		errorFrame.setLayout(new FlowLayout());
		errorFrame.setSize(150,100);
		JLabel errorLabel = new JLabel("No errors so far.");
		errorFrame.add(errorLabel);
		
		JButton closeButton = new JButton("Ok");
		closeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				errorFrame.dispose(); 
				
			}
		});		
		errorFrame.add(closeButton);
		errorFrame.setVisible(true);
	}

}
