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

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import com.thelocalmarketplace.hardware.external.CardIssuer;
import com.thelocalmarketplace.software.UI.components.ErrorPopup;
import com.thelocalmarketplace.software.UI.components.WrappedJComponent;
import com.thelocalmarketplace.software.UI.hardwaresim.components.CardIssuerTabDetails;
import com.thelocalmarketplace.software.payment.BankDataBase;

public class CardIssuerTab2 extends AbstractAttendantTab {

	private static final long serialVersionUID = -5221044028265491112L;
	
	private JList<String> cardIssuerList;
	private DefaultListModel<String> cardIssuerModel;
	private JTabbedPane tabs;
	private WrappedJComponent<JTextField> issuer;
	private WrappedJComponent<JTextField> maxHolds;
	private WrappedJComponent<JButton> addBtn;
	private WrappedJComponent<JButton> removeBtn;

	public CardIssuerTab2() {
		super(1);
		
		tabs = new JTabbedPane();
		
		cardIssuerModel = new DefaultListModel<String>();
		cardIssuerList = new JList<String>(cardIssuerModel);
		
		issuer = WrappedJComponent.create(JTextField.class, Integer.TYPE, 20);
		maxHolds = WrappedJComponent.create(JTextField.class, Integer.TYPE, 20);
		addBtn = WrappedJComponent.create(JButton.class, String.class, "Add Card Issuer");
		addBtn.getComponent().addActionListener(this::handleAddIssuer);
		removeBtn = WrappedJComponent.create(JButton.class, String.class, "Remove Selected");
		removeBtn.getComponent().addActionListener(this::handleRemoveIssuer);
		recreateTabs();
		
		add(tabs);
	}
	
	private void recreateTabs() {
		tabs.removeAll();

		JPanel issuerPanel = new JPanel();
		issuerPanel.setLayout(new GridLayout(0, 2, 20, 20));
		issuerPanel.add(cardIssuerList);
		
		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
		controlPanel.setBorder(new TitledBorder("Create Issuer"));
		JPanel createPanel = new JPanel();
		createPanel.setLayout(new GridLayout(0, 2, 20, 20));
		createPanel.add(new JLabel("Issuer:"));
		createPanel.add(issuer);
		createPanel.add(new JLabel("Max Holds:"));
		createPanel.add(maxHolds);
		controlPanel.add(createPanel);
		controlPanel.add(addBtn);
		controlPanel.add(removeBtn);
		
		issuerPanel.add(controlPanel);
		
		tabs.add("Card Issuers", issuerPanel);
		
		// Add tab for each card issuer
		for(int i = 0; i < cardIssuerModel.getSize(); i++) {
			tabs.add(cardIssuerModel.get(i), new CardIssuerTabDetails(cardIssuerModel.get(i)));
		}
	}
	
	private void handleAddIssuer(ActionEvent e) {
		String name = this.issuer.getComponent().getText().toLowerCase();
		if(name.trim().equals("")) {
			ErrorPopup.showError("Invalid Issuer", "The issuer name cannot be blank");
			return;
		}
		int maxHolds;
		try {
			maxHolds = Integer.parseInt(this.maxHolds.getComponent().getText());
		} catch(NumberFormatException e1) {
			ErrorPopup.showError("Invalid Issuer", "The maximum number of holds must be a number");
			return;
		}
		
		CardIssuer issuer = new CardIssuer(name, maxHolds);
		
		BankDataBase.getInstance().getDataBase().put(name, issuer);
		cardIssuerModel.addElement(name);
		recreateTabs();
		CardTab.updateCardKind();
	}
	
	private void handleRemoveIssuer(ActionEvent e) {
		String selected = cardIssuerList.getSelectedValue();
		CardIssuer issuer = BankDataBase.getInstance().getDataBase().get(selected);
		
		if(issuer == null) {
			ErrorPopup.showError("Invalid Issuer", "Please select a card issuer to remove.");
			return;
		}
		
		BankDataBase.getInstance().getDataBase().remove(selected);
		CardIssuerTabDetails.onRemove(selected);
		cardIssuerModel.removeElement(selected);
		recreateTabs();
		CardTab.updateCardKind();
	}

}
