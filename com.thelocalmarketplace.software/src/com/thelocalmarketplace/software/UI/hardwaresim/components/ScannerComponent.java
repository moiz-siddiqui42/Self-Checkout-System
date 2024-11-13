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

import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.jjjwelectronics.scanner.IBarcodeScanner;
import com.thelocalmarketplace.software.UI.components.ErrorPopup;
import com.thelocalmarketplace.software.UI.components.WrappedJComponent;

import ca.ucalgary.seng300.simulation.SimulationException;

public class ScannerComponent extends JPanel {

	private static final long serialVersionUID = -2927130360212530663L;

	IBarcodeScanner scanner;
	JTextField input;
	
	public ScannerComponent(IBarcodeScanner scanner, String name) {
		this.scanner = scanner;

		input = new JTextField(10);
		WrappedJComponent<JButton> button = new WrappedJComponent<JButton>(JButton.class, new Object[] {"Scan"}, new Class<?>[] {String.class});
		button.getComponent().addActionListener(this::scan);

		setBorder(new TitledBorder(name));
		add(input);
		add(button);
	}

	private void scan(ActionEvent e) {
		String barcode = input.getText();
		Numeral[] digits = new Numeral[barcode.length()];
		for(int i = 0; i < barcode.length(); i++) {
			try {
				digits[i] = Numeral.valueOf(Byte.parseByte("" + barcode.charAt(i)));
			} catch(NumberFormatException e1) {
				ErrorPopup.showError("Invalid Barcode", "The barcode " + barcode + " is not a valid barcode.");
				return;
			}
		}
		try {
			Barcode bc = new Barcode(digits);
			scanner.scan(new BarcodedItem(bc, Mass.ONE_GRAM));
		} catch(SimulationException e1) {
			ErrorPopup.showError("Invalid Barcode", "The barcode must be at least one digit long!");
		}
	}
}
