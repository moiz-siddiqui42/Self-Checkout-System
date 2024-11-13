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
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.bag.ReusableBag;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.tdc.CashOverloadException;
import com.tdc.banknote.Banknote;
import com.tdc.coin.Coin;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.PriceLookUpCode;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.SelfCheckoutConfiguration;
import com.thelocalmarketplace.software.Software;
import com.thelocalmarketplace.software.payment.CashPayment;
import com.thelocalmarketplace.software.payment.IPayment;
import com.thelocalmarketplace.software.payment.Transaction;
import com.thelocalmarketplace.software.payment.TransactionItem;
import com.thelocalmarketplace.software.session.AttendantKeyboardHandler;
import com.thelocalmarketplace.software.session.UserSession;
import com.thelocalmarketplace.software.test.stubs.TestableAttendantStation;
import com.thelocalmarketplace.software.test.stubs.TestableSelfCheckoutStationGold;

import powerutility.PowerGrid;

public class TransactionTest {
	private UserSession session;
	private Transaction transaction;
	private BarcodedProduct productOne;
	private BarcodedProduct productTwo;
	private BarcodedProduct bulkyItem;
	private Numeral num;
	private Barcode bc;
	private BarcodedItem itemOne;
	private AttendantKeyboardHandler akh;
	private PLUCodedProduct pluProductOne;
	private PLUCodedProduct pluProductTwo;
	
    // simulate a payment by defining a payment stub
    private static class PaymentStub implements IPayment {
        private BigDecimal amountPaid;
        
        public PaymentStub(BigDecimal amountPaid) {
            this.amountPaid = amountPaid;
        }

        @Override
        public BigDecimal getAmountPaid() {
            return amountPaid;
        }
    }
    
	@Before
	public void setup() {
		ProductDatabases.BARCODED_PRODUCT_DATABASE.clear();
		PowerGrid.engageUninterruptiblePowerSource();
		Software.uninitialize();
		Software.initialize(new SelfCheckoutConfiguration(TestableSelfCheckoutStationGold.class, TestableAttendantStation.class), 1);
		this.session = Software.getInstance().startNewSession(0);
		this.transaction = session.getTransaction();
		this.akh = new AttendantKeyboardHandler(session);
		this.num = Numeral.eight;
		this.bc= new Barcode(new Numeral[] {num});
		this.productOne = new BarcodedProduct(bc, "test1", 100, 1);
		this.productTwo = new BarcodedProduct(bc, "test2", 200, 2);
		this.bulkyItem = new BarcodedProduct(bc,"bulky item", 50, 100);
		this.itemOne = new BarcodedItem(bc,new Mass(BigDecimal.valueOf(1)));
		this.pluProductOne = new PLUCodedProduct(new PriceLookUpCode("1357"), "plu1", 100);
		this.pluProductTwo = new PLUCodedProduct(new PriceLookUpCode("2468"), "plu2", 200);
	}
	
	@Test
	public void testNullItemProduct() {
		assertThrows(NullPointerException.class, () -> transaction.addItem((TransactionItem) null));
		assertThrows(NullPointerException.class, () -> transaction.addItem((PLUCodedProduct) null, Mass.ZERO));
		assertThrows(NullPointerException.class, () -> transaction.addItem((BarcodedProduct) null));
	}
	
	@Test
	public void testNullPayment() {
		assertThrows(NullPointerException.class, () -> transaction.addPayment(null));
	}
	
	@Test
	public void testPositiveWeight() {
		int comparisonResult = transaction.getExpectedMass().compareTo(Mass.ZERO);
		Assert.assertTrue(comparisonResult == 0 || comparisonResult == 1);
	}
	
	@Test
	public void testPositiveCost() {
		Assert.assertTrue(transaction.getTotalCost().compareTo(BigDecimal.ZERO) >= 0);
	}
	
	@Test
	public void testInputOnKeyBoard() {
		akh.aKeyHasBeenReleased("1");
		Assert.assertEquals(akh.getInput(), "1");
	}
	
	@Test
	public void testBackSpaceOnKeyBoard() {
		akh.aKeyHasBeenReleased("1");
		akh.aKeyHasBeenReleased("2");
		akh.aKeyHasBeenReleased("Backspace");
		Assert.assertEquals(akh.getInput(), "1");
	}
	
	
	@Test
	public void testAddBarcodedItemByKeyboard() {
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(bc, productOne);
		akh.aKeyHasBeenReleased("t");
		akh.aKeyHasBeenReleased("e");
		akh.aKeyHasBeenReleased("Enter");
		boolean found = false;
		for(TransactionItem item : transaction.getItems()) {
			if(item.getBarcodedProduct() == productOne) {
				found = true;
				break;
			}
		}
		assertTrue(found);
	}
	
	@Test
	public void testAddPLUItemByKeyboard() {
		ProductDatabases.PLU_PRODUCT_DATABASE.put(new PriceLookUpCode("1357"), pluProductOne);
		akh.aKeyHasBeenReleased("p");
		akh.aKeyHasBeenReleased("l");
		akh.aKeyHasBeenReleased("Enter");
		Assert.assertEquals(transaction.getItems()[0].getPluProduct(), pluProductOne);
	}
	
	
	@Test
	public void testAddOneItemWeight() {
		transaction.addItem(productOne);
		Mass productOneMass = new Mass(productOne.getExpectedWeight());
		Assert.assertTrue(productOneMass.compareTo(transaction.getExpectedMass()) == 0);
	}
	
	@Test
	public void testAddMultipleItemsWeight() {
		transaction.addItem(productOne);
		Mass productOneMass = new Mass(productOne.getExpectedWeight());
		transaction.addItem(productTwo);
		Mass productTwoMass = new Mass(productTwo.getExpectedWeight());
		Mass combinedProductMass = productOneMass.sum(productTwoMass);

		Assert.assertTrue(combinedProductMass.compareTo(transaction.getExpectedMass()) == 0);
	}
	
	@Test
	public void testAddOneItemCost() {
		transaction.addItem(productOne);
		Assert.assertEquals(transaction.getTotalCost().compareTo(BigDecimal.valueOf(1.00)), 0);
	}
	
	@Test
	public void testOnePLUProductMass() {
		//add 1kg of product
		transaction.addItem(pluProductOne, new Mass (1000000000));
		Assert.assertEquals(transaction.getExpectedMass(), new Mass(1000000000));
	}
	
	@Test
	public void testOnePLUProductPrice() {
		//add 1kg of product
		transaction.addItem(pluProductOne, new Mass (1000000000));
		Assert.assertEquals(transaction.getTotalCost(), BigDecimal.valueOf(1));
	}
	
	@Test
	public void removePLUProductMass() {
		//add 1kg of each product
		TransactionItem item1 = transaction.addItem(pluProductOne, new Mass (2000000000));
		transaction.addItem(pluProductTwo, new Mass (1000000000));
		//remove item 1
		transaction.removeItem(item1);
		Assert.assertEquals(transaction.getExpectedMass(), new Mass(1000000000));
	}
	
	@Test
	public void removePLUProductPrice() {
		//add 1kg of each product
		TransactionItem item1 = transaction.addItem(pluProductOne, new Mass (1_000_000_000));
		transaction.addItem(pluProductTwo, new Mass (1_000_000_000));
		//remove item 1
		transaction.removeItem(item1);
		Assert.assertEquals(transaction.getTotalCost(), BigDecimal.valueOf(2));
	}
	
	@Test
	public void checkAddedItemTaggedWithPriceAndWeight() {
		transaction.addItem(pluProductOne, new Mass (1000000000));
		Assert.assertEquals(transaction.getItems()[0].getMass(), new Mass(1000000000));
		Assert.assertEquals(transaction.getItems()[0].getPrice(), new BigDecimal(1));
		Assert.assertEquals(transaction.getItems()[0].getDescription(), "plu1");
	}

	
	@Test
	public void testAddMultipleItemsCost() {
		transaction.addItem(productOne);
		transaction.addItem(productTwo);
		Assert.assertEquals(transaction.getTotalCost().compareTo(BigDecimal.valueOf(3.00)), 0);
	}
	
	@Test
	public void testRemoveItemWeight() {
		transaction.addItem(productOne);
		TransactionItem item = transaction.addItem(productTwo);
		session.getUIHandler().removeItemSelected(item);
		Mass productOneMass = new Mass(productOne.getExpectedWeight());
		Assert.assertTrue(productOneMass.compareTo(transaction.getExpectedMass()) == 0);
	}
	
	@Test
	public void testRemoveItemCost() {
		transaction.addItem(productOne);
		TransactionItem item = transaction.addItem(productTwo);
		session.getUIHandler().removeItemSelected(item);
		BigDecimal productOneCost = BigDecimal.valueOf(productOne.getPrice()).divide(BigDecimal.valueOf(100));
		Assert.assertTrue(productOneCost.compareTo(transaction.getTotalCost())==0);
	}
	
	@Test
	public void testAddBulkyItemWeight() {
		session.getUIHandler().skipBaggingSelected();
		Assert.assertTrue(transaction.getExpectedMass().compareTo(new Mass(0))==0);
	}
	
	@Test
	public void testAddBulkyItemCost() {
		session.getTransaction().addItem(bulkyItem);
		session.getUIHandler().skipBaggingSelected();
		Assert.assertTrue(transaction.getTotalCost().compareTo(BigDecimal.valueOf(bulkyItem.getPrice()).divide(BigDecimal.valueOf(100)))==0);
	}
	
	
	@Test
	public void testValidPayment() {
	    CashPayment valid = new CashPayment(BigDecimal.TEN);
	    BigDecimal tCost = transaction.getTotalCost();
	    transaction.addPayment(valid);
	    assertEquals(valid, transaction.getPayments()[0]);
	    BigDecimal remainder = new BigDecimal(10);
	    BigDecimal newCost = transaction.getTotalCost();
	    newCost = newCost.add(remainder); // Use add() method to add BigDecimal values
	    assertEquals(tCost, newCost); // Use assertEquals for comparison
	}

	
	@Test
    public void testAddItem() {
        transaction.addItem(productOne);
        assertEquals(1, transaction.getItems().length);
    }
	
	@Test
	public void testAddItemByHandheldScanner() {
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(bc, productOne);
		for(int i = 0; i < 100; i++) session.getHardware().getHandheldScanner().scan(itemOne);
		boolean found = false;
		for(TransactionItem item : transaction.getItems()) {
			if(item.getBarcodedProduct() == productOne) {
				found = true;
				break;
			}
		}
		assertTrue(found);
	}

	@Test
    public void testZeroTotalCostChange() throws Exception {
        transaction.calculateChange(); // Calculate change
        // No exception should be thrown because there is no change to dispense
    }
	
    @Test
    public void testZeroChange() {
        transaction.addItem(productOne); // Adding a product with price 1.00
        transaction.addPayment(new PaymentStub(BigDecimal.valueOf(1.00))); // Simulating payment of 1.00
        
        try {
            transaction.calculateChange();
            BigDecimal expectedChange = BigDecimal.valueOf(0.0);
            BigDecimal actualChange = transaction.getTotalCost();
            assertEquals(expectedChange, actualChange);
        } catch (Exception e) {
        }
    }
	
    @Test
    public void testZeroChange2() {
        transaction.addItem(productOne); // Adding a product with price 1.00
        transaction.addItem(productOne); // Adding a product with price 1.00
        transaction.addPayment(new PaymentStub(BigDecimal.valueOf(1.00))); // Simulating payment of 1.00
        transaction.addPayment(new PaymentStub(BigDecimal.valueOf(1.00))); // Simulating payment of 1.00
        
        try {
            transaction.calculateChange();
            BigDecimal expectedChange = BigDecimal.valueOf(0.0);
            BigDecimal actualChange = transaction.getTotalCost();
            assertEquals(expectedChange, actualChange);
        } catch (Exception e) {
        }
    }
	
    @Test
    public void testOneChange() throws Exception {
        transaction.addItem(productOne); // Adding a product with price 1.00
        transaction.addPayment(new PaymentStub(BigDecimal.valueOf(2.00))); // Simulating payment of 2.00
        
        try {
			Software.getInstance().getHardware(0).getCoinDispensers().get(BigDecimal.ONE).load(new Coin(Currency.getInstance(Locale.CANADA), BigDecimal.ONE));
		} catch (CashOverloadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        transaction.calculateChange();
        BigDecimal expectedChange = BigDecimal.valueOf(-1.0);
        BigDecimal actualChange = transaction.getTotalCost();
        assertEquals(expectedChange, actualChange);
        
        // Check the coins that were dispensed match the coins that should have been dispensed.
        List<Coin> collectedCoins = Software.getInstance().getHardware(0).getCoinTray().collectCoins();
        assertEquals(1, collectedCoins.size());
        assertEquals(0, collectedCoins.get(0).getValue().compareTo(BigDecimal.valueOf(1.0)));
    }
	
    @Test
    public void testTwoChange() throws Exception {
        transaction.addItem(productOne); // Adding a product with price 1.00
        transaction.addPayment(new PaymentStub(BigDecimal.valueOf(3.00))); // Simulating payment of 3.00
        
        try {
			Software.getInstance().getHardware(0).getCoinDispensers().get(BigDecimal.ONE).load(
				new Coin(Currency.getInstance(Locale.CANADA), BigDecimal.ONE),
				new Coin(Currency.getInstance(Locale.CANADA), BigDecimal.ONE)
			);
		} catch (CashOverloadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        transaction.calculateChange();
        BigDecimal expectedChange = BigDecimal.valueOf(-2.0);
        BigDecimal actualChange = transaction.getTotalCost();
        assertEquals(expectedChange, actualChange);
        List<Coin> collectedCoins = Software.getInstance().getHardware(0).getCoinTray().collectCoins();
        assertEquals(2, collectedCoins.size());
        assertEquals(0, collectedCoins.get(0).getValue().compareTo(BigDecimal.valueOf(1.0)));
        assertEquals(0, collectedCoins.get(1).getValue().compareTo(BigDecimal.valueOf(1.0)));
    }

	
    @Test
    public void testOptimalChange() throws Exception {
    	Software.uninitialize();
    	Software.initialize(new SelfCheckoutConfiguration(
			TestableSelfCheckoutStationGold.class,
			TestableAttendantStation.class,
    		Currency.getInstance(Locale.CANADA), 
    		100, 
    		1000, 
    		25, 
    		new BigDecimal[] {BigDecimal.valueOf(2), BigDecimal.valueOf(1)}, 
    		new BigDecimal[] {BigDecimal.valueOf(10)},
    		100, 
			100,
			BigDecimal.valueOf(1.99)
    	), 1);
    	session = Software.getInstance().startNewSession(0);
        transaction = session.getTransaction();
        transaction.addItem(productOne); // Adding a product with price 1.00
        transaction.addPayment(new PaymentStub(BigDecimal.valueOf(4.00))); // Simulating payment of 4.00
        
        try {
			Software.getInstance().getHardware(0).getCoinDispensers().get(BigDecimal.ONE).load(
				new Coin(Currency.getInstance(Locale.CANADA), BigDecimal.ONE),
				new Coin(Currency.getInstance(Locale.CANADA), BigDecimal.ONE)
			);
			Software.getInstance().getHardware(0).getCoinDispensers().get(BigDecimal.valueOf(2)).load(
				new Coin(Currency.getInstance(Locale.CANADA), BigDecimal.valueOf(2)),
				new Coin(Currency.getInstance(Locale.CANADA), BigDecimal.valueOf(2))
			);
		} catch (CashOverloadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        transaction.calculateChange();
        BigDecimal expectedChange = BigDecimal.valueOf(-3.0);
        BigDecimal actualChange = transaction.getTotalCost();
        assertEquals(expectedChange, actualChange);
        List<Coin> collectedCoins = Software.getInstance().getHardware(0).getCoinTray().collectCoins();
        assertEquals(2, collectedCoins.size());
        int loonieCount = 0, toonieCount = 0;
        for(Coin coin : collectedCoins) {
        	if(coin.getValue().compareTo(BigDecimal.ONE) == 0) loonieCount++;
        	if(coin.getValue().compareTo(BigDecimal.valueOf(2)) == 0) toonieCount++;
        }
        assertEquals(1, loonieCount);
        assertEquals(1, toonieCount);
    }

	
    @Test
    public void testChangeBill() throws Exception {
        transaction.addItem(productOne); // Adding a product with price 1.00
        transaction.addPayment(new PaymentStub(BigDecimal.valueOf(12.00))); // Simulating payment of 12.00
        
        try {
			Software.getInstance().getHardware(0).getCoinDispensers().get(BigDecimal.ONE).load(
				new Coin(Currency.getInstance(Locale.CANADA), BigDecimal.ONE),
				new Coin(Currency.getInstance(Locale.CANADA), BigDecimal.ONE)
			);
			Software.getInstance().getHardware(0).getBanknoteDispensers().get(BigDecimal.valueOf(10)).load(
				new Banknote(Currency.getInstance(Locale.CANADA), BigDecimal.valueOf(10)),
				new Banknote(Currency.getInstance(Locale.CANADA), BigDecimal.valueOf(10))
			);
		} catch (CashOverloadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        transaction.calculateChange();
        BigDecimal expectedChange = BigDecimal.valueOf(-11.0);
        BigDecimal actualChange = transaction.getTotalCost();
        assertEquals(expectedChange, actualChange);
        List<Coin> collectedCoins = Software.getInstance().getHardware(0).getCoinTray().collectCoins();
        assertEquals(1, collectedCoins.size());
        assertEquals(0, collectedCoins.get(0).getValue().compareTo(BigDecimal.valueOf(1)));
        
        List<Banknote> collectedBanknotes = Software.getInstance().getHardware(0).getBanknoteOutput().removeDanglingBanknotes();
        assertEquals(1, collectedBanknotes.size());
        assertEquals(0, collectedBanknotes.get(0).getDenomination().compareTo(BigDecimal.valueOf(10)));
    }
    
    @Test
    public void testPurchaseBagsNoBags() throws Exception {
		assertThrows(Exception.class, () -> transaction.purchaseBags());
    }

    @Test
    public void testPurchaseBags() throws Exception {
    	BigInteger massOfOneBag = BigInteger.valueOf(5_000_000);
    	Mass expectedMassAfterPurchase = new Mass(massOfOneBag);
    	BigDecimal expectedCostAfterPurchase = BigDecimal.valueOf(1.99);
    	
    	session.getHardware().getReusableBagDispenser().load(new ReusableBag());
    	
		transaction.purchaseBags();
    	
		Mass actualMass = session.getTransaction().getExpectedMass();
		BigDecimal actualCost = session.getTransaction().getTotalCost();
	   	assertEquals(expectedMassAfterPurchase.compareTo(actualMass), 0);
	   	assertEquals(expectedCostAfterPurchase.compareTo(actualCost), 0);
    }
}

	
