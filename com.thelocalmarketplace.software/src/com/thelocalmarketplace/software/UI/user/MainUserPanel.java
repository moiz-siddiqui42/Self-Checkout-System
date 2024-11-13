package com.thelocalmarketplace.software.UI.user;

import java.awt.GridLayout;

import javax.swing.JPanel;

import com.thelocalmarketplace.software.Software;
import com.thelocalmarketplace.software.SoftwareObserver;
import com.thelocalmarketplace.software.UI.user.screens.AbstractUserScreen;
import com.thelocalmarketplace.software.UI.user.screens.AddingBagsScreen;
import com.thelocalmarketplace.software.UI.user.screens.DisabledScreen;
import com.thelocalmarketplace.software.UI.user.screens.PrintingScreen;
import com.thelocalmarketplace.software.UI.user.screens.ReadyForItemScreen;
import com.thelocalmarketplace.software.UI.user.screens.ReadyForPaymentScreen;
import com.thelocalmarketplace.software.UI.user.screens.SystemErrorScreen;
import com.thelocalmarketplace.software.UI.user.screens.WaitingForAttendantScreen;
import com.thelocalmarketplace.software.UI.user.screens.WaitingForBaggingScreen;
import com.thelocalmarketplace.software.UI.user.screens.WelcomeScreen;
import com.thelocalmarketplace.software.session.SessionObserver;
import com.thelocalmarketplace.software.session.UserSession;
import com.thelocalmarketplace.software.state.UserSessionState;

public class MainUserPanel extends JPanel implements SessionObserver, SoftwareObserver {
	
	private boolean disabled = false;
	private UserSessionState state = null;
	private int machineID;

	private static final long serialVersionUID = -7881208850880351803L;
	
	private AbstractUserScreen currentScreen;

	public MainUserPanel(int machineID) {
		setLayout(new GridLayout(1,1));
		this.machineID = machineID;
		Software.getInstance().register(machineID, this);
		
		redraw();
	}
	
	private void redraw() {
		if(currentScreen != null) currentScreen.onScreenRemoved();
		currentScreen = null;
		
		removeAll();
		
		if(this.state == null) {
			if(disabled) {
				currentScreen = new DisabledScreen(machineID);
			} else {
				currentScreen = new WelcomeScreen(machineID);
			}
			add(currentScreen);
			
			revalidate();
			repaint();
			return;
		}
		
		switch(this.state) {
			case PRINTER_NEEDS_REFILL:
				currentScreen = new SystemErrorScreen(machineID);
				break;
			case PRINT_RECEIPT:
				currentScreen = new PrintingScreen(machineID);
				break;
			case READY_FOR_ITEM:
				currentScreen = new ReadyForItemScreen(machineID);
				break;
			case READY_FOR_PAYMENT:
				currentScreen = new ReadyForPaymentScreen(machineID);
				break;
			case WAITING_FOR_ATTENDANT:
				currentScreen = new WaitingForAttendantScreen(machineID);
				break;
			case WAITING_FOR_BAGGING:
				currentScreen = new WaitingForBaggingScreen(machineID);
				break;
			case ADDING_BAGS_STATE:
				currentScreen = new AddingBagsScreen(machineID);
			default:
				break;
		}
		
		if(currentScreen != null) add(currentScreen);
		
		revalidate();
		repaint();
	}

	@Override
	public void onStateChanged(UserSessionState newState) {
		this.state = newState;
		redraw();
	}

	@Override
	public void onSessionStart() {
		UserSession session = Software.getInstance().getCurrentSession(this.machineID);
		session.register(this);
		this.state = session.getState();
		redraw();
	}

	@Override
	public void onSessionEnd() {
		state = null;
		redraw();
	}

	@Override
	public void onMachineDisabled() {
		disabled = true;
		redraw();
	}

	@Override
	public void onMachineEnabled() {
		disabled = false;
		redraw();
	}
	
}
