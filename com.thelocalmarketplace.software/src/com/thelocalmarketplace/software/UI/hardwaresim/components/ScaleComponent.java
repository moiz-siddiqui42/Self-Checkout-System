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

import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import com.jjjwelectronics.Item;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.scale.AbstractElectronicScale;
import com.jjjwelectronics.scale.IElectronicScale;
import com.thelocalmarketplace.software.UI.components.ErrorPopup;
import com.thelocalmarketplace.software.UI.components.WrappedJComponent;

public class ScaleComponent extends JPanel {

	private static final long serialVersionUID = 2409413101383782924L;

	private static class WeightedItem extends Item {

		protected WeightedItem(Mass mass) {
			super(mass);
		}
		
		@Override
		public String toString() {
			return getMass().inGrams().toPlainString() + "g";
		}
		
	}
	
	JLabel currentWeightLabel;
	WrappedJComponent<JTextField> input;
	DefaultListModel<WeightedItem> currentItemsOnScale = new DefaultListModel<WeightedItem>();
	JList<WeightedItem> itemList;
	
	private IElectronicScale scale;

	public ScaleComponent(IElectronicScale scale, String name) {
		this.scale = scale;
		
		setBorder(new TitledBorder(name));
		setLayout(new GridLayout(0, 2, 20, 20));
		
		add(new JLabel("Current Weight:"));
		currentWeightLabel = new JLabel("0g");
		add(currentWeightLabel);
		
		itemList = new JList<WeightedItem>(currentItemsOnScale);
		
		JScrollPane itemListScrollPane = new JScrollPane(itemList);
		
		add(itemListScrollPane);
		
		WrappedJComponent<JButton> removeBtn = WrappedJComponent.create(JButton.class, String.class, "Remove Selected");
		removeBtn.getComponent().addActionListener(this::removeSelected);
		add(removeBtn);
		
		input = new WrappedJComponent<JTextField>(JTextField.class, new Object[] {10}, new Class<?>[] {Integer.TYPE});
		add(input);
		WrappedJComponent<JButton> addWeightBtn = WrappedJComponent.create(JButton.class, String.class, "Add");
		addWeightBtn.getComponent().addActionListener(this::addWeight);
		
		add(addWeightBtn);
		
		updateCurrentWeight();
	}
	
	private void addWeight(ActionEvent e) {
		
		float weightInGrams;
		try {
			weightInGrams = Float.parseFloat(input.getComponent().getText());
		} catch(NumberFormatException e1) {
			ErrorPopup.showError("Invalid Weight", "The weight " + input.getComponent().getText() + " is not a valid weight.");
			return;
		}
		WeightedItem item = new WeightedItem(new Mass(weightInGrams)) {};
		scale.addAnItem(item);
		currentItemsOnScale.add(currentItemsOnScale.getSize(), item);
		updateCurrentWeight();
	}
	
	private void updateCurrentWeight() {
		AbstractElectronicScale scale = (AbstractElectronicScale) this.scale;
		try {
			currentWeightLabel.setText(scale.getCurrentMassOnTheScale().inGrams().toPlainString() + "g");
		} catch (OverloadedDevice e) {
			currentWeightLabel.setText("overloaded!");
			e.printStackTrace();
		}
	}
	
	private void removeSelected(ActionEvent e) {
		if(itemList.getSelectedIndex() == -1) {
			ErrorPopup.showError("Failed to remove item", "No item is currently selected");
			return;
		}
		WeightedItem item = currentItemsOnScale.remove(itemList.getSelectedIndex());
		scale.removeAnItem(item);
		updateCurrentWeight();
	}
}
