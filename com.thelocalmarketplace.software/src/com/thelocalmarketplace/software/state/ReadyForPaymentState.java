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
import com.thelocalmarketplace.software.payment.CardPayment;
import com.thelocalmarketplace.software.payment.CashPayment;
import com.thelocalmarketplace.software.payment.Transaction;
import com.thelocalmarketplace.software.session.UserSession;

public class ReadyForPaymentState implements IUserSessionState<UserSessionState> {

	@Override
	public UserSessionState onStateSet(UserSession session) {
		//Get current balance by creating a transaction instance
		Transaction transaction = session.getTransaction(); 
		
		if (transaction.getItems().length == 0) {
			//If item is at a 0, set state to ready for item
	    	return UserSessionState.READY_FOR_ITEM;
		} else if(transaction.getTotalCost().compareTo(BigDecimal.ZERO) <= 0) {
			//Check if balance is 0 and that there is an item to end session 
			try {
				transaction.calculateChange();
			} catch (Exception e) {
				e.printStackTrace();
			}
	        return UserSessionState.PRINT_RECEIPT;
		}
		
		// Enable the coin slot to allow the user to insert a coin while the software
		// is in the correct state
		session.getHardware().getCoinSlot().enable();
		session.getHardware().getBanknoteInput().enable();
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

	@Override
	public UserSessionState onCoinInserted(UserSession session, BigDecimal value) {
		//Create CoinPayment class instance
		CashPayment payment = new CashPayment(value);
		
		//Adding payment onto the current transaction 
		Transaction transaction = session.getTransaction();
	    transaction.addPayment(payment);
	    
	    //Checking when the balance goes down to zero 
	    if (transaction.getTotalCost().compareTo(BigDecimal.ZERO) <= 0) {
			try {
				transaction.calculateChange();
			} catch (Exception e) {
				return UserSessionState.WAITING_FOR_ATTENDANT;
			}
	        return UserSessionState.PRINT_RECEIPT;
	    }
	    
	    return null;
	}
	
	public UserSessionState onBanknoteInserted(UserSession session, BigDecimal value) {
		//Create CoinPayment class instance
		CashPayment payment = new CashPayment(value);
		
		//Adding payment onto the current transaction 
		Transaction transaction = session.getTransaction();
	    transaction.addPayment(payment);
	    
	    //Checking when the balance goes down to zero 
	    if (transaction.getTotalCost().compareTo(BigDecimal.ZERO) <= 0) {
			try {
				transaction.calculateChange();
			} catch (Exception e) {
				return UserSessionState.WAITING_FOR_ATTENDANT;
			}
	        return UserSessionState.PRINT_RECEIPT;
	    }
	    
	    return null;
	}
	
	@Override 
	public UserSessionState onCardDataRead(UserSession session, CardData data) {
		CardPayment payment = new CardPayment(data);
		Transaction transaction = session.getTransaction();; 
		payment.makePayment(transaction.getTotalCost()); 
		System.out.println("Paid amount: " + payment.getAmountPaid().doubleValue());
		transaction.addPayment(payment);

		
		
		if(transaction.getTotalCost().compareTo(BigDecimal.ZERO) <= 0) {
			try {
				transaction.calculateChange();
			} catch (Exception e) {
				e.printStackTrace();
			}
	        session.setState(UserSessionState.PRINT_RECEIPT);
		}
		return null;
	}

}
