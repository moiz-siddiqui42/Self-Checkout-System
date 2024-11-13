package com.thelocalmarketplace.software.test;

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

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.software.SelfCheckoutConfiguration;
import com.thelocalmarketplace.software.Software;
import com.thelocalmarketplace.software.session.UserSession;
import com.thelocalmarketplace.software.state.UserSessionState;
import com.thelocalmarketplace.software.test.stubs.TestableAttendantStation;
import com.thelocalmarketplace.software.test.stubs.TestableSelfCheckoutStationGold;

import powerutility.PowerGrid;

public class UserSessionTest {

    private UserSession session;
    @Before
    public void setup() {
		PowerGrid.engageUninterruptiblePowerSource();
    	Software.uninitialize();
    	Software.initialize(new SelfCheckoutConfiguration(TestableSelfCheckoutStationGold.class, TestableAttendantStation.class), 1);
    	session = Software.getInstance().startNewSession(0);
    }
    @Test
    public void testInitialState() {
        assertEquals(session.getState(), UserSessionState.READY_FOR_ITEM);
    }
    @Test
    public void testChangingState() {
    	// The state will not change to ready for payment if there is no
    	// product in the order.
    	session.getTransaction().addItem(new BarcodedProduct(new Barcode(new Numeral[] {Numeral.five}), "test product", 100, 100));
        session.setState(UserSessionState.READY_FOR_PAYMENT);
        UserSessionState newstate = session.getState();
        assertEquals(newstate, UserSessionState.READY_FOR_PAYMENT);
    }
 
    @Test
    public void testChangingState2() {
    	// The state will not change to ready for payment if the expected weight already matches the
    	// actual weight, so we add a product
    	session.getTransaction().addItem(new BarcodedProduct(new Barcode(new Numeral[] {Numeral.five}), "test product", 100, 100));
        session.setState(UserSessionState.WAITING_FOR_BAGGING);
        UserSessionState newstate = session.getState();
        assertEquals(newstate, UserSessionState.WAITING_FOR_BAGGING);
    }

    @Test
    public void testChangingState3() {
    	// The state will not change to ready for payment if the expected weight already matches the
    	// actual weight, so we add a product
    	session.getTransaction().addItem(new BarcodedProduct(new Barcode(new Numeral[] {Numeral.five}), "test product", 100, 100));
        session.setState(UserSessionState.WAITING_FOR_BAGGING);
        session.setState(UserSessionState.READY_FOR_ITEM);
        session.setState(UserSessionState.WAITING_FOR_BAGGING);
        session.setState(UserSessionState.READY_FOR_PAYMENT);
        session.setState(UserSessionState.WAITING_FOR_BAGGING);
        UserSessionState newstate = session.getState();
        assertEquals(newstate, UserSessionState.WAITING_FOR_BAGGING);
    }
    
    @Test
    public void testSameState() {
    	session.setState(UserSessionState.READY_FOR_ITEM);
        assertEquals(session.getState(), UserSessionState.READY_FOR_ITEM);
    	session.setState(UserSessionState.READY_FOR_ITEM);
        assertEquals(session.getState(), UserSessionState.READY_FOR_ITEM);
    }
}
