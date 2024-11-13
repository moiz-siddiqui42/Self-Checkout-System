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
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.printer.IReceiptPrinter;
import com.jjjwelectronics.scale.IElectronicScale;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.jjjwelectronics.scanner.IBarcodeScanner;
import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.IComponent;
import com.tdc.IComponentObserver;
import com.tdc.banknote.Banknote;
import com.tdc.banknote.BanknoteInsertionSlot;
import com.tdc.banknote.BanknoteValidator;
import com.tdc.banknote.BanknoteValidatorObserver;
import com.tdc.coin.Coin;
import com.tdc.coin.CoinSlot;
import com.tdc.coin.CoinValidator;
import com.tdc.coin.CoinValidatorObserver;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.SelfCheckoutConfiguration;
import com.thelocalmarketplace.software.Software;
import com.thelocalmarketplace.software.payment.Transaction;
import com.thelocalmarketplace.software.session.UserSession;
import com.thelocalmarketplace.software.state.UserSessionState;
import com.thelocalmarketplace.software.test.stubs.TestableAttendantStation;
import com.thelocalmarketplace.software.test.stubs.TestableSelfCheckoutStationGold;

import powerutility.PowerGrid;

public class FullSystemTest {

	Barcode barcode1, barcode2, barcode3;
	BarcodedProduct product1, product2, product3;
	
	Coin dollarCoin;
	Coin fakeCoin;
	Banknote tenDollarBill;
	
	@Before
	public void setup() {
		PowerGrid.engageUninterruptiblePowerSource();
		Software.uninitialize();
		Software software = Software.initialize(new SelfCheckoutConfiguration(
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
		
		barcode1 = new Barcode(new Numeral[] {
			Numeral.one,
			Numeral.one,
			Numeral.one,
			Numeral.one,
			Numeral.one,
			Numeral.one,
			Numeral.one,
			Numeral.one,
			Numeral.one
		});
		
		barcode2 = new Barcode(new Numeral[] {
			Numeral.two,
			Numeral.two,
			Numeral.two,
			Numeral.two,
			Numeral.two,
			Numeral.two,
			Numeral.two,
			Numeral.two,
			Numeral.two
		});
		
		barcode3 = new Barcode(new Numeral[] {
			Numeral.three,
			Numeral.three,
			Numeral.three,
			Numeral.three,
			Numeral.three,
			Numeral.three,
			Numeral.three,
			Numeral.three,
			Numeral.three
		});

		product1 = new BarcodedProduct(barcode1, "Fake Product 1", 1000, 100);
		product2 = new BarcodedProduct(barcode2, "Fake Product 2", 2000, 200);
		product3 = new BarcodedProduct(barcode3, "Fake Product 3", 900, 300);

		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode1, product1);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode2, product2);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode3, product3);
		
		dollarCoin = new Coin(Currency.getInstance(Locale.CANADA), BigDecimal.ONE);
		fakeCoin = new Coin(Currency.getInstance(Locale.CHINA), BigDecimal.TEN);
		tenDollarBill = new Banknote(Currency.getInstance(Locale.CANADA), BigDecimal.TEN);
		
		
		IReceiptPrinter printer = software.getHardware(0).getPrinter();
		try {
			printer.addInk(1<<20);
			printer.addPaper(1024);
		} catch (OverloadedDevice e) {
			e.printStackTrace();
		}
	}
	
	private static class CoinValidatorObserverStub implements CoinValidatorObserver {

		public boolean invalidCoinDetected = false;
		
		@Override
		public void enabled(IComponent<? extends IComponentObserver> component) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void disabled(IComponent<? extends IComponentObserver> component) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void turnedOn(IComponent<? extends IComponentObserver> component) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void turnedOff(IComponent<? extends IComponentObserver> component) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void validCoinDetected(CoinValidator validator, BigDecimal value) {
			// TODO Auto-generated method stub
			invalidCoinDetected = false;
		}

		@Override
		public void invalidCoinDetected(CoinValidator validator) {
			// TODO Auto-generated method stub
			invalidCoinDetected = true;
		}
		
	}
	
	@Test
	public void TestSingleItemTransaction() {
		Software sc = Software.getInstance();
		UserSession session = sc.startNewSession(0);
		Transaction transaction = session.getTransaction();

		IBarcodeScanner scanner = sc.getHardware(0).getMainScanner(); 
		IElectronicScale baggingArea = sc.getHardware(0).getBaggingArea(); 
		CoinSlot coinSlot = Software.getInstance().getHardware(0).getCoinSlot();

		assertEquals(session.getState(), UserSessionState.READY_FOR_ITEM);
		
		// Add product 1
		for(int i = 0; i < 100; i++) scanner.scan(new BarcodedItem(barcode1, new Mass(100.0)));
		
		// We should now be waiting for the item to be bagged
		assertEquals(session.getState(), UserSessionState.WAITING_FOR_BAGGING);
		
		// Check the cost and expected weight are correct
		assertEquals(transaction.getTotalCost().compareTo(BigDecimal.valueOf(10.00)), 0);
		assertEquals(transaction.getExpectedMass().compareTo(new Mass(100.0)), 0);
		
		// Check there is 1 item in the transaction
		assertEquals(transaction.getItems().length, 1);
		
		// Place the item in the bagging area
		baggingArea.addAnItem(new BarcodedItem(barcode1, new Mass(100.0)));

		// We should now be in the ready for item state
		assertEquals(session.getState(), UserSessionState.READY_FOR_ITEM);
		
		// State our intentions to pay now
		session.setState(UserSessionState.READY_FOR_PAYMENT);
		assertEquals(session.getState(), UserSessionState.READY_FOR_PAYMENT);
		
		CoinValidatorObserverStub stub = new CoinValidatorObserverStub();
		Software.getInstance().getHardware(0).getCoinValidator().attach(stub);
		
		// Pay 1 dollar
		try {
			do {
				coinSlot.receive(dollarCoin);
			} while(stub.invalidCoinDetected);
		} catch (DisabledException | CashOverloadException e) {
			throw new RuntimeException();
		}
		
		BigDecimal expectedCost = BigDecimal.valueOf(9.00);
		
		assertEquals(transaction.getTotalCost().compareTo(expectedCost), 0);
		
		for(int i = 0; i < 8; i++) {
			try {
				do {
					coinSlot.receive(dollarCoin);
				} while(stub.invalidCoinDetected);
				expectedCost = expectedCost.subtract(BigDecimal.ONE);
				assertEquals(transaction.getTotalCost().compareTo(expectedCost), 0);
			} catch (DisabledException | CashOverloadException e) {
				throw new RuntimeException();
			}
		}
		
		assertEquals(transaction.getTotalCost().compareTo(BigDecimal.valueOf(1)), 0);
		try {
			do {
				coinSlot.receive(dollarCoin);
			} while(stub.invalidCoinDetected);
		} catch (DisabledException | CashOverloadException e) {
			throw new RuntimeException();
		}
		assertNull(Software.getInstance().getCurrentSession(0));
	}
	
	private static class BanknoteValidatorObserverStub implements BanknoteValidatorObserver {

		public boolean invalidBanknoteDetected = false;
		
		@Override
		public void enabled(IComponent<? extends IComponentObserver> component) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void disabled(IComponent<? extends IComponentObserver> component) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void turnedOn(IComponent<? extends IComponentObserver> component) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void turnedOff(IComponent<? extends IComponentObserver> component) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void goodBanknote(BanknoteValidator validator, Currency currency, BigDecimal denomination) {
			// TODO Auto-generated method stub
			invalidBanknoteDetected = false;
		}

		@Override
		public void badBanknote(BanknoteValidator validator) {
			// TODO Auto-generated method stub
			invalidBanknoteDetected = true;
		}
		
	}
	
	@Test
	public void TestSingleItemTransactionPayWithBanknote() {
		Software sc = Software.getInstance();
		UserSession session = sc.startNewSession(0);
		Transaction transaction = session.getTransaction();

		IBarcodeScanner scanner = sc.getHardware(0).getMainScanner(); 
		IElectronicScale baggingArea = sc.getHardware(0).getBaggingArea(); 
		BanknoteInsertionSlot banknoteInput = Software.getInstance().getHardware(0).getBanknoteInput();
		
		try {
			sc.getHardware(0).getCoinDispensers().get(BigDecimal.ONE).load(
				new Coin(Currency.getInstance(Locale.CANADA), BigDecimal.ONE),
				new Coin(Currency.getInstance(Locale.CANADA), BigDecimal.ONE)
			);
		} catch (CashOverloadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		assertEquals(session.getState(), UserSessionState.READY_FOR_ITEM);
		
		// Add product 1
		for(int i = 0; i < 100; i++) scanner.scan(new BarcodedItem(barcode3, new Mass(300.0)));
		
		// We should now be waiting for the item to be bagged
		assertEquals(session.getState(), UserSessionState.WAITING_FOR_BAGGING);
		
		// Check the cost and expected weight are correct
		assertEquals(transaction.getTotalCost().compareTo(BigDecimal.valueOf(9.00)), 0);
		assertEquals(transaction.getExpectedMass().compareTo(new Mass(300.0)), 0);
		
		// Check there is 1 item in the transaction
		assertEquals(transaction.getItems().length, 1);
		
		// Place the item in the bagging area
		baggingArea.addAnItem(new BarcodedItem(barcode1, new Mass(300.0)));

		// We should now be in the ready for item state
		assertEquals(session.getState(), UserSessionState.READY_FOR_ITEM);
		
		// State our intentions to pay now
		session.setState(UserSessionState.READY_FOR_PAYMENT);
		assertEquals(session.getState(), UserSessionState.READY_FOR_PAYMENT);
		
		BanknoteValidatorObserverStub stub = new BanknoteValidatorObserverStub();
		Software.getInstance().getHardware(0).getBanknoteValidator().attach(stub);
		
		// Pay 1 dollar
		try {
			do {
				banknoteInput.receive(tenDollarBill);
			} while(stub.invalidBanknoteDetected);
		} catch (DisabledException | CashOverloadException e) {
			throw new RuntimeException();
		}

        List<Coin> collectedCoins = Software.getInstance().getHardware(0).getCoinTray().collectCoins();
        assertEquals(1, collectedCoins.size());
        assertEquals(0, collectedCoins.get(0).getValue().compareTo(BigDecimal.valueOf(1)));
	}
	
	@Test
	public void TestTryAddWeightDuringPayment() {
		Software sc = Software.getInstance();
		UserSession session = sc.startNewSession(0);
		Transaction transaction = session.getTransaction();

		IBarcodeScanner scanner = Software.getInstance().getHardware(0).getMainScanner();
		IElectronicScale baggingArea = Software.getInstance().getHardware(0).getBaggingArea();
		
		BarcodedItem item1 = new BarcodedItem(barcode1, new Mass(100.0));

		assertEquals(session.getState(), UserSessionState.READY_FOR_ITEM);
		
		// Add product 1
		for(int i = 0; i < 100; i++) scanner.scan(new BarcodedItem(barcode1, new Mass(100.0)));
		
		// We should now be waiting for the item to be bagged
		assertEquals(session.getState(), UserSessionState.WAITING_FOR_BAGGING);
		
		// Check the cost and expected weight are correct
		assertEquals(transaction.getTotalCost().compareTo(BigDecimal.valueOf(10.00)), 0);
		assertEquals(transaction.getExpectedMass().compareTo(new Mass(100.0)), 0);
		
		// Check there is 1 item in the transaction
		assertEquals(transaction.getItems().length, 1);
		
		// Place the item in the bagging areas
		baggingArea.addAnItem(item1);

		// We should now be in the ready for item state
		assertEquals(session.getState(), UserSessionState.READY_FOR_ITEM);
		
		// State our intentions to pay now
		session.setState(UserSessionState.READY_FOR_PAYMENT);
		assertEquals(session.getState(), UserSessionState.READY_FOR_PAYMENT);
		
		BarcodedItem item2 = new BarcodedItem(barcode2, new Mass(100.0));
		
		// Place the item in the bagging area
		baggingArea.addAnItem(item2);
		
		// We should now be waiting for the item to be removed.
		assertEquals(session.getState(), UserSessionState.WAITING_FOR_BAGGING);
		
		// Place the item in the bagging area
		baggingArea.removeAnItem(item2);

		// We should now be in the ready for item state
		assertEquals(session.getState(), UserSessionState.READY_FOR_ITEM);
	}
	
	@Test 
	public void TestTryAddWeightWithoutItem() {
		Software sc = Software.getInstance();
		UserSession session = sc.startNewSession(0);

		IElectronicScale baggingArea = Software.getInstance().getHardware(0).getBaggingArea();
		
		BarcodedItem item1 = new BarcodedItem(barcode1, new Mass(100.0));

		assertEquals(session.getState(), UserSessionState.READY_FOR_ITEM);
		
		// Place the item in the bagging area
		baggingArea.addAnItem(item1);
		
		// We should now be waiting for the item to be bagged
		assertEquals(session.getState(), UserSessionState.WAITING_FOR_BAGGING);
		
		// Remove the item from the bagging area
		baggingArea.removeAnItem(item1);

		assertEquals(session.getState(), UserSessionState.READY_FOR_ITEM);
	}
	
	
	@Test
	public void testTryAddItemNotInDataBase() {
		Software sc  = Software.getInstance(); 
		UserSession session = sc.startNewSession(0);
		
		IBarcodeScanner scanner = sc.getHardware(0).getMainScanner(); 
		Numeral[] dummyCode = {Numeral.one, Numeral.two};
		BarcodedItem newItem = new BarcodedItem(new Barcode(dummyCode), new Mass(10.0));
		scanner.scan(newItem); 
		assertEquals(session.getState(), UserSessionState.READY_FOR_ITEM);
	}
	
	
	
	
}
