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
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.SelfCheckoutConfiguration;
import com.thelocalmarketplace.software.Software;
import com.thelocalmarketplace.software.session.UserSession;
import com.thelocalmarketplace.software.state.UserSessionState;
import com.thelocalmarketplace.software.test.stubs.TestableAttendantStation;
import com.thelocalmarketplace.software.test.stubs.TestableSelfCheckoutStationGold;

import powerutility.PowerGrid;

public class ReadyForItemStateTest {
	
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
		Software.uninitialize();
		Software.initialize(new SelfCheckoutConfiguration(TestableSelfCheckoutStationGold.class, TestableAttendantStation.class), 1);
		Software.getInstance().startNewSession(0);
		session.setState(UserSessionState.READY_FOR_ITEM);
		// The coin slot should be disabled.
		assertTrue(Software.getInstance().getHardware(0).getCoinSlot().isDisabled());
	}
	
	@Test
	public void testOnCoinInserted() {
		session.getTransaction().addItem(new BarcodedProduct(new Barcode(new Numeral[] {Numeral.five}), "test product", 100, 100));
		session.setState(UserSessionState.READY_FOR_ITEM);
		
		// The state should not change
		UserSessionState newState = UserSessionState.READY_FOR_ITEM.onCoinInserted(session, null);
		
		assertEquals(newState, null);
	}
	
	@Test
	public void testOnWeightChangedSignificant() {
		session.setState(UserSessionState.READY_FOR_ITEM);
		
		// The state should not change
		UserSessionState newState = UserSessionState.READY_FOR_ITEM.onWeightChanged(session, new Mass(50.0));

		assertEquals(newState, UserSessionState.WAITING_FOR_BAGGING);
	}
	
	@Test
	public void testOnWeightChangedInsignificant() {
		session.setState(UserSessionState.READY_FOR_ITEM);
		
		// The state should not change
		UserSessionState newState = UserSessionState.READY_FOR_ITEM.onWeightChanged(session, new Mass(0.001));

		assertEquals(newState, null);
	}
	
	@Test
	public void testScanBarcodeInDatabase() {
		session.setState(UserSessionState.READY_FOR_ITEM);
		
		Barcode barcode = new Barcode(new Numeral[] {Numeral.eight});
		
		BarcodedProduct product = new BarcodedProduct(barcode, "test product", 1000, 100);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, product);
		
		// The state should not change
		UserSessionState newState = UserSessionState.READY_FOR_ITEM.onScanBarcode(session, barcode);

		assertEquals(session.getTransaction().getItems()[0].getDescription(), product.getDescription());
		
		assertEquals(newState, UserSessionState.WAITING_FOR_BAGGING);
	}
	
	@Test
	public void testScanBarcodeNotInDatabase() {
		session.setState(UserSessionState.READY_FOR_ITEM);
		
		Barcode barcode = new Barcode(new Numeral[] {Numeral.seven});
		
		// The state should not change
		UserSessionState newState = UserSessionState.READY_FOR_ITEM.onScanBarcode(session, barcode);

		assertEquals(session.getTransaction().getItems().length, 0);
		
		assertEquals(newState, null);
	}
}
