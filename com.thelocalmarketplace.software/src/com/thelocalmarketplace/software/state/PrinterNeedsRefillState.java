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

import com.thelocalmarketplace.software.Software;
import com.thelocalmarketplace.software.session.UserSession;

public class PrinterNeedsRefillState implements IUserSessionState<UserSessionState> {
    @Override
    public UserSessionState onStateSet(UserSession session) {
		// Disable the coin slot to prevent the user from inserting a coin while the software
		// is not in the correct state
		session.getHardware().getCoinSlot().disable();
		session.getHardware().getBanknoteInput().disable();
		
        Software.getInstance().attendantStationFlagged = true;
        return null;
    }

    @Override
    public void onStateUnset(UserSession session) {
        Software.getInstance().attendantStationFlagged = false;
    }

    @Override
    public UserSessionState onPrinterRefilled(UserSession session) {
        return UserSessionState.PRINT_RECEIPT;
    }
}
