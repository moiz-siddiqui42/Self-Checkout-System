package com.thelocalmarketplace.software.payment;

import java.math.BigDecimal;

import com.jjjwelectronics.Mass;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.PLUCodedProduct;

public class TransactionItem {
	
	private String description;
	private Mass mass;
	private BigDecimal price;

	private BarcodedProduct barcodedProduct;
	private PLUCodedProduct pluProduct;
	
	public TransactionItem(String description, Mass mass, BigDecimal price) {
		this.description = description;
		this.mass = mass;
		this.price = price;
	}
	
	public static TransactionItem from(BarcodedProduct product) {
		TransactionItem item = new TransactionItem(product.getDescription(), new Mass(product.getExpectedWeight()), BigDecimal.valueOf((double) product.getPrice() / 100.0));
		item.barcodedProduct = product;
		return item;
	}
	
	public static TransactionItem from(PLUCodedProduct product, Mass mass, BigDecimal price) {
		TransactionItem item = new TransactionItem(product.getDescription(), mass, price);
		item.pluProduct = product;
		return item;
	}

	public String getDescription() {
		return description;
	}

	public Mass getMass() {
		return mass;
	}

	public BigDecimal getPrice() {
		return price;
	}
	
	public String getFormattedPrice() {
		String price = this.price.toPlainString();
		return "$" + price;
	}

	public BarcodedProduct getBarcodedProduct() {
		return barcodedProduct;
	}

	public PLUCodedProduct getPluProduct() {
		return pluProduct;
	}
}
