package com.thelocalmarketplace.software.UI;

import com.thelocalmarketplace.hardware.PriceLookUpCode;
import com.thelocalmarketplace.software.payment.TransactionItem;

public interface UIObserver {
	
	/**
	 * announces when user selects option to add their own bags
	 */
	void addBagSelected();
	
	/**
	 * announces when user enters a plu code
	 * @param code
	 */
	void addFromPLU(PriceLookUpCode code);
	
	/**
	 * announces when user selects option to remove item from order
	 */
	void removeItemSelected(TransactionItem product);
	
	/**
	 * announces when user selects option to skip bagging the current product
	 */
	void skipBaggingSelected();

	/**
	 * announces when user selects option to finish adding a bag to the current product
	 */
	void doneAddingBagsSelected();

	/*
	 * announces when user selects option to purchase a bag
	 */
	void purchasingBagsSelected();
}
