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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import com.thelocalmarketplace.software.SelfCheckoutConfiguration;
import com.thelocalmarketplace.software.Software;
import com.thelocalmarketplace.software.test.stubs.TestableAttendantStation;
import com.thelocalmarketplace.software.test.stubs.TestableSelfCheckoutStationGold;

import powerutility.PowerGrid;

public class SelfCheckoutTest {
	
	@Before
	public void setup() {
		PowerGrid.engageUninterruptiblePowerSource();
		Software.uninitialize();
	}
	
	@Test
	public void testDoubleInitilization() {
		Software.initialize(new SelfCheckoutConfiguration(TestableSelfCheckoutStationGold.class, TestableAttendantStation.class), 1);
		assertThrows(RuntimeException.class, () -> Software.initialize(new SelfCheckoutConfiguration(TestableSelfCheckoutStationGold.class, TestableAttendantStation.class), 0));
		Software.uninitialize();
	}
	
	@Test
	public void testSessionWithBronzeMachine() {
		Software.initialize(new SelfCheckoutConfiguration(
			TestableSelfCheckoutStationGold.class,
			TestableAttendantStation.class,
			Currency.getInstance(Locale.CANADA), 
			100, 
			1000, 
			25, 
			new BigDecimal[] {BigDecimal.ONE}, new BigDecimal[] {BigDecimal.valueOf(10)}, 
			100, 
			100,
			BigDecimal.valueOf(1.99)
		), 1);
		assertNotNull(Software.getInstance());
		Software.uninitialize();
	}
	
	@Test
	public void testSessionWithSilverMachine() {
		Software.initialize(new SelfCheckoutConfiguration(
			TestableSelfCheckoutStationGold.class,
			TestableAttendantStation.class,
			Currency.getInstance(Locale.CANADA), 
			100, 
			1000, 
			25, 
			new BigDecimal[] {BigDecimal.ONE}, new BigDecimal[] {BigDecimal.valueOf(10)}, 
			100, 
			100,
			BigDecimal.valueOf(1.99)
		), 1);
		assertNotNull(Software.getInstance());
	}
	
	@Test
	public void testSessionWithGoldMachine() {
		Software.initialize(new SelfCheckoutConfiguration(
			TestableSelfCheckoutStationGold.class,
			TestableAttendantStation.class,
			Currency.getInstance(Locale.CANADA), 
			100, 
			1000, 
			25, 
			new BigDecimal[] {BigDecimal.ONE}, new BigDecimal[] {BigDecimal.valueOf(10)}, 
			100, 
			100,
			BigDecimal.valueOf(1.99)
		), 1);
		assertNotNull(Software.getInstance());
	}
	
	@Test
	public void testNullType() {
		assertThrows(NullPointerException.class, () -> Software.initialize(null, 1));
		Software.uninitialize();
	}
	
	@Test
	public void testDoubleSessions() {
		Software.uninitialize();
		Software check = Software.initialize(new SelfCheckoutConfiguration(TestableSelfCheckoutStationGold.class, TestableAttendantStation.class), 1);
		check.startNewSession(0);
		assertThrows(RuntimeException.class, () -> check.startNewSession(0));
		Software.uninitialize();
	}
	
	@Test
	public void testSessionEnds() {
		Software check = Software.initialize(new SelfCheckoutConfiguration(TestableSelfCheckoutStationGold.class, TestableAttendantStation.class), 1);
		check.startNewSession(0);
		assertEquals(check.endCurrentSession(0), true);
		Software.uninitialize();
	}
	
	@Test
	public void testSessionEndsNull() {
		Software check = Software.initialize(new SelfCheckoutConfiguration(TestableSelfCheckoutStationGold.class, TestableAttendantStation.class), 1);
		assertEquals(check.endCurrentSession(0), false);
		Software.uninitialize();
	}

	@Test
	public void testIfConfigCorrectlyApplied() {
		Currency currencyTest = Currency.getInstance(Locale.CANADA);
		BigDecimal[] coinDenominationsTest = new BigDecimal[]{BigDecimal.valueOf(0.25), BigDecimal.valueOf(1.00)};
		BigDecimal[] banknoteDenominationsTest = new BigDecimal[]{BigDecimal.valueOf(10), BigDecimal.valueOf(20)};
		int StorageCapTest = 250;
		int DispenserCapTest = 50;
		int TrayCapTest = 15;

		SelfCheckoutConfiguration config = new SelfCheckoutConfiguration(
				TestableSelfCheckoutStationGold.class,
				TestableAttendantStation.class,
				currencyTest,
				DispenserCapTest,
				StorageCapTest,
				TrayCapTest,
				coinDenominationsTest,
				banknoteDenominationsTest,
				100,
				100,
				BigDecimal.valueOf(1.99)
				);

		Software check = Software.initialize(config, 1);
		check.startNewSession(0);

		assertEquals(Currency.getInstance(Locale.CANADA), config.currency);
		assertEquals(50 , config.coinDispenserCapacity);
		assertEquals(250, config.coinStorageUnitCapacity);
		assertEquals(15 , config.coinTrayCapacity);
		assertArrayEquals(new BigDecimal[]{BigDecimal.valueOf(0.25), BigDecimal.valueOf(1.00)}, config.coinDenominations);

		Software.uninitialize();
	}
  
	@Test
	public void testGetInstanceNoInstance() {
		assertThrows(RuntimeException.class, () -> Software.getInstance());
	}
	
	@Test
	public void testSelfCheckoutEnabledFromDisable() {
		Software.initialize(new SelfCheckoutConfiguration(TestableSelfCheckoutStationGold.class, TestableAttendantStation.class), 1);
		Software.getInstance().disableStation(0);
		assertEquals(true, Software.getInstance().enableStation(0));
	}
	
	@Test
	public void testSelfCheckoutEnabledFromEnabled() {
		Software.initialize(new SelfCheckoutConfiguration(TestableSelfCheckoutStationGold.class, TestableAttendantStation.class), 1);
		assertEquals(false, Software.getInstance().enableStation(0));
	}
	
	@Test
	public void testSelfCheckoutEnabledOnInitialize() {
		Software.initialize(new SelfCheckoutConfiguration(TestableSelfCheckoutStationGold.class, TestableAttendantStation.class), 1);
		assertEquals(true, Software.getInstance().getStationEnabledState(0));
	}
	
	@Test
	public void testSelfCheckoutResponse() {
		Software.initialize(new SelfCheckoutConfiguration(TestableSelfCheckoutStationGold.class, TestableAttendantStation.class), 1);
		Software.getInstance().enableStation(0);
		assertEquals(true, Software.getInstance().getStationEnabledState(0));
		Software.getInstance().disableStation(0);
		assertEquals(false, Software.getInstance().getStationEnabledState(0));
	}
	
	@Test
	public void testDisableDuringSession() {
		Software.initialize(new SelfCheckoutConfiguration(TestableSelfCheckoutStationGold.class, TestableAttendantStation.class), 1);
		Software.getInstance().startNewSession(0);
		assertEquals(false, Software.getInstance().disableStation(0));
	}
	
}
