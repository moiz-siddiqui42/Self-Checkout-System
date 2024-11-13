package com.thelocalmarketplace.software.UI.Attendant;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.thelocalmarketplace.software.Software;
import com.thelocalmarketplace.software.SoftwareObserver;
import com.thelocalmarketplace.software.UI.components.TransactionView;
import com.thelocalmarketplace.software.session.SessionObserver;
import com.thelocalmarketplace.software.session.UserSession;
import com.thelocalmarketplace.software.state.UserSessionState;
/*
 * TODO: 
 * Add functionality for the assist button 
 * Add alert button functionality
 * Add functionality for adding item through text 
 *  
 */



public class SelfCheckoutComponent extends JPanel implements SoftwareObserver, SessionObserver {
	// gonna have a bidirectional communication channel with the self checkout station 
	// add a transaction viewer as a wrapped component as a JList
	public TransactionView transactionViewer;
	 
	private JPanel statusField;
	private JPanel closeBox;  
	private JPanel assistButtonPanel; 
	private JButton assistButton; 
	private int machineID; 
	
	private JButton toggleDisabled;
	
	private boolean notificationSent = false;
	
	private UserSessionState state = null; 
	
	// attendant's view of a self checkout machine
	public SelfCheckoutComponent(int newmachineID) {
		this.machineID = newmachineID; 
		// register the listener 
		Software.getInstance().register(machineID, this);
		
		// gridbaglayout will be used
		setLayout(new GridBagLayout());
		setBackground(Color.GRAY);
		
		GridBagConstraints c = new GridBagConstraints();
	
		
		// add the status field
		statusField = new JPanel();
		statusField.setLayout(new GridLayout(1,1));
		JLabel label = new JLabel("Station: " + this.machineID);
		label.setSize(300, 90);
		statusField.setBackground(Color.GRAY);
		label.setFont(new Font("regular", Font.BOLD, 22));
		statusField.add(label);
		statusField.setPreferredSize(label.getSize());
		c = new GridBagConstraints(); 
		c.gridx = 1; 
		c.gridy = 0; 
		c.weightx = 1; 
		c.weighty = 0; 
		c.anchor = GridBagConstraints.CENTER;
		add(statusField, c);
		
		// add the close checkbox, when checked the machine is disabled
		closeBox = new JPanel();
		closeBox.setLayout(new GridLayout(1,1));
		toggleDisabled = new JButton("Disable");
		toggleDisabled.setSize(75,50);
		closeBox.setPreferredSize(toggleDisabled.getSize());
		closeBox.setBackground(Color.GRAY);
		toggleDisabled.addActionListener((e) -> {
			if(Software.getInstance().getStationEnabledState(newmachineID)) {
				if(!Software.getInstance().disableStation(newmachineID)) {
					toggleDisabled.setText("Disable Queued");
					toggleDisabled.revalidate();
					toggleDisabled.repaint();
				}
			} else {
				Software.getInstance().enableStation(newmachineID);
			}
		});
		
		
		closeBox.add(toggleDisabled); 
		c = new GridBagConstraints();
		c.gridx = 2; 
		c.gridy = 0; 
		c.weightx = 0;
		c.anchor = GridBagConstraints.NORTH; 
		add(closeBox, c);

		
		
		transactionViewer = new TransactionView(machineID);// need to connect the transaction to a session after it's started
		transactionViewer.setBorder(BorderFactory.createEmptyBorder(0,0 ,100,0));
		JScrollPane scroll = new JScrollPane(transactionViewer);
		c = new GridBagConstraints();
		c.gridx = 1; 
		c.gridy = 1; 
		c.weightx = 1; 
		c.weighty  = 1; 
		c.gridheight = 2; 
		c.fill = GridBagConstraints.BOTH; 
		add(scroll, c);
		
		// add the assist button
		assistButtonPanel = new JPanel();
		assistButtonPanel.setLayout(new GridLayout(1,1));
		
		assistButton = new JButton("Assist");
		
		assistButton.setSize(75, 50);
		assistButtonPanel.setPreferredSize(assistButton.getSize());
		assistButtonPanel.setBackground(Color.GRAY);
		assistButtonPanel.add(assistButton);
		
		c = new GridBagConstraints();
		c.gridy = 2; 
		c.gridx = 2; 
		c.weightx = 0.1; 
		c.weighty = 0.1; 
		c.anchor = GridBagConstraints.SOUTHWEST; 
		add(assistButtonPanel, c);
		setVisible(true);	
	}
	
	
	
	
	@Override
	public void onStateChanged(UserSessionState newState) {
		if(newState.equals(UserSessionState.WAITING_FOR_ATTENDANT) && !notificationSent) {
			notificationSent = true;
			// if waiting for the attendant alert that the station is waiting
			JFrame alertFrame = new JFrame("Alert!");
			alertFrame.setSize(300, 200);
			alertFrame.getContentPane().setLayout(new GridLayout(2,1));
			alertFrame.getContentPane().add(new JLabel("Station " + machineID + " needs assistance."));
			
			JButton alertButton = new JButton("Resolved");
			alertButton.setSize(100, 50);
			alertButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					notificationSent = false;
					// set the state of the machine to be ready for item state by default 
					state = UserSessionState.READY_FOR_ITEM; 
					Software.getInstance().getCurrentSession(machineID).setState(UserSessionState.READY_FOR_ITEM);
					// then need to delete the alert frame 
					alertFrame.dispose();
				}
				
			});
			alertFrame.getContentPane().add(alertButton); 
			alertFrame.setVisible(true);
			
		}else {
			this.state = newState; 
		}
		
		
		
	}

	@Override
	public void onSessionStart() {
		UserSession currentSession = Software.getInstance().getCurrentSession(machineID); 
		// remember that this is also a listener
		currentSession.register(this);
		transactionViewer.connect(Software.getInstance().getCurrentSession(machineID).getTransaction());
		
		// we also want to set the state to be whatever the state current session is in
		state = currentSession.getState(); 
	}

	@Override
	public void onSessionEnd() {
		state = null; 
	}
	
	
	public JButton getAssistButton() {
		return this.assistButton; 
	}
	
	@Override
	public void onMachineDisabled() {
		toggleDisabled.setText("Enable");
		toggleDisabled.revalidate();
		toggleDisabled.repaint();
	}

	@Override
	public void onMachineEnabled() {
		toggleDisabled.setText("Disable");
		toggleDisabled.revalidate();
		toggleDisabled.repaint();
	}
}
