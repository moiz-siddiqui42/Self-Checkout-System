package com.thelocalmarketplace.software.UI.user.screens;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.jjjwelectronics.Mass;
import com.thelocalmarketplace.software.Software;
import com.thelocalmarketplace.software.UI.components.TransactionView;
import com.thelocalmarketplace.software.payment.IPayment;
import com.thelocalmarketplace.software.payment.Transaction;
import com.thelocalmarketplace.software.payment.TransactionItem;
import com.thelocalmarketplace.software.payment.TransactionObserver;
import com.thelocalmarketplace.software.state.UserSessionState;

public class ReadyForPaymentScreen extends AbstractUserScreen implements TransactionObserver {
	private static final long serialVersionUID = -3858453260247291799L;
	
	private TransactionView view;
	
	private JLabel remainingBalance;
	
	public ReadyForPaymentScreen(int machineID) {
		super(machineID);
		setLayout(new GridBagLayout());
		
		remainingBalance = new JLabel();
		remainingBalance.setFont(new Font("regular", Font.BOLD, 32));
		
		view = new TransactionView(machineID);
		Transaction transaction = Software.getInstance().getCurrentSession(machineID).getTransaction();
		view.connect(transaction);
		transaction.register(this);
		
		updateRemainingBalance();
		redraw();
	}

	@Override
	public void redraw() {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.NORTH;
		JScrollPane pane = new JScrollPane(view);
		add(pane, gbc);
		
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc2 = new GridBagConstraints();
		gbc2.gridx = 0;
		gbc2.gridy = 0;
		gbc2.weightx = 1;
		gbc2.weighty = 1;
		gbc2.anchor = GridBagConstraints.SOUTH;
		infoPanel.add(new JLabel("Remaining Balance:"), gbc2);
		
		gbc2.gridx = 0;
		gbc2.gridy = 1;
		gbc2.weightx = 1;
		gbc2.weighty = 0;
		gbc2.anchor = GridBagConstraints.CENTER;
		infoPanel.add(remainingBalance, gbc2);
		
		gbc2.gridx = 0;
		gbc2.gridy = 2;
		gbc2.weightx = 1;
		gbc2.weighty = 1;
		gbc2.anchor = GridBagConstraints.NORTH;
		infoPanel.add(new JLabel("Insert Cash or use PinPad to pay."), gbc2);
		
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.NORTH;
		add(infoPanel, gbc);
		
		JButton backButton = new JButton("Back");
		backButton.addActionListener(this::back);
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		add(backButton, gbc);
	}
	
	
	/**
	 * Accessor for the attendant station mainly
	 * @return transaction view of the checkoutstation
	 */
	public TransactionView getView() {
		return this.view; 
	}
	
	
	
	
	
	@Override
	public void onScreenRemoved() {
		Transaction transaction = Software.getInstance().getCurrentSession(machineID).getTransaction();
		view.disconnect(transaction);
		transaction.deregister(this);
	}
	
	public void updateRemainingBalance() {
		Transaction transaction = Software.getInstance().getCurrentSession(machineID).getTransaction();
		String price = transaction.getTotalCost().toPlainString();
		this.remainingBalance.setText("$" + price);
	}

	@Override
	public void itemAdded(TransactionItem product) {
		updateRemainingBalance();
	}

	@Override
	public void itemRemoved(TransactionItem product) {
		updateRemainingBalance();
	}

	@Override
	public void paymentAdded(IPayment payment) {
		updateRemainingBalance();
	}

	@Override
	public void bagAdded(Mass bagMass) {
		updateRemainingBalance();
	}
	
	private void back(ActionEvent e) {
		Software.getInstance().getCurrentSession(machineID).setState(UserSessionState.READY_FOR_ITEM);
	}
}
