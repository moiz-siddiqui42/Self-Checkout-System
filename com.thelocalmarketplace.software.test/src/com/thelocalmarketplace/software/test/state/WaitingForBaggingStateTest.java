package com.thelocalmarketplace.software.test.state;

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
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.software.SelfCheckoutConfiguration;
import com.thelocalmarketplace.software.Software;
import com.thelocalmarketplace.software.session.UserSession;
import com.thelocalmarketplace.software.state.UserSessionState;
import com.thelocalmarketplace.software.test.stubs.TestableAttendantStation;
import com.thelocalmarketplace.software.test.stubs.TestableSelfCheckoutStationGold;

import powerutility.PowerGrid;

public class WaitingForBaggingStateTest {
	
	UserSession session;
	
	@Before
	public void setup() {
		PowerGrid.engageUninterruptiblePowerSource();
		Software.uninitialize();
		Software.initialize(new SelfCheckoutConfiguration(TestableSelfCheckoutStationGold.class, TestableAttendantStation.class), 1);
		session = Software.getInstance().startNewSession(0);
	}
	
	@Test
	public void testCoinSlotDisabled() {
		session.setState(UserSessionState.WAITING_FOR_BAGGING);
		// The coin slot should be disabled.
		assertTrue(Software.getInstance().getHardware(0).getCoinSlot().isDisabled());
	}
	
	@Test
	public void testOnStateSetValidParameters() {
		session.getTransaction().addItem(new BarcodedProduct(new Barcode(new Numeral[] {Numeral.five}), "test product", 100, 100));
		session.setState(UserSessionState.WAITING_FOR_BAGGING);
		// The coin slot should be disabled.
		assertTrue(session.getState().equals(UserSessionState.WAITING_FOR_BAGGING));
	}
	
	@Test
	public void testOnStateSetOverloadWeight() {
		Software.getInstance().getHardware(0).getBaggingArea().addAnItem(new BarcodedItem(new Barcode(new Numeral[] {Numeral.five}), new Mass(9999999999999999999999999999999999999999999.0)));
		session.setState(UserSessionState.WAITING_FOR_BAGGING);
		
		assertTrue(session.getState().equals(UserSessionState.WAITING_FOR_ATTENDANT));
	}
	
	@Test
	public void testOnStateSetTooMuchWeight() {
		Software.getInstance().getHardware(0).getBaggingArea().addAnItem(new BarcodedItem(new Barcode(new Numeral[] {Numeral.five}), new Mass(9999.0)));
		session.setState(UserSessionState.WAITING_FOR_BAGGING);
		
		// The coin slot should be disabled.
		assertTrue(session.getState().equals(UserSessionState.WAITING_FOR_BAGGING));
	}
	
	@Test
	public void testOnScanBarcode() {
		session.getTransaction().addItem(new BarcodedProduct(new Barcode(new Numeral[] {Numeral.five}), "test product", 100, 100));
		session.setState(UserSessionState.WAITING_FOR_BAGGING);
		
		// The state should not change
		UserSessionState newState = UserSessionState.WAITING_FOR_BAGGING.onScanBarcode(session, null);
		
		assertEquals(newState, null);
	}
	
	@Test
	public void testOnCoinInserted() {
		session.getTransaction().addItem(new BarcodedProduct(new Barcode(new Numeral[] {Numeral.five}), "test product", 100, 100));
		session.setState(UserSessionState.WAITING_FOR_BAGGING);
		
		// The state should not change
		UserSessionState newState = UserSessionState.WAITING_FOR_BAGGING.onCoinInserted(session, null);
		
		assertEquals(newState, null);
	}
	
	@Test
	public void testOnWeightChangedNotEnough() {
		session.getTransaction().addItem(new BarcodedProduct(new Barcode(new Numeral[] {Numeral.five}), "test product", 100, 100));
		session.setState(UserSessionState.WAITING_FOR_BAGGING);
		
		// The state should not change
		UserSessionState newState = UserSessionState.WAITING_FOR_BAGGING.onWeightChanged(session, new Mass(50.0));

		assertEquals(null, newState);
	}
	
	@Test
	public void testOnWeightChangedTooMuch() {
		session.getTransaction().addItem(new BarcodedProduct(new Barcode(new Numeral[] {Numeral.five}), "test product", 100, 100));
		session.setState(UserSessionState.WAITING_FOR_BAGGING);
		
		// The state should not change
		UserSessionState newState = UserSessionState.WAITING_FOR_BAGGING.onWeightChanged(session, new Mass(2000.0));

		assertEquals(null, newState);
	}
	
	@Test
	public void testOnWeightChangedJustRight() {
		session.getTransaction().addItem(new BarcodedProduct(new Barcode(new Numeral[] {Numeral.five}), "test product", 100, 100));
		session.setState(UserSessionState.WAITING_FOR_BAGGING);
		
		// The returned state should be item
		UserSessionState newState = UserSessionState.WAITING_FOR_BAGGING.onWeightChanged(session, new Mass(100.0));

		assertEquals(UserSessionState.READY_FOR_ITEM, newState);
	}
	
	@Test
	public void testOnWeightChangedWithinMargin() {
		session.getTransaction().addItem(new BarcodedProduct(new Barcode(new Numeral[] {Numeral.five}), "test product", 100, 100));
		session.setState(UserSessionState.WAITING_FOR_BAGGING);
		
		// The state should not change
		UserSessionState newState = UserSessionState.WAITING_FOR_BAGGING.onWeightChanged(session, new Mass(101.0));
		
		assertEquals(UserSessionState.READY_FOR_ITEM, newState);
	}
}
