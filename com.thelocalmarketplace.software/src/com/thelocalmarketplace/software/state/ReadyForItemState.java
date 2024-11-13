package com.thelocalmarketplace.software.state;

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

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.scale.AbstractElectronicScale;
import com.jjjwelectronics.scale.IElectronicScale;
import com.jjjwelectronics.scanner.Barcode;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.PriceLookUpCode;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.Globals;
import com.thelocalmarketplace.software.Software;
import com.thelocalmarketplace.software.payment.Transaction;
import com.thelocalmarketplace.software.session.UserSession;

import powerutility.NoPowerException; 

public class ReadyForItemState implements IUserSessionState<UserSessionState> {

	@Override
	public UserSessionState onStateSet(UserSession session) {
		// Disable the coin slot to prevent the user from inserting a coin while the software
		// is not in the correct state
		session.getHardware().getCoinSlot().disable();
		session.getHardware().getBanknoteInput().disable();
		Transaction currentTransaction = session.getTransaction(); // Get current transaction
		Mass expectedMass = currentTransaction.getExpectedMass(); // Get expected mass
		IElectronicScale scale = session.getHardware().getBaggingArea();
		if(!(scale instanceof AbstractElectronicScale)) return null;
		
		Mass absoluteDifference;
		try {
			absoluteDifference = expectedMass.difference(((AbstractElectronicScale) scale).getCurrentMassOnTheScale()).abs();
		} catch (OverloadedDevice e) {
			throw new RuntimeException("The scale is currently overloaded.");
		} // Compare expected and actual mass of item placed in bagging area
		
		if(absoluteDifference.compareTo(Globals.MAXIMUM_WEIGHT_DISCREPENCY) > 1) { // If item falls within the scale's sensitivity window,
			return UserSessionState.WAITING_FOR_BAGGING; 					  // go back to ReadyForItemState
		}
		return null;
	}

	@Override
	public UserSessionState onScanBarcode(UserSession session, Barcode barcode) {
		BarcodedProduct barcodeProduct = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(barcode);
		// want to check to see if the product exists within the database
		if(barcodeProduct != null) {
			Transaction currentTransaction = session.getTransaction();
			currentTransaction.addItem(barcodeProduct);
			
			return UserSessionState.WAITING_FOR_BAGGING;	
		}
		return null; 
		
	}
	
	@Override
	public UserSessionState onPLUentered(UserSession session, PriceLookUpCode plu) {;
		PLUCodedProduct product = ProductDatabases.PLU_PRODUCT_DATABASE.get(plu);

		IElectronicScale scale = session.getHardware().getScanningArea();

		Mass massOnScale = Mass.ZERO;
		try {
			massOnScale = ((AbstractElectronicScale) scale).getCurrentMassOnTheScale();
		} catch (OverloadedDevice e) {
			return UserSessionState.WAITING_FOR_ATTENDANT;
		} catch(NoPowerException e) {}
		if (product!=null && massOnScale.compareTo(Mass.ZERO) == 1) {
			session.getTransaction().addItem(product, massOnScale);
			return UserSessionState.WAITING_FOR_BAGGING;
		}
		return null;
	}
 
	@Override
	public UserSessionState onWeightChanged(UserSession session, Mass mass) {
		// Possible Weight Discrepancy
		
		// Get the relevant masses to compare
		Transaction transaction = session.getTransaction();
		Mass expectedMass = transaction.getExpectedMass();
		Mass absoluteDifference = expectedMass.difference(mass).abs();
		
		// The maximum difference between masses.
		Mass maximumDifference = session.getHardware().getBaggingArea().getSensitivityLimit();
		
		// Check if we are within the margin of error. If so, do nothing
		if(absoluteDifference.compareTo(maximumDifference) == 1) {
			return UserSessionState.WAITING_FOR_BAGGING;
		}
		
		// The change in mass was within the margin of error. It is okay to
		// allow the customer to continue. Stay on the same state.
		return null;
	}
}
