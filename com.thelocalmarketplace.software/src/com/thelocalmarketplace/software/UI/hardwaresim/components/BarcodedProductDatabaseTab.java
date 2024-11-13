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

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.UI.components.ErrorPopup;
import com.thelocalmarketplace.software.UI.components.WrappedJComponent;

import ca.ucalgary.seng300.simulation.SimulationException;

public class BarcodedProductDatabaseTab extends AbstractProductDatabaseTab<BarcodedProduct> {

	private static final long serialVersionUID = 9124183042212708387L;
	
	private WrappedJComponent<JTextField> description;
	private WrappedJComponent<JTextField> barcode;
	private WrappedJComponent<JTextField> price;
	private WrappedJComponent<JTextField> expectedWeight;
	
	public BarcodedProductDatabaseTab() {
		super(ProductDatabases.BARCODED_PRODUCT_DATABASE.values());
	}

	@Override
	protected Component[] createControlPanel() {
		description = WrappedJComponent.create(JTextField.class, Integer.TYPE, 20);
		price = WrappedJComponent.create(JTextField.class, Integer.TYPE, 20);
		expectedWeight = WrappedJComponent.create(JTextField.class, Integer.TYPE, 20);
		barcode = WrappedJComponent.create(JTextField.class, Integer.TYPE, 20);
		
		return new Component[] {
				new JLabel("Description:"),
				description,
				new JLabel("Barcode:"),
				barcode,
				new JLabel("Price:"),
				price,
				new JLabel("Expected Weight:"),
				expectedWeight
		};
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends BarcodedProduct> list, BarcodedProduct value,
			int index, boolean isSelected, boolean cellHasFocus) {
		JPanel panel = new JPanel();
		if(isSelected) {
			panel.setBackground(list.getSelectionBackground());
			panel.setForeground(list.getSelectionForeground());
		} else {
			panel.setBackground(list.getBackground());
			panel.setForeground(list.getForeground());
		}
		panel.setLayout(new GridLayout(1, 0));
		panel.add(new JLabel(value.getDescription()));
		panel.add(new JLabel(value.getBarcode().toString()));
		panel.add(new JLabel(formatPrice(value.getPrice())));
		panel.add(new JLabel(new Mass(value.getExpectedWeight()).inGrams().toPlainString() + "g"));
		return panel;
	}
	
	private String formatPrice(long price) {
		long dollars = price / 100;
		long cents = price % 100;
		
		return "$" + dollars + "." + cents;
	}
	
	private Barcode getBarcode() {
		String barcodeText = this.barcode.getComponent().getText();

		Numeral[] digits = new Numeral[barcodeText.length()];
		for(int i = 0; i < barcodeText.length(); i++) {
			try {
				digits[i] = Numeral.valueOf(Byte.parseByte("" + barcodeText.charAt(i)));
			} catch(NumberFormatException e1) {
				ErrorPopup.showError("Invalid Barcode", "The barcode " + barcode + " is not a valid barcode.");
				return null;
			}
		}
		try {
			return new Barcode(digits);
		} catch(SimulationException e1) {
			ErrorPopup.showError("Invalid Barcode", "The barcode must be at least one digit long!");
			return null;
		}
	}
	
	private Long getPrice() {
		double price;
		try {
			price = Double.parseDouble(this.price.getComponent().getText());
		} catch(NumberFormatException e) {
			ErrorPopup.showError("Invalid price", "The price " + this.price.getComponent().getText() + " is not a valid price.");
			return null;
		}
		
		return (long) (price * 100);
	}

	private Mass getExpectedWeight() {
		double expectedWeight;
		try {
			expectedWeight = Double.parseDouble(this.expectedWeight.getComponent().getText());
		} catch(NumberFormatException e) {
			ErrorPopup.showError("Invalid price", "The price " + this.price.getComponent().getText() + " is not a valid price.");
			return null;
		}
		
		return new Mass(expectedWeight);
	}

	@Override
	protected BarcodedProduct createProduct() {
		String description = this.description.getComponent().getText();
		if(description == null || description.trim().equals("")) return null;

		Barcode barcode = getBarcode();
		if(barcode == null) return null;

		Long price = getPrice();
		if(price == null) return null;

		Mass expectedWeight = getExpectedWeight();
		if(expectedWeight == null) return null;

		return new BarcodedProduct(barcode, description, price, expectedWeight.inGrams().doubleValue());
	}

	@Override
	protected void addProduct(BarcodedProduct product) {
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(product.getBarcode(), product);
	}

	@Override
	protected void removeProduct(BarcodedProduct product) {
		ProductDatabases.BARCODED_PRODUCT_DATABASE.remove(product.getBarcode());
	}

	@Override
	protected boolean isProductInDatabase(BarcodedProduct product) {
		return ProductDatabases.BARCODED_PRODUCT_DATABASE.containsKey(product.getBarcode());
	}

}
