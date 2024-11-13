package com.thelocalmarketplace.software.payment;

import com.jjjwelectronics.Mass;

public interface TransactionObserver {
	void itemAdded(TransactionItem product);
	void itemRemoved(TransactionItem product);
	
	void paymentAdded(IPayment payment);	
	void bagAdded(Mass bagMass);
}
