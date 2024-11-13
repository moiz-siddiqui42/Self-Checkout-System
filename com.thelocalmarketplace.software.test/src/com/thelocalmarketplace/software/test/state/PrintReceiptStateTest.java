package com.thelocalmarketplace.software.test.state;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

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

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.card.Card;
import com.jjjwelectronics.printer.IReceiptPrinter;
import com.jjjwelectronics.printer.ReceiptPrinterGold;
import com.jjjwelectronics.scanner.Barcode;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.external.CardIssuer;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.SelfCheckoutConfiguration;
import com.thelocalmarketplace.software.Software;
import com.thelocalmarketplace.software.payment.BankDataBase;
import com.thelocalmarketplace.software.payment.CardPayment;
import com.thelocalmarketplace.software.payment.CashPayment;
import com.thelocalmarketplace.software.payment.Transaction;
import com.thelocalmarketplace.software.session.UserSession;
import com.thelocalmarketplace.software.state.PrintReceiptState;
import com.thelocalmarketplace.software.state.UserSessionState;
import com.thelocalmarketplace.software.test.stubs.TestableAttendantStation;
import com.thelocalmarketplace.software.test.stubs.TestableSelfCheckoutStationGold;

import powerutility.PowerGrid;

public class PrintReceiptStateTest {
	private PrintReceiptState state;
	private UserSession session;
	private Barcode tempBarcode;
	private Mass tempMass;
	private Barcode barcode;
	private Barcode barcode1;
	private Barcode barcode2;
	private Barcode barcode3;
	private BarcodedProduct product, product1, product2, product3;
	private IReceiptPrinter printer;
	private Card credit;

	@Before
	public void setUp() throws OverloadedDevice, RuntimeException {
		PowerGrid.engageUninterruptiblePowerSource();
		Software.uninitialize();
		BankDataBase.uninitialize();
		BankDataBase.initialize(new HashMap<String, CardIssuer>());

		credit = new Card("visa", "12345678", "Ansel", "304", "1234", true, true);

		BankDataBase.getInstance().getDataBase().put("visa", new CardIssuer("visa", 10));
		Calendar expiry = Calendar.getInstance();
		expiry.set(2026, 1, 1);
		BankDataBase.getInstance().getDataBase().get("visa").addCardData(credit.number, credit.cardholder, expiry,
				credit.cvv, 2000);

		Software.initialize(
				new SelfCheckoutConfiguration(TestableSelfCheckoutStationGold.class, TestableAttendantStation.class),
				1);
		session = Software.getInstance().startNewSession(0);
		state = new PrintReceiptState();
		// make some products we can use to print to the receipt of these items in a
		// transaction
		barcode = new Barcode(new Numeral[] { Numeral.one, Numeral.one, Numeral.one, Numeral.one, Numeral.one,
				Numeral.one, Numeral.one, Numeral.one, Numeral.one });

		barcode1 = new Barcode(new Numeral[] { Numeral.two, Numeral.two, Numeral.two, Numeral.two, Numeral.two,
				Numeral.two, Numeral.two, Numeral.two, Numeral.two });
		barcode2 = new Barcode(new Numeral[] { Numeral.three, Numeral.three, Numeral.three, Numeral.three,
				Numeral.three, Numeral.three, Numeral.three, Numeral.three, Numeral.three });

		barcode3 = new Barcode(new Numeral[] { Numeral.four, Numeral.four, Numeral.four, Numeral.four, Numeral.four,
				Numeral.four, Numeral.four, Numeral.four, Numeral.four });

		product = new BarcodedProduct(barcode, "Test", 300, 2);
		product1 = new BarcodedProduct(barcode1, "Test1", 200, 2);
		product2 = new BarcodedProduct(barcode2, "Test2", 350, 2);
		product3 = new BarcodedProduct(barcode3, "Test3", 100, 2);

		// add to database
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, product);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode1, product1);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode2, product2);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode3, product3);

		printer = Software.getInstance().getHardware(0).getPrinter();

	}

	/**
	 * Tests adding multiple items to a transaction paying for transaction with a
	 * card and sees if the printed output is what is expected
	 * 
	 * @throws OverloadedDevice In the event too much paper or ink added
	 * @throws RuntimeException Can occur during credit card payment
	 * @throws IOException
	 */
	@Test
	public void testReceiptPrint() throws OverloadedDevice, RuntimeException, IOException {

		// need to ensure that the printer is filled with ink and paper
		// adding the max amount, would use the constants detailed in the class but the
		// class is not visible
		Software.getInstance().getHardware(0).getPrinter().addInk(1 << 20);
		Software.getInstance().getHardware(0).getPrinter().addPaper(1024);

		// going to add a bunch of items to the transaction, then set the state to print
		// receipt
		Transaction transaction = Software.getInstance().getCurrentSession(0).getTransaction();
		transaction.addItem(product);
		transaction.addItem(product1);
		transaction.addItem(product2);
		transaction.addItem(product3);

		Software.getInstance().getCurrentSession(0).setState(UserSessionState.READY_FOR_PAYMENT);
		// now call the instance on the credit card
		Software.getInstance().getHardware(0).getCardReader().swipe(credit);

		// check expected value
		String output = printer.removeReceipt();
		assertTrue(output.equals("Test : $3.0\n" + "Test1 : $2.0\n" + "Test2 : $3.5\n"
				+ "Test3 : $1.0\nPayment Methods:\nvisa ending in 5678: $9.5\n"));

	}

	/**
	 * Similar test to the previous but does it on cash as the payment instead of
	 * credit
	 * 
	 * @throws OverloadedDevice if printer is full of ink or paper
	 */
	@Test
	public void testReceiptPrintCash() throws OverloadedDevice {
		Software.getInstance().getHardware(0).getPrinter().addInk(1 << 20);
		Software.getInstance().getHardware(0).getPrinter().addPaper(1024);

		Transaction transaction = Software.getInstance().getCurrentSession(0).getTransaction();
		transaction.addItem(product);
		transaction.addItem(product1);
		transaction.addItem(product2);
		transaction.addItem(product3);

		// "pay" with cash
		transaction.addPayment(new CashPayment(transaction.getTotalCost()));
		// transition state manually for testing purposes
		Software.getInstance().getCurrentSession(0).setState(UserSessionState.PRINT_RECEIPT);
		// check expected value
		String output = printer.removeReceipt();
		assertTrue(output.equals("Test : $3.0\n" + "Test1 : $2.0\n" + "Test2 : $3.5\n"
				+ "Test3 : $1.0\nPayment Methods:\nCash: $9.5\n"));

	}

	/**
	 * Similar to previous other two tests but with multiple sources of payment such
	 * as card and cash
	 * 
	 * @throws RuntimeException possibly can be thrown when paying with card
	 * @throws OverloadedDevice possibly when printing the receipt
	 * @throws IOException      Also possible during card payment
	 */
	@Test
	public void testReceiptPrintMulti() throws RuntimeException, OverloadedDevice, IOException {
		Software.getInstance().getHardware(0).getPrinter().addInk(1 << 20);
		Software.getInstance().getHardware(0).getPrinter().addPaper(1024);

		Transaction transaction = Software.getInstance().getCurrentSession(0).getTransaction();
		transaction.addItem(product);
		transaction.addItem(product1);
		transaction.addItem(product2);
		transaction.addItem(product3);

		transaction.addPayment(new CashPayment(transaction.getTotalCost().divide(BigDecimal.valueOf(2))));

		Software.getInstance().getCurrentSession(0).setState(UserSessionState.READY_FOR_PAYMENT);
		// now call the instance on the credit card
		Software.getInstance().getHardware(0).getCardReader().swipe(credit);

		// check expected value
		String output = printer.removeReceipt();

		if (output
				.equals("Test : $3.0\n" + "Test1 : $2.0\n" + "Test2 : $3.5\n"
						+ "Test3 : $1.0\nPayment Methods:\nCash: $4.75\nvisa ending in 5678: $4.75\n")
				|| output.equals("Test : $3.0\n" + "Test1 : $2.0\nTest2 : $3.5\nTest3 : $1.0"
						+ "\nPayment Methods:\nvisa ending in 5678: $4.75\nCash: $4.75\n")) {
			assertTrue(true);
		} else {
			assertTrue(1 == 2);
		}

	}

	/**
	 * Tests to see if the system goes to the correct state in the event that the
	 * printer does not have enough ink or paper
	 */
	@Test
	public void testEmptyDevice() {
		// add an item so that there is something to print on the receipt
		Software.getInstance().getCurrentSession(0).getTransaction().addItem(product);

		Software.getInstance().getCurrentSession(0).setState(UserSessionState.PRINT_RECEIPT);
		assertEquals(Software.getInstance().getCurrentSession(0).getState(), UserSessionState.PRINTER_NEEDS_REFILL);
	}

	/**
	 * Tests to see if the printer will reprint a receipt after it has been refilled
	 * with ink and paper when there was an attempt to print a receipt that failed
	 * 
	 * @throws OverloadedDevice: In the event that the printer is at max capacity
	 *                           ink or paper
	 */
	@Test
	public void testReprint() throws OverloadedDevice {
		// add item to the transaction, printer right now has no paper and no ink
		Software.getInstance().getCurrentSession(0).getTransaction().addItem(product);
		Software.getInstance().getCurrentSession(0).getTransaction().addPayment(
				new CashPayment(Software.getInstance().getCurrentSession(0).getTransaction().getTotalCost()));
		// try to print the receipt, should fail
		UserSession currentState = Software.getInstance().getCurrentSession(0); 
		currentState.setState(UserSessionState.PRINT_RECEIPT);
		// now we'll act as the attendant and refill the printer with ink and paper
		printer.addInk(1 << 20);
		printer.addPaper(1024);
		
		assertEquals(printer.removeReceipt(), "Test : $3.0\nPayment Methods:\nCash: $3.0\n");

	}
/**
 * Similar to previous test except the machine only has too little ink 
 * @throws OverloadedDevice Possible when adding paper or ink to the machine
 */
	@Test 
	public void testReprintInk() throws OverloadedDevice {
		Software.getInstance().getHardware(0).getPrinter().addInk(1);
		Software.getInstance().getHardware(0).getPrinter().addPaper(1024);
		
		Software.getInstance().getCurrentSession(0).getTransaction().addItem(product);
		Software.getInstance().getCurrentSession(0).getTransaction().addPayment(
				new CashPayment(Software.getInstance().getCurrentSession(0).getTransaction().getTotalCost()));
		// try to print the receipt, should fail
		UserSession currentState = Software.getInstance().getCurrentSession(0); 
		currentState.setState(UserSessionState.PRINT_RECEIPT);
		// refill the printer with ink
		printer.addInk(1 << 20 - 1);
		
		String output = printer.removeReceipt();  
		
		assertEquals(output, "Test : $3.0\nPayment Methods:\nCash: $3.0\n");
		
		
	}
	
	/**
	 * Similar to the previous test except the machine only has too little paper
	 * @throws OverloadedDevice Possible when loading ink and paper onto the machine
	 */
	@Test 
	public void testReprintPaper() throws OverloadedDevice{
		
		Software.getInstance().getHardware(0).getPrinter().addInk(1 << 20);
		Software.getInstance().getHardware(0).getPrinter().addPaper(1);
		
		Software.getInstance().getCurrentSession(0).getTransaction().addItem(product);
		Software.getInstance().getCurrentSession(0).getTransaction().addPayment(
				new CashPayment(Software.getInstance().getCurrentSession(0).getTransaction().getTotalCost()));
		// try to print the receipt, should fail
		UserSession currentState = Software.getInstance().getCurrentSession(0); 
		currentState.setState(UserSessionState.PRINT_RECEIPT);
		// refill the printer with ink
		printer.addPaper(1000);
		
		String output = printer.removeReceipt();  
		assertEquals(output, "Test : $3.0\nPayment Methods:\nCash: $3.0\n");
		
	}
	
	/**
	 * Will be used to test to see if a blank receipt will print when nothing is bought
	 */
	@Test 
	public void testPrintBlank() {
		Software.getInstance().getCurrentSession(0).setState(UserSessionState.PRINT_RECEIPT);
		assertEquals(Software.getInstance().getHardware(0).getPrinter().removeReceipt(), "");
	}
	
	
	
	@Test
	public void testOnStateUnset() {
		state.onStateUnset(session);
		// Verify no state changes or method calls
	}

	@Test
	public void testOnWeightChanged() {
		UserSessionState result = state.onWeightChanged(session, tempMass);
		assertNull(result);
		// Verify no state changes or method calls
	}

	@Test
	public void testOnScanBarcode() {
		UserSessionState result = state.onScanBarcode(session, tempBarcode);
		assertNull(result);
		// Verify no state changes or method calls
	}

	@Test
	public void testOnCoinInserted() {
		UserSessionState result = state.onCoinInserted(session, BigDecimal.ONE);
		assertNull(result);
		// Verify no state changes or method calls
	}

	@Test
	public void testOnPrinterRefilled() {
		UserSessionState result = state.onPrinterRefilled(session);
		assertNull(result);
		// Verify no state changes or method calls
	}
}
