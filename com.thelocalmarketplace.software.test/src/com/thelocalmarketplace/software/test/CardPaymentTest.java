package com.thelocalmarketplace.software.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;

import org.junit.Before;

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

import org.junit.Test;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.card.Card;
import com.jjjwelectronics.card.Card.CardData;
import com.jjjwelectronics.printer.IReceiptPrinter;
import com.jjjwelectronics.scale.IElectronicScale;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.jjjwelectronics.scanner.IBarcodeScanner;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.external.CardIssuer;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.SelfCheckoutConfiguration;
import com.thelocalmarketplace.software.Software;
import com.thelocalmarketplace.software.payment.BankDataBase;
import com.thelocalmarketplace.software.payment.CardPayment;
import com.thelocalmarketplace.software.payment.Transaction;
import com.thelocalmarketplace.software.session.UserSession;
import com.thelocalmarketplace.software.state.UserSessionState;
import com.thelocalmarketplace.software.test.stubs.TestableAttendantStation;
import com.thelocalmarketplace.software.test.stubs.TestableSelfCheckoutStationGold;

import powerutility.NoPowerException;
import powerutility.PowerGrid; 


/**
 * Following class will be for testing transactions specifically related to debit and credit cards
 */
public class CardPaymentTest {

	private Barcode barcode; 
	private BarcodedProduct product; 
	private Card debit; 
	private Card credit; 
	private Card fake; 
	private CardIssuer bank; 
	
	
	@Before
	public void setup() throws OverloadedDevice, RuntimeException {
		PowerGrid.engageUninterruptiblePowerSource();
		Software.uninitialize(); 
		BankDataBase.uninitialize();
		// as of right now this self checkout is bronze, may set to gold to remove some probability
		Software.initialize(new SelfCheckoutConfiguration(
				TestableSelfCheckoutStationGold.class, 
				TestableAttendantStation.class, 
				Currency.getInstance(Locale.CANADA), 
				100, 
				1000, 
				25, 
				new BigDecimal[] {BigDecimal.ONE}, 
				new BigDecimal[] {BigDecimal.valueOf(10)}, 
				100, 
				100,
				BigDecimal.valueOf(1.99)
		), 1);
		barcode = new Barcode(new Numeral []{Numeral.one, Numeral.two, Numeral.three});
		
		product = new BarcodedProduct(barcode, "item", 1249, 100);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, product); 
		
		bank = new CardIssuer("bank", 10);
		debit = new Card("visa", "1234", "name", "503", "1234", true, true);
		Calendar expiry = Calendar.getInstance();
		expiry.set(2026, 1, 1);
		bank.addCardData(debit.number, debit.cardholder, expiry, debit.cvv, 100);
		
		credit = new Card("visa", "4321", "name", "405", "1234", true, true);
		bank.addCardData(credit.number, credit.cardholder, expiry, credit.cvv, 200);
		 
		fake = new Card("card", "1111", "notName", "101", "1234", true, true);
		
		HashMap<String, CardIssuer> map = new HashMap<>(); 
		map.put("visa", bank); 
		BankDataBase.initialize(map);
			
		
		IReceiptPrinter printer = Software.getInstance().getHardware(0).getPrinter();
		try {
			printer.addInk(1<<20);
			printer.addPaper(1024);
		} catch (OverloadedDevice e) {
			e.printStackTrace();
		}
	}
	
	
	@Test 
	public void testDebitSwipePayment() {
		// set up session and self checkout
		Software sc = Software.getInstance(); 
		UserSession session = sc.startNewSession(0); 
		session.getTransaction(); 
		
		IBarcodeScanner scanner = session.getHardware().getMainScanner(); 
		IElectronicScale baggingArea = session.getHardware().getBaggingArea(); 
		// scan the product then add it to the baggingArea
		scanner.scan(new BarcodedItem(barcode, new Mass(100)));
		baggingArea.addAnItem(new BarcodedItem(barcode, new Mass(100)));

		session.setState(UserSessionState.READY_FOR_PAYMENT); 
		try {
			session.getHardware().getCardReader().swipe(debit);
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		assertNull(sc.getCurrentSession(0));
		
		
	}
	
	@Test 
	public void testDebitTapPayment() {
		Software sc = Software.getInstance(); 
		UserSession session = sc.startNewSession(0); 
		session.getTransaction(); 
		
		IBarcodeScanner scanner = session.getHardware().getMainScanner(); 
		IElectronicScale baggingArea = session.getHardware().getBaggingArea(); 
		scanner.scan(new BarcodedItem(barcode, new Mass(100)));
		baggingArea.addAnItem(new BarcodedItem(barcode, new Mass(100)));

		session.setState(UserSessionState.READY_FOR_PAYMENT); 
		try {
			session.getHardware().getCardReader().tap(debit);
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		assertNull(sc.getCurrentSession(0));	
	}
	
	@Test
	public void testDebitInsert() {
		Software sc = Software.getInstance(); 
		UserSession session = sc.startNewSession(0); 
		session.getTransaction(); 
		
		IBarcodeScanner scanner = session.getHardware().getMainScanner(); 
		IElectronicScale baggingArea = session.getHardware().getBaggingArea(); 
		scanner.scan(new BarcodedItem(barcode, new Mass(100)));
		baggingArea.addAnItem(new BarcodedItem(barcode, new Mass(100)));

		session.setState(UserSessionState.READY_FOR_PAYMENT); 
		try {
			session.getHardware().getCardReader().insert(debit, "1234");
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		assertNull(sc.getCurrentSession(0));
		
		
		
	}
	
	@Test 
	public void testTapPayment() {
		Software sc = Software.getInstance(); 
		UserSession session = sc.startNewSession(0); 
		session.getTransaction(); 
		
		IBarcodeScanner scanner = session.getHardware().getMainScanner(); 
		IElectronicScale baggingArea = session.getHardware().getBaggingArea(); 
		scanner.scan(new BarcodedItem(barcode, new Mass(100)));
		baggingArea.addAnItem(new BarcodedItem(barcode, new Mass(100)));

		session.setState(UserSessionState.READY_FOR_PAYMENT); 
		try {
			session.getHardware().getCardReader().tap(credit);
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		assertNull(sc.getCurrentSession(0));
	}
	
	@Test 
	public void testInsetrPayment() {
		Software sc = Software.getInstance(); 
		UserSession session = sc.startNewSession(0); 
		session.getTransaction(); 
		
		IBarcodeScanner scanner = session.getHardware().getMainScanner(); 
		IElectronicScale baggingArea = session.getHardware().getBaggingArea(); 
		scanner.scan(new BarcodedItem(barcode, new Mass(100)));
		baggingArea.addAnItem(new BarcodedItem(barcode, new Mass(100)));

		session.setState(UserSessionState.READY_FOR_PAYMENT); 
		try {
			session.getHardware().getCardReader().insert(credit, "1234");
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		assertNull(sc.getCurrentSession(0));
	}
	
	
	
	@Test
    public void testSwipePayment() {
        Software sc = Software.getInstance();
        UserSession session = sc.startNewSession(0);
        Transaction transaction = session.getTransaction();
		IBarcodeScanner scanner = sc.getHardware(0).getMainScanner(); 
		IElectronicScale baggingArea = sc.getHardware(0).getBaggingArea(); 
		// scan the product then add it to the baggingArea
		scanner.scan(new BarcodedItem(barcode, new Mass(100)));
		baggingArea.addAnItem(new BarcodedItem(barcode, new Mass(100)));

        // Prepare card data (simulate swiping the card)
        CardData cardData = null;
		try {
			cardData = debit.swipe();
		} catch (IOException e) {
			e.printStackTrace();
		} 

        // Initialize CardPayment and attempt payment
        CardPayment payment = new CardPayment(cardData);
        boolean result = payment.makePayment(transaction.getTotalCost());

        // Assert payment success
        assertTrue("Payment should succeed", result);
        // Verify the amount paid is equal to the transaction amount
        assertEquals("Amount paid should match transaction total", transaction.getTotalCost(), payment.getAmountPaid());
    }
	
	
	@Test
    public void testSwipePaymentOnFake() {
        Software sc = Software.getInstance();
        sc.startNewSession(0);
		IBarcodeScanner scanner = sc.getHardware(0).getMainScanner(); 
		IElectronicScale baggingArea = sc.getHardware(0).getBaggingArea(); 
		// scan the product then add it to the baggingArea
		scanner.scan(new BarcodedItem(barcode, new Mass(100)));
		baggingArea.addAnItem(new BarcodedItem(barcode, new Mass(100)));

        // Prepare card data (simulate swiping the card)
        CardData cardData = null;
		try {
			cardData = fake.swipe();
		} catch (IOException e) {
			e.printStackTrace();
		} 

        // Initialize CardPayment and attempt payment
        CardPayment payment = new CardPayment(cardData);
        boolean result = payment.makePayment( sc.getCurrentSession(0).getTransaction().getTotalCost());

        // Assert payment fail
        assertFalse("Payment should fail", result);
    }
	
	
	/**
	 * Test to see ensure that database will not run twice
	 */
	@Test (expected = RuntimeException.class)
	public void testDoubleInitialize() {
		HashMap<String, CardIssuer> map = new HashMap<>(); 
		map.put("visa", bank); 
		BankDataBase.initialize(map);
	}
	
	/**
	 * Tests to see if payment will fail if cardReader attempts to swipe a null card
	 */
	@Test
	public void swipeNull() {
		Software sc = Software.getInstance(); 
		assertThrows(NullPointerException.class, () -> sc.getHardware(0).getCardReader().swipe(null));
	}
	
	/**
	 * Tests to see if payment will fail if cardData is null
	 */
	@Test (expected = NullPointerException.class)
	public void cardDataNull() {
		 Software sc = Software.getInstance();
	        UserSession session = sc.startNewSession(0);
	        Transaction transaction = session.getTransaction();
	        IBarcodeScanner scanner = sc.getHardware(0).getMainScanner(); 
			IElectronicScale baggingArea = sc.getHardware(0).getBaggingArea(); 
			// scan the product then add it to the baggingArea
			scanner.scan(new BarcodedItem(barcode, new Mass(100)));
			baggingArea.addAnItem(new BarcodedItem(barcode, new Mass(100)));

	        // Prepare card data (simulate swiping the card)
	        CardData cardData = null;
	        CardPayment payment = new CardPayment(cardData);
	        payment.makePayment( transaction.getTotalCost());
	}
	
	/**
	 * Tests for noPowerException
	 */
	@Test (expected = NoPowerException.class)
	public void noPowerException() {
		Software sc = Software.getInstance();
        UserSession session = sc.startNewSession(0);
        sc.getHardware(0).getCardReader().turnOff();
        session.getTransaction();
		IBarcodeScanner scanner = sc.getHardware(0).getMainScanner(); 
		IElectronicScale baggingArea = sc.getHardware(0).getBaggingArea(); 
		// scan the product then add it to the baggingArea
		scanner.scan(new BarcodedItem(barcode, new Mass(100)));
		baggingArea.addAnItem(new BarcodedItem(barcode, new Mass(100)));

		try {
			sc.getHardware(0).getCardReader().swipe(debit);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
