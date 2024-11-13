package com.thelocalmarketplace.software.UI.user.screens;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.jjjwelectronics.Mass;
import com.thelocalmarketplace.software.Software;
import com.thelocalmarketplace.software.UI.components.TransactionView;
import com.thelocalmarketplace.software.UI.user.components.StatusBarComponent;
import com.thelocalmarketplace.software.payment.IPayment;
import com.thelocalmarketplace.software.payment.Transaction;
import com.thelocalmarketplace.software.payment.TransactionItem;
import com.thelocalmarketplace.software.payment.TransactionObserver;
import com.thelocalmarketplace.software.state.UserSessionState;

public abstract class AbstractMainScreen extends AbstractUserScreen implements TransactionObserver {
	private static final long serialVersionUID = -3858453260247291799L;
	
	protected TransactionView view;
	protected StatusBarComponent statusbar;
	
	protected JButton payButton;
	
	public AbstractMainScreen(int machineID, boolean canPay) {
		super(machineID);
		view = new TransactionView(machineID);
		statusbar = new StatusBarComponent(this::onSelectAddItemManually);
		statusbar.setNormalStatus();
		
		payButton = new JButton("Pay Now $0.00");
		payButton.addActionListener(this::paySelected);
		payButton.setEnabled(canPay);

		setLayout(new GridBagLayout());

		Transaction transaction = Software.getInstance().getCurrentSession(machineID).getTransaction();
		view.connect(transaction);
		transaction.register(this);
		redrawPayButton();
	}
	
	public final void drawMain() {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.NORTH;
		JScrollPane pane = new JScrollPane(view);
		add(pane, gbc);
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.SOUTH;
		add(statusbar, gbc);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(0, 2));
		
		JButton helpButton = new JButton("Help");
		helpButton.addActionListener(this::getHelp);
		buttonPanel.add(helpButton);
		
		Component[] actionButtons = getActionButtons();
		for(Component button : actionButtons) {
			buttonPanel.add(button);
		}
		
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		add(buttonPanel, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.EAST;
		add(payButton, gbc);
	}
	
	public abstract Component[] getActionButtons();
	
	public abstract void onSelectAddItemManually(ActionEvent e);
	
	@Override
	public void onScreenRemoved() {
		Transaction transaction = Software.getInstance().getCurrentSession(machineID).getTransaction();
		view.disconnect(transaction);
		transaction.deregister(this);
		
	}
	
	private final void getHelp(ActionEvent e) {
		Software.getInstance().getCurrentSession(machineID).setState(UserSessionState.WAITING_FOR_ATTENDANT);
	}
	
	private void paySelected(ActionEvent e) {
		Software.getInstance().getCurrentSession(machineID).setState(UserSessionState.READY_FOR_PAYMENT);
	}
	
	public void redrawPayButton() {
		Transaction transaction = Software.getInstance().getCurrentSession(machineID).getTransaction();
		String price = transaction.getTotalCost().toPlainString();
		
		payButton.setText("Pay Now $" + price);
		revalidate();
		repaint();
	}
	
	@Override
	public void itemAdded(TransactionItem product) {
		redrawPayButton();
	}
	
	@Override
	public void itemRemoved(TransactionItem product) {
		redrawPayButton();
	}
	
	@Override
	public void paymentAdded(IPayment payment) {
		redrawPayButton();
	}
	
	@Override
	public void bagAdded(Mass bagMass) {
	}
}
