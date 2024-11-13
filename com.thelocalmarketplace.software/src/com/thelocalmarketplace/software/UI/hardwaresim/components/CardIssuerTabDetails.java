package com.thelocalmarketplace.software.UI.hardwaresim.components;

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

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.border.TitledBorder;

import com.thelocalmarketplace.software.UI.components.ErrorPopup;
import com.thelocalmarketplace.software.UI.components.WrappedJComponent;
import com.thelocalmarketplace.software.UI.hardwaresim.AbstractAttendantTab;
import com.thelocalmarketplace.software.payment.BankDataBase;

public class CardIssuerTabDetails extends AbstractAttendantTab implements ListCellRenderer<CardIssuerTabDetails.CardData> {
	
	private static Map<String, List<CardData>> existingCardData = new HashMap<>();
	
	private static final long serialVersionUID = -8877306545492648380L;
	
	public static class CardData {
		String number;
		String cardholder;
		Calendar expiry;
		String cvv;
		double balance;
	};
	
	JList<CardData> cardList;
	DefaultListModel<CardData> cardModel;
	
	WrappedJComponent<JTextField> number;
	WrappedJComponent<JTextField> cardholder;
	WrappedJComponent<JTextField> expiryYear;
	WrappedJComponent<JTextField> expiryMonth;
	WrappedJComponent<JTextField> expiryDay;
	WrappedJComponent<JTextField> cvv;
	WrappedJComponent<JTextField> balance;
	
	private String cardIssuer;

	public CardIssuerTabDetails(String cardIssuer) {
		super(2);
		
		if(!existingCardData.containsKey(cardIssuer)) {
			existingCardData.put(cardIssuer, new ArrayList<CardIssuerTabDetails.CardData>());
		}
		
		this.cardIssuer = cardIssuer;
		
		cardModel = new DefaultListModel<CardData>();
		cardModel.addAll(existingCardData.get(cardIssuer));
		cardList = new JList<CardData>(cardModel);
		cardList.setCellRenderer(this);
		
		add(cardList);
		
		number = WrappedJComponent.create(JTextField.class, Integer.TYPE, 20);
		cardholder = WrappedJComponent.create(JTextField.class, Integer.TYPE, 20);
		expiryYear = WrappedJComponent.create(JTextField.class, Integer.TYPE, 20);
		expiryMonth = WrappedJComponent.create(JTextField.class, Integer.TYPE, 20);
		expiryDay = WrappedJComponent.create(JTextField.class, Integer.TYPE, 20);
		cvv = WrappedJComponent.create(JTextField.class, Integer.TYPE, 20);
		balance = WrappedJComponent.create(JTextField.class, Integer.TYPE, 20);

		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
		controlPanel.setBorder(new TitledBorder("Add Card"));
		JPanel createPanel = new JPanel();
		createPanel.setLayout(new GridLayout(0, 2, 20, 20));
		
		createPanel.add(new JLabel("Number"));
		createPanel.add(number);
		createPanel.add(new JLabel("Cardholder"));
		createPanel.add(cardholder);
		createPanel.add(new JLabel("Expiry (Year)"));
		createPanel.add(expiryYear);
		createPanel.add(new JLabel("Expiry (Month)"));
		createPanel.add(expiryMonth);
		createPanel.add(new JLabel("Expiry (Day)"));
		createPanel.add(expiryDay);
		createPanel.add(new JLabel("CVV"));
		createPanel.add(cvv);
		createPanel.add(new JLabel("Balance"));
		createPanel.add(balance);
		
		controlPanel.add(createPanel);
		JButton addCardBtn = new JButton("Add Card");
		addCardBtn.addActionListener(this::handleAddCard);
		
		controlPanel.add(addCardBtn);
		
		add(controlPanel);
	}
	
	@Override
	public Component getListCellRendererComponent(JList<? extends CardData> list, CardData value, int index, boolean isSelected,
			boolean cellHasFocus) {
		JPanel panel = new JPanel();
		if(isSelected) {
			panel.setBackground(list.getSelectionBackground());
			panel.setForeground(list.getSelectionForeground());
		} else {
			panel.setBackground(list.getBackground());
			panel.setForeground(list.getForeground());
		}
		panel.setLayout(new GridLayout(1, 0));
		panel.add(new JLabel(value.number));
		panel.add(new JLabel(value.cardholder));
		panel.add(new JLabel(new SimpleDateFormat("yyyy-MM-dd").format(value.expiry.getTime())));
		panel.add(new JLabel(value.cvv));
		panel.add(new JLabel("$" + value.balance));
		return panel;
	}
	
	private void handleAddCard(ActionEvent e) {
		CardData data = new CardData();
		try {
			int year = Integer.parseInt(this.expiryYear.getComponent().getText());
			int month = Integer.parseInt(this.expiryMonth.getComponent().getText());
			int day = Integer.parseInt(this.expiryDay.getComponent().getText());
			data.expiry = new Calendar.Builder().setDate(year, month, day).build();
			data.balance = Double.parseDouble(this.balance.getComponent().getText());
		} catch(NumberFormatException e1) {
			ErrorPopup.showError("Invalid Card", "The expiry date or balance is not valid.");
			return;
		}
		
		data.number = this.number.getComponent().getText();
		data.cardholder = this.cardholder.getComponent().getText();
		data.cvv = this.cvv.getComponent().getText();
		
		try {
			BankDataBase.getInstance().getDataBase().get(this.cardIssuer).addCardData(data.number, data.cardholder, data.expiry, data.cvv, data.balance);
		} catch(Exception e1) {
			ErrorPopup.showError("Invalid Card", e1.getMessage());
			return;
		}
		cardModel.addElement(data);
		existingCardData.get(this.cardIssuer).add(data);
	}
	
	public static void onRemove(String cardIssuer) {
		existingCardData.remove(cardIssuer);
	}
}
