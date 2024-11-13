package com.thelocalmarketplace.software.payment;

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

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.card.Card.CardData;
import com.jjjwelectronics.card.CardReaderListener;
import com.thelocalmarketplace.hardware.external.CardIssuer;

public class CardPayment implements CardReaderListener, IPayment{

	private BigDecimal amountDue;
	private BigDecimal amountPaid; 
	private CardData data; 
	public CardPayment(CardData data) {
		this.amountPaid = BigDecimal.ZERO;
		this.data = data; 
	}
	
	/**
	 * Will attempt to post a transaction using a credit/debit card via swiping, tap and insert
	 * @return result of transaction, true if successful, false if not
	 */
	public boolean makePayment(BigDecimal amount) {
		this.amountDue = amount;

		// check to see if the bank that corresponds to the card's type exists 
		if(BankDataBase.getInstance().getDataBase().containsKey(data.getType().toLowerCase())) {
			
				CardIssuer bank = BankDataBase.getInstance().getDataBase().get(data.getType().toLowerCase());
			
			
				long blockNum = bank.authorizeHold(data.getNumber(), this.amountDue.doubleValue());
				if (blockNum != -1) {
					// if the hold is successful then post the transaction
					boolean posted = bank.postTransaction(data.getNumber(), blockNum, this.amountDue.doubleValue());
						// Whether transaction is valid or not release the hold
						bank.releaseHold(data.getNumber(), blockNum);
						// case where the transaction is successful so need to account for that
						if(posted) {
							setAmountPaid(this.amountDue);
						}
						
					// once that is all done then return the result of the transaction being posted
					return posted;

				}	

		}
		// if the bank doesn't exist then simply return false
		return false;
	}
	
	@Override
	public String toString() {
		return data.getType() + " ending in " + data.getNumber().substring(data.getNumber().length() - 4) + ": $" + getAmountPaid().doubleValue(); 
	}
	
	@Override
	public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {
		

	}

	@Override
	public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {

	}

	@Override
	public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {
		

	}

	@Override
	public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {
		

	}

	@Override
	public void aCardHasBeenSwiped() {
		

	}

	@Override
	public void theDataFromACardHasBeenRead(CardData data) {

	}
	
	@Override
	public BigDecimal getAmountPaid() {
		
		return this.amountPaid;
	}
	
	
	protected void setAmountPaid(BigDecimal amountPaid) {
		this.amountPaid = amountPaid; 
	}

	@Override
	public void aCardHasBeenInserted() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void theCardHasBeenRemoved() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void aCardHasBeenTapped() {
		// TODO Auto-generated method stub
		
	}
}
