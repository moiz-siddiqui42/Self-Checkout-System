package com.thelocalmarketplace.software.UI.hardwaresim;

/**
 * SENG 300 Project - Group 1:
 * 
 * Avery Keuben - 30170731
 * Moiz Siddiqui - 30150291
 * Ammaar Melethil - 30141956
 * Joey Fisher - 30105628
 * Ethan Pangilinan - 30179143
 * Joshua Kraft - 30171525
 * Nathan Vaters - 30121908
 * Max Butcher - 30149202
 * Neeraj Ghansela - 30157473
 * Ansel Sulejmani - 30178521
 * Suleman Basit - 30132816
 * Jacob Boyden - 30193220
 * Cheshta Sharma - 30064538
 * Callum Bates - 30188601
 * Armughan Mustafa - 30154601
 * Connor Ell - 30073291
 * Saif Farag - 30195046
 * Ivan Agalakov - 30172107
 * Samuel Turner - 10064857
 * Stephanie Sevilla - 30176781
 * Winston Wang - 30185321
 */

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import com.jjjwelectronics.card.Card;
import com.thelocalmarketplace.software.UI.components.ErrorPopup;
import com.thelocalmarketplace.software.payment.BankDataBase;

public class CardTab extends AbstractHardwareSimTab {
	
	private static final long serialVersionUID = 1881179373704340614L;
	
	private static List<CardTab> instances = new ArrayList<CardTab>();

	JComboBox<String> cardKindField; 
	JTextField cardNumberField; 
	JTextField cardholderField; 
	JTextField ccvField; 
	JTextField pinField; 
	JCheckBox tapEnabledField; 
	JCheckBox hasChipField; 

	public CardTab(int machineId) {
		super(machineId, 1);
		
		instances.add(this);
		
		JPanel cardInputPanel = new JPanel();
		cardInputPanel.setBorder(new TitledBorder("Card Details"));
		cardInputPanel.setLayout(new GridLayout(0, 2));
		cardKindField = new JComboBox<String>();
		updateCardKind();
		cardNumberField = new JTextField(10);
		cardholderField = new JTextField(10);
		ccvField = new JTextField(3);
		pinField = new JTextField(4);
		tapEnabledField = new JCheckBox("Tap Enabled");
		hasChipField = new JCheckBox("Has Chip");
		
		cardInputPanel.add(new JLabel("Card Kind:"));
		cardInputPanel.add(cardKindField);
		cardInputPanel.add(new JLabel("Card Number:"));
		cardInputPanel.add(cardNumberField);
		cardInputPanel.add(new JLabel("Cardholder:"));
		cardInputPanel.add(cardholderField);
		cardInputPanel.add(new JLabel("CCV:"));
		cardInputPanel.add(ccvField);
		cardInputPanel.add(new JLabel("Pin:"));
		cardInputPanel.add(pinField);
		cardInputPanel.add(tapEnabledField);
		cardInputPanel.add(hasChipField);
		
		add(cardInputPanel);
		
		JPanel cardPayPanel = new JPanel();
		cardPayPanel.setBorder(new TitledBorder("Pay"));
		JButton swipeButton = new JButton("Swipe");
		swipeButton.addActionListener(this::swipe);
		JButton insertButton = new JButton("Insert");
		insertButton.addActionListener(this::insert);
		JButton tapButton = new JButton("Tap");
		tapButton.addActionListener(this::tap);
		cardPayPanel.add(swipeButton);
		cardPayPanel.add(insertButton);
		cardPayPanel.add(tapButton);
		
		add(cardPayPanel);
	}
	
	public static void updateCardKind() {
		for(CardTab tab : instances) {
			tab.cardKindField.removeAllItems();
			for(String issuer : BankDataBase.getInstance().getDataBase().keySet()) {
				tab.cardKindField.addItem(issuer);
			}
		}
	}
	
	private Card getEnteredCard() {
		String kind = (String) cardKindField.getSelectedItem();
		kind = kind.toLowerCase();
		String cardholder = cardholderField.getText();
		String number = cardNumberField.getText();
		String ccv = ccvField.getText();
		String pin = pinField.getText();
		boolean tap = tapEnabledField.isSelected();
		boolean chip = hasChipField.isSelected();
		Card card = new Card(kind, number, cardholder, ccv, pin, tap, chip);
		return card;
	}
	
	private void swipe(ActionEvent e) {
		Card card = getEnteredCard();
		try {
			getHardware().getCardReader().swipe(card);
		} catch (IOException | RuntimeException e1) {
			ErrorPopup.showError("Failed to swipe card", e1.getStackTrace().toString());
		}
	}
	
	private void insert(ActionEvent e) {
		Card card = getEnteredCard();
		String pin = JOptionPane.showInputDialog("Enter Pin");
		try {
			getHardware().getCardReader().insert(card, pin);
		} catch (IOException | RuntimeException e1) {
			StringBuilder sb = new StringBuilder();
			for(StackTraceElement elem : e1.getStackTrace()) {
				sb.append(elem.toString());
				sb.append("\n");
			}
			ErrorPopup.showError("Failed to insert card", sb.toString());
		} finally {
			getHardware().getCardReader().remove();
		}
	}
	
	private void tap(ActionEvent e) {
		Card card = getEnteredCard();
		try {
			getHardware().getCardReader().tap(card);
		} catch (IOException | RuntimeException e1) {
			ErrorPopup.showError("Failed to tap card", e1.getStackTrace().toString());
		}
	}
}