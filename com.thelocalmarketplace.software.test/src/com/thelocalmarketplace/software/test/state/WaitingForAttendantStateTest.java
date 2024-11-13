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

public class WaitingForAttendantStateTest {

    private UserSession session;

    @Before
	public void setup() {
		PowerGrid.engageUninterruptiblePowerSource();
		Software.uninitialize();
		Software.initialize(new SelfCheckoutConfiguration(TestableSelfCheckoutStationGold.class, TestableAttendantStation.class), 1);
		session = Software.getInstance().startNewSession(0);
	}

    @Test
	public void TestCoinSlotDisabled() {
		session.setState(UserSessionState.WAITING_FOR_BAGGING);
		assertTrue(Software.getInstance().getHardware(0).getCoinSlot().isDisabled());
	}

    @Test
    public void TestBankNoteInputDisabled() {
        session.setState(UserSessionState.WAITING_FOR_BAGGING);
        assertTrue(Software.getInstance().getHardware(0).getBanknoteInput().isDisabled());
    }

    @Test
    public void TestReady_for_item_state() {
        session.setState(UserSessionState.READY_FOR_ITEM);
        assertTrue(session.getState().equals(UserSessionState.READY_FOR_ITEM));
    }
}
