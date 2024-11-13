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

import java.math.BigDecimal;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.card.Card.CardData;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.IBarcodeScanner;
import com.tdc.banknote.Banknote;
import com.tdc.coin.Coin;

import com.thelocalmarketplace.hardware.PriceLookUpCode;

import com.thelocalmarketplace.software.session.UserSession;


public interface IUserSessionState<T> {
	/**
	 * Called when the state is set to the current state.
	 * Useful for enabling or disabling hardware for this
	 * particular state.
	 * @return The state to transition to after this function returns, or null to stay on the same state.
	 */
	T onStateSet(UserSession session);
	
	/**
	 * Called when the current state is set to a different
	 * state when it was previously this state. Useful for
	 * cleaning up resources used, or resetting any changed 
	 * values.
	 */
	default void onStateUnset(UserSession session)  {};
	
	/**
	 * Called when a {@link Barcode} is scanned by any {@link IBarcodeScanner}
	 * on the self checkout station.
	 * @param barcode The {@link Barcode} that was scanned
	 * @return The state to transition to after this function returns, or null to stay on the same state.
	 */
	default T onScanBarcode(UserSession session, Barcode barcode) {return null;};
	
	/**
	 * Called when the {@link Mass} of the bagging area is changed.
	 * @param mass The new total mass on the bagging area scale.
	 * @return The state to transition to after this function returns, or null to stay on the same state.
	 */
	default T onWeightChanged(UserSession session, Mass mass) {return null;};
	
	/**
	 * Called when a {@link Coin} is inserted into the coin slot.
	 * @param value The value of the coin that was inserted.
	 * @return The state to transition to after this function returns, or null to stay on the same state.
	 */
	default T onCoinInserted(UserSession session, BigDecimal value) {return null;};

	/**
	 * Called when the printer is refilled with ink.
	 * @return
	 */
	default T onPrinterRefilled(UserSession session) {return null;};
	/**
	 * Called when a {@link Banknote} is inserted into the input slot.
	 * @param value The value of the banknote that was inserted.
	 * @return The state to transition to after this function returns, or null to stay on the same state.
	 */
	default T onBanknoteInserted(UserSession session, BigDecimal value) {return null;}
	
	/**
	 * Called when a {@link Card} has it's data read by the card reader through tap, swipe or insert.
	 * @param data The Card data of the card that was just read.
	 * @return The state that should be transition to after the function executes, null if state is same. 
	 */
	default T onCardDataRead(UserSession session, CardData data) {return null;};

	/**
	 * Called when a PLU is entered by the user
	 * @param session
	 * @param plu code enetered by user
	 * @return The state that should be transition to after the function executes, null if state is same. 
	 */
	default T onPLUentered(UserSession session, PriceLookUpCode plu ) {return null;};

	
}
