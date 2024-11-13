package com.thelocalmarketplace.software.UI.Attendant;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.thelocalmarketplace.software.Software;
import com.thelocalmarketplace.software.UI.components.TransactionView;

public class AssistButtonListener implements ActionListener {
	private int machineID; 
	private AttendantUI ui; 
	private AddItemPanel addItemPanel;
	// get the attendant ui and the machineID to indicate which machine called the action method 
	// this way we can bring up the proper transaction viewer
	public AssistButtonListener(AttendantUI ui, int machineID) {
		this.ui = ui; 
		this.machineID = machineID; 
		// get the id and the ui
	}
	
	
	// want to create a new screen for the attendant UI
	@Override
	public void actionPerformed(ActionEvent e) {
		JButton source = (JButton)e.getSource(); 
		// check source of the event, will always be a button
		if(source.getText().equals("Assist")) {
		// clear ui screen
		ui.getFrame().getContentPane().removeAll(); 
		ui.getFrame().getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints(); 
		
		// begin adding transaction viewer(don't have access to that yet)
		TransactionView view = new TransactionView(machineID);
		if(Software.getInstance().getCurrentSession(machineID) != null) {
			view.connect(Software.getInstance().getCurrentSession(machineID).getTransaction());
		}
		c.gridx = 0; 
		c.gridy = 0; 
		c.weightx = 1; 
		c.weighty = 1;
		c.gridheight = 1; 
		c.fill = GridBagConstraints.BOTH; 
		ui.getFrame().getContentPane().add(view, c);
		
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.BOTH;
		addItemPanel = new AddItemPanel(machineID, ui.getFrame().getContentPane());
		ui.getFrame().getContentPane().add(addItemPanel, c);
		
		// add a button that can be used to set the screen back to what it was before
		JPanel buttonPanel = new JPanel(); 
		JButton button = new JButton("Close");
		button.setSize(75,50);
		buttonPanel.setPreferredSize(button.getSize());
		button.addActionListener(new AssistButtonListener(ui, machineID));
		buttonPanel.add(button); 
		c = new GridBagConstraints(); 
		c.gridx = 0; 
		c.gridy = 1; 
		c.weighty = 0.8;
		c.weightx = 0.8; 
		c.anchor = GridBagConstraints.SOUTHEAST; 
		ui.getFrame().getContentPane().add(button, c); 
		ui.getFrame().validate(); 
		
		}
		else {
			ui.redraw(); 
		}
	}

}
