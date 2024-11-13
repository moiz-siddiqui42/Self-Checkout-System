package com.thelocalmarketplace.software.test.state;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.thelocalmarketplace.software.SelfCheckoutConfiguration;
import com.thelocalmarketplace.software.Software;
import com.thelocalmarketplace.software.session.UserSession;
import com.thelocalmarketplace.software.state.UserSessionState;
import com.thelocalmarketplace.software.test.stubs.TestableAttendantStation;
import com.thelocalmarketplace.software.test.stubs.TestableSelfCheckoutStationGold;

import powerutility.PowerGrid;

public class UserSessionStateTest {
 
    UserSession session;

    @Before
	public void setup() {
		PowerGrid.engageUninterruptiblePowerSource();
		Software.uninitialize();
		Software.initialize(new SelfCheckoutConfiguration(TestableSelfCheckoutStationGold.class, TestableAttendantStation.class), 1);
		session = Software.getInstance().startNewSession(0);
	}
    
    //private UserSession user_session;
    @Test
    public void TestReady_for_item_state() {
        session.setState(UserSessionState.READY_FOR_ITEM);
        assertTrue(session.getState().equals(UserSessionState.READY_FOR_ITEM));
    }

    @Test
    public void TestWaiting_for_bagging_state() {
        session.setState(UserSessionState.WAITING_FOR_BAGGING);
        assertTrue(session.getState().equals(UserSessionState.READY_FOR_ITEM));
    }

    @Test
    public void TestReady_for_payment_state() {
        session.setState(UserSessionState.READY_FOR_PAYMENT);
        assertTrue(session.getState().equals(UserSessionState.READY_FOR_ITEM));
    }

    @Test
    public void TestPrint_Receipt_state() {
        session.setState(UserSessionState.PRINT_RECEIPT);
        assertTrue(session.getState().equals(UserSessionState.PRINT_RECEIPT));
    }

    @Test
    public void TestPrinter_needs_refil_state() {
        session.setState(UserSessionState.PRINTER_NEEDS_REFILL);
        assertTrue(session.getState().equals(UserSessionState.PRINTER_NEEDS_REFILL));
    }

    @Test
    public void TestWaiting_for_attendant_state() {
        session.setState(UserSessionState.WAITING_FOR_ATTENDANT);
        assertTrue(session.getState().equals(UserSessionState.WAITING_FOR_ATTENDANT));
    }
}
