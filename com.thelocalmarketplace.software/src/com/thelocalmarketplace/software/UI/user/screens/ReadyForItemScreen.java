package com.thelocalmarketplace.software.UI.user.screens;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.PriceLookUpCode;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.Software;
import com.thelocalmarketplace.software.membership.MembershipDatabase;
import com.thelocalmarketplace.software.session.UIHandler;

public class ReadyForItemScreen extends AbstractMainScreen {

	private static final long serialVersionUID = 6147707410164322045L;
	
	private JLabel pluView;
	private JLabel memberNumber;
	
	private char state = 0;
	
	public ReadyForItemScreen(int machineID) {
		super(machineID, true);
		statusbar.setNormalStatus();
		
		memberNumber = new JLabel();
		memberNumber.setBackground(Color.WHITE);
		memberNumber.setPreferredSize(new Dimension(100, 50));

		pluView = new JLabel();
		pluView.setBackground(Color.WHITE);
		pluView.setPreferredSize(new Dimension(100, 50));
		
		redraw();
	}
	public void drawAddMemberNumber() {
		JPanel keyArea = new JPanel();
		keyArea.setLayout(new BoxLayout(keyArea, BoxLayout.Y_AXIS));
		
		keyArea.add(memberNumber);
		
		JPanel keypad = new JPanel();
		keypad.setLayout(new GridLayout(0, 3));
		
		for(int i = 1; i <= 9; i++) {
			JButton numeral = new JButton(i + "");
			numeral.addActionListener(this::addNumeralMembership);
			
			keypad.add(numeral);
		}
		JButton backspace = new JButton("<");
		backspace.addActionListener(this::removeNumeralMembership);
		keypad.add(backspace);
		
		JButton numeral = new JButton("0");
		numeral.addActionListener(this::addNumeralMembership);
		keypad.add(numeral);
		
		JButton submit = new JButton(">");
		submit.addActionListener(this::submitMembership);
		keypad.add(submit);
		
		keyArea.add(keypad);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.NORTH;
		
		add(keyArea, gbc);
		
		JButton backButton = new JButton("Back");
		backButton.addActionListener(this::onSelectBack);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.SOUTHWEST;
		
		add(backButton, gbc);
	}

	public void drawManualEntry() {
		JPanel catalog = new JPanel();
		for(PLUCodedProduct product : ProductDatabases.PLU_PRODUCT_DATABASE.values()) {
			JButton button = new JButton("<html><center>" + product.getDescription().replaceAll(" ", "<br>") + "</center></html>");
			button.addActionListener((e) -> selectFromCatalog(product));
			button.setPreferredSize(new Dimension(100, 100));
			catalog.add(button);
		}
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.NORTH;
		
		add(catalog, gbc);
		
		JPanel keyArea = new JPanel();
		keyArea.setLayout(new BoxLayout(keyArea, BoxLayout.Y_AXIS));
		
		keyArea.add(pluView);
		
		JPanel keypad = new JPanel();
		keypad.setLayout(new GridLayout(0, 3));
		
		for(int i = 1; i <= 9; i++) {
			JButton numeral = new JButton(i + "");
			numeral.addActionListener(this::addNumeral);
			
			keypad.add(numeral);
		}
		JButton backspace = new JButton("<");
		backspace.addActionListener(this::removeNumeral);
		keypad.add(backspace);
		
		JButton numeral = new JButton("0");
		numeral.addActionListener(this::addNumeral);
		keypad.add(numeral);
		
		JButton submit = new JButton(">");
		submit.addActionListener(this::enterFromPLU);
		keypad.add(submit);
		
		keyArea.add(keypad);

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 0.3;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.NORTH;
		
		add(keyArea, gbc);
		
		JButton backButton = new JButton("Back");
		backButton.addActionListener(this::onSelectBack);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.SOUTHWEST;
		
		add(backButton, gbc);
	}

	@Override
	public void redraw() {
		removeAll();
		
		if(state == 0) {
			drawMain();
		}
		else if(state == 1) {
			drawManualEntry();
		}
		else if(state == 2) {
			drawAddMemberNumber();
		}
		
		revalidate();
		repaint();
	}
	
	private void addBags(ActionEvent e) {
		UIHandler handler = Software.getInstance().getCurrentSession(machineID).getUIHandler();
		handler.addBagSelected();
	}
	
	private void purchaseBags(ActionEvent e) {
		UIHandler handler = Software.getInstance().getCurrentSession(machineID).getUIHandler();
		handler.purchasingBagsSelected();
	}
	
	private void onSelectBack(ActionEvent e) {
		state = 0;
		redraw();
	} 
	
	private void selectFromCatalog(PLUCodedProduct product) {
		UIHandler handler = Software.getInstance().getCurrentSession(machineID).getUIHandler();
		handler.addFromPLU(product.getPLUCode());
	}
	
	private void enterFromPLU(ActionEvent e) {
		String current = pluView.getText();
		pluView.setText("");
		try {
			PriceLookUpCode code = new PriceLookUpCode(current);
			UIHandler handler = Software.getInstance().getCurrentSession(machineID).getUIHandler();
			handler.addFromPLU(code);
		} catch(Exception e1) {}
	}
	
	private void submitMembership(ActionEvent e) {
		String current = memberNumber.getText();
		memberNumber.setText("");
		long number = Long.parseLong(current);
		boolean isMember = MembershipDatabase.getInstance().validateMembership(number);
		if(!isMember) return;
		Software.getInstance().getCurrentSession(machineID).getTransaction().setTransactionMembershipID(number);
		state = 0;
		redraw();
	}

	private void addNumeralMembership(ActionEvent e) {
		String current = memberNumber.getText();
		current += ((JButton) e.getSource()).getText();
		memberNumber.setText(current);
	}
	
	private void removeNumeralMembership(ActionEvent e) {
		String current = memberNumber.getText();
		current = current.substring(0, current.length() - 1);
		memberNumber.setText(current);
	}
	private void addNumeral(ActionEvent e) {
		String current = pluView.getText();
		if(current.length() >= 4) return;
		current += ((JButton) e.getSource()).getText();
		pluView.setText(current);
	}
	
	private void removeNumeral(ActionEvent e) {
		String current = pluView.getText();
		if(current.length() <= 0) return;
		current = current.substring(0, current.length() - 1);
		pluView.setText(current);
	}
	
	private void removeSelected(ActionEvent e) {
		view.deleteSelected();
	}

	@Override
	public Component[] getActionButtons() {
		// TODO Auto-generated method stub
		JButton addBagsButton = new JButton("Add Own Bags");
		addBagsButton.addActionListener(this::addBags);
		JButton purchaseBagsButton = new JButton("Purchase Bags");
		purchaseBagsButton.addActionListener(this::purchaseBags);
		JButton removeSelectedButton = new JButton("Remove Selected");
		removeSelectedButton.addActionListener(this::removeSelected);

		JButton addMemberNumber = new JButton("Add Membership Number");
		addMemberNumber.addActionListener(this::addMemberNumberButton);
		
		Component[] btns = new Component[] {
				addBagsButton, purchaseBagsButton, addMemberNumber, removeSelectedButton
		};
		
		long memId = Software.getInstance().getCurrentSession(machineID).getTransaction().getTransactionMembershipID();
		
		if(memId != -1) {
			btns[2] = new JLabel("Membership: " + MembershipDatabase.getInstance().getMemberName(memId));
		}
		
		return btns;
	}

	@Override
	public void onSelectAddItemManually(ActionEvent e) {
		state = 1;
		redraw();
	}
	
	public void addMemberNumberButton(ActionEvent e) {
		state = 2;
		redraw();
	}
}
