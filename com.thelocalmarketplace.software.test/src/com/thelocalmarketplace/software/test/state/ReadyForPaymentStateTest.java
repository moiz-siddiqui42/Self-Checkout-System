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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.printer.IReceiptPrinter;
import com.jjjwelectronics.scanner.Barcode;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.software.SelfCheckoutConfiguration;
import com.thelocalmarketplace.software.Software;
import com.thelocalmarketplace.software.payment.CashPayment;
import com.thelocalmarketplace.software.session.UserSession;
import com.thelocalmarketplace.software.state.UserSessionState;
import com.thelocalmarketplace.software.test.stubs.TestableAttendantStation;
import com.thelocalmarketplace.software.test.stubs.TestableSelfCheckoutStationGold;

import powerutility.PowerGrid;

public class ReadyForPaymentStateTest {
	
	UserSession session;
	
	@Before
	public void setup() {
		PowerGrid.engageUninterruptiblePowerSource();
		Software.uninitialize();
		Software.initialize(new SelfCheckoutConfiguration(TestableSelfCheckoutStationGold.class, TestableAttendantStation.class), 1);
		session = Software.getInstance().startNewSession(0);
		session.getTransaction().addItem(new BarcodedProduct(new Barcode(new Numeral[] {Numeral.five}), "test product", 100, 100));

		IReceiptPrinter printer = Software.getInstance().getHardware(0).getPrinter();
		try {
			printer.addInk(1<<20);
			printer.addPaper(1024);
		} catch (OverloadedDevice e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testEmptyTransaction() {
		Software.uninitialize();
		Software.initialize(new SelfCheckoutConfiguration(TestableSelfCheckoutStationGold.class, TestableAttendantStation.class), 1);
		session = Software.getInstance().startNewSession(0);
		session.setState(UserSessionState.READY_FOR_PAYMENT);
		assertEquals(session.getState(), UserSessionState.READY_FOR_ITEM);
	}
	
	@Test
	public void testCoinSlotEnabled() {
		session.setState(UserSessionState.READY_FOR_PAYMENT);
		// The coin slot should be enabled.
		assertFalse(Software.getInstance().getHardware(0).getCoinSlot().isDisabled());
	}
	
	@Test
	public void testStateSetWithNoRemainingBalance() {
		session.getTransaction().addPayment(new CashPayment(BigDecimal.valueOf(1)));
		session.setState(UserSessionState.READY_FOR_PAYMENT);
		// The session should have ended.
		assertNull(Software.getInstance().getCurrentSession(0));
	}
	
	@Test
	public void testOnScanBarcode() {
		session.setState(UserSessionState.READY_FOR_PAYMENT);
		
		// The state should not change
		UserSessionState newState = UserSessionState.READY_FOR_PAYMENT.onScanBarcode(session, null);
		
		assertEquals(newState, null);
	}
	
	@Test
	public void testOnWeightChangedSignificant() {
		session.setState(UserSessionState.READY_FOR_PAYMENT);
		
		// The state should not change
		UserSessionState newState = UserSessionState.READY_FOR_PAYMENT.onWeightChanged(session, new Mass(150.00));

		assertEquals(newState, UserSessionState.WAITING_FOR_BAGGING);
	}
	
	@Test
	public void testOnWeightChangedInsignificant() {
		session.setState(UserSessionState.READY_FOR_PAYMENT);
		
		// The state should not change
		UserSessionState newState = UserSessionState.READY_FOR_PAYMENT.onWeightChanged(session, new Mass(100.01));
		
		assertEquals(newState, null);
	}
	
	@Test
	public void testInsertingCoins() {
		session.getTransaction().addItem(new BarcodedProduct(new Barcode(new Numeral[] {Numeral.five}), "test product", 100, 100));
		session.setState(UserSessionState.READY_FOR_PAYMENT);
		assertEquals(session.getState(), UserSessionState.READY_FOR_PAYMENT);
		session.getState().onCoinInserted(session, new BigDecimal(1));
		UserSessionState state = session.getState().onCoinInserted(session, new BigDecimal(1));
		session.setState(state);
		assertNull(Software.getInstance().getCurrentSession(0));
	}
}
