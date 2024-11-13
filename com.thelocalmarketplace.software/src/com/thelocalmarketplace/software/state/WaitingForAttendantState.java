package com.thelocalmarketplace.software.state;

import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.scale.AbstractElectronicScale;
import com.thelocalmarketplace.software.payment.Transaction;
import com.thelocalmarketplace.software.session.UserSession;

public class WaitingForAttendantState implements IUserSessionState<UserSessionState> {

    @Override
    public UserSessionState onStateSet(UserSession session) {
		// Disable the coin slot to prevent the user from inserting a coin while the software
		// is not in the correct state
		session.getHardware().getCoinSlot().disable();
		session.getHardware().getBanknoteInput().disable();
		
        return null;
    }
    
    @Override
    public void onStateUnset(UserSession session) {
    	AbstractElectronicScale scale = (AbstractElectronicScale) session.getHardware().getBaggingArea();
    	Transaction transaction = session.getTransaction();
    	
    	try {
			transaction.setExpectedMass(scale.getCurrentMassOnTheScale());
		} catch (OverloadedDevice e) {
		}
    }
}

