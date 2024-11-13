package com.thelocalmarketplace.software.UI.Attendant;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.thelocalmarketplace.software.Software;
import com.thelocalmarketplace.software.UI.components.WrappedJComponent;

/**
 * Class will hold the simulation for the self checkout machine
 */

public class AttendantUI {

	// main frame of the ui will be the touchscreen from the hardware
	private JFrame frame; 
	private SelfCheckoutComponent[] componentList; 
	
	
	// initialize stuff and add listeners
	public AttendantUI() {
		// now have access to the touchscreen of the attendant station
		this.frame = Software.getInstance().getAttendantStation().screen.getFrame();
		this.frame.getContentPane().setLayout(new GridLayout(0,2));
		this.frame.setSize(950, 600); // for now this is fine but will need to change later
		
		componentList  = new SelfCheckoutComponent[Software.getInstance().getAttendantStation().supervisedStationCount()];
		// add self checkout components 
		for(int i = 0; i < componentList.length ; i++) {
			componentList[i] = new SelfCheckoutComponent(i);
			componentList[i].getAssistButton().addActionListener(new AssistButtonListener(this, i));
			componentList[i].setBorder(BorderFactory.createLineBorder(Color.BLACK));
			this.frame.getContentPane().add(componentList[i]);
			
		}
		// set it visible the safe way
		Software.getInstance().getAttendantStation().screen.setVisible(true);
		
		
		
	}
	
	public void redraw() {
		this.frame.getContentPane().removeAll(); 
		this.frame.getContentPane().setLayout(new GridLayout(0,2));
		componentList  = new SelfCheckoutComponent[Software.getInstance().getAttendantStation().supervisedStationCount()];
		// add self checkout components 
		for(int i = 0; i < componentList.length ; i++) {
			componentList[i] = new SelfCheckoutComponent(i);
			componentList[i].getAssistButton().addActionListener(new AssistButtonListener(this, i));
			componentList[i].setBorder(BorderFactory.createLineBorder(Color.BLACK));
			if(Software.getInstance().getCurrentSession(i) != null) {
				componentList[i].transactionViewer.connect(Software.getInstance().getCurrentSession(i).getTransaction());	
			}
			this.frame.getContentPane().add(componentList[i]);
			this.frame.getContentPane().revalidate();
		}
		this.frame.getContentPane().repaint(); 
		
	}
	
	public JFrame getFrame() {
		return this.frame; 
	}
	
	public SelfCheckoutComponent[] getComponentList() {
		return componentList; 
	}

}
