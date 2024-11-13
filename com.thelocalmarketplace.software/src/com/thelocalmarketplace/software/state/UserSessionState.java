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
import com.thelocalmarketplace.hardware.PriceLookUpCode;
import com.thelocalmarketplace.software.session.UserSession;

public enum UserSessionState implements IUserSessionState<UserSessionState> {
	/**
	 * This state is used when the system
	 * is ready for an item to be added to the bagging area.
	 */
	READY_FOR_ITEM(new ReadyForItemState()),
	/**
	 * This state is used when the system
	 * is waiting for an item to be added to the bagging area
	 */
	WAITING_FOR_BAGGING(new WaitingForBaggingState()),
	/**
	 * This state is used when the system
	 * is ready to accept payment
	 */
	READY_FOR_PAYMENT(new ReadyForPaymentState()),
	/**
	 * This state is used when the system has received full payment and
	 * can end the customers session
	 */
	PRINT_RECEIPT(new PrintReceiptState()),
	/**
	 * This state is used when the printer cannot fully print a customer receipt
	 * and needs to notify the attendant station
	 */
	PRINTER_NEEDS_REFILL(new PrinterNeedsRefillState()),
	/**
	 * This state is used when the system
	 * is waiting for attendant to approve
	 */
	WAITING_FOR_ATTENDANT(new WaitingForAttendantState()),
	/**
	 * This state is used when the customer
	 * indicates they are adding their own bags
	 */
	ADDING_BAGS_STATE(new AddingBagsState());

	private IUserSessionState<UserSessionState> state;
	
	private UserSessionState(IUserSessionState<UserSessionState> sessionState) {
		this.state = sessionState;
	}

	@Override
	public UserSessionState onStateSet(UserSession session) {
		return state.onStateSet(session);
	}

	@Override
	public void onStateUnset(UserSession session) {
		state.onStateUnset(session);
	}

	@Override
	public UserSessionState onScanBarcode(UserSession session, Barcode barcode) {
		return state.onScanBarcode(session, barcode);
	}

	@Override
	public UserSessionState onWeightChanged(UserSession session, Mass mass) {
		return state.onWeightChanged(session, mass);
	}

	@Override
	public UserSessionState onCoinInserted(UserSession session, BigDecimal value) {
		return state.onCoinInserted(session, value);
	}

	@Override
	public UserSessionState onPrinterRefilled(UserSession session) {
		return state.onPrinterRefilled(session);
	}
	
	@Override
	public UserSessionState onBanknoteInserted(UserSession session, BigDecimal value) {
		return state.onBanknoteInserted(session, value);
	}
	
	@Override
	public UserSessionState onCardDataRead(UserSession session, CardData data) {
		return state.onCardDataRead(session, data);
	}
	
	@Override
	public UserSessionState onPLUentered(UserSession session, PriceLookUpCode plu) {
		return state.onPLUentered(session, plu);
	}
}
