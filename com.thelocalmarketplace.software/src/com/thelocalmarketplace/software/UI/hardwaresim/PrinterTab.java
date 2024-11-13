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

import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JLabel;

import com.jjjwelectronics.OverloadedDevice;
import com.thelocalmarketplace.software.UI.components.ErrorPopup;

public class PrinterTab extends AbstractHardwareSimTab {

	private static final long serialVersionUID = -3729078427461109594L;

	JLabel inkRemainingLabel, paperRemainingLabel;
	
	public PrinterTab(int machineId) {
		super(machineId, 2);
		
		inkRemainingLabel = new JLabel("<loading>");
		paperRemainingLabel = new JLabel("<loading>");
		
		add(new JLabel("Ink Remaining: "));
		add(inkRemainingLabel);
		add(new JLabel("Paper Remaining: "));
		add(paperRemainingLabel);
		
		JButton refillInkBtn = new JButton("Refill Ink (100 units)");
		JButton refillPaperBtn = new JButton("Refill Paper (100 units)");
		
		refillInkBtn.addActionListener(this::refillInk);
		refillPaperBtn.addActionListener(this::refillPaper);
		
		add(refillInkBtn);
		add(refillPaperBtn);
		
		refreshUI();
	}

	
	void refillInk(ActionEvent e) {
		try {
			getHardware().getPrinter().addInk(100);
		} catch (OverloadedDevice e1) {
			ErrorPopup.showError("Failed to refill ink", "The ink is already full!");
		}
		
		refreshUI();
	}
	
	void refillPaper(ActionEvent e) {
		try {
			getHardware().getPrinter().addPaper(100);
		} catch (OverloadedDevice e1) {
			ErrorPopup.showError("Failed to refill paper", "The paper is already full!");
		}
		
		refreshUI();
	}
	
	void refreshUI() {
		try {
			inkRemainingLabel.setText("" + getHardware().getPrinter().inkRemaining());
		} catch(UnsupportedOperationException e) {
			inkRemainingLabel.setText("unsupported");
		}

		try {
			paperRemainingLabel.setText("" + getHardware().getPrinter().paperRemaining());
		} catch(UnsupportedOperationException e) {
			paperRemainingLabel.setText("unsupported");
		}
	}
}
