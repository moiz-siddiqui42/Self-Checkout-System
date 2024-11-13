package com.thelocalmarketplace.software.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.Item;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.software.SelfCheckoutConfiguration;
import com.thelocalmarketplace.software.Software;
import com.thelocalmarketplace.software.payment.TransactionItem;
import com.thelocalmarketplace.software.session.UIHandler;
import com.thelocalmarketplace.software.session.UserSession;
import com.thelocalmarketplace.software.state.UserSessionState;
import com.thelocalmarketplace.software.test.stubs.TestableAttendantStation;
import com.thelocalmarketplace.software.test.stubs.TestableSelfCheckoutStationGold;

public class RemoveItemTest {

    private UserSession userSession;
    private UIHandler uiHandler;
    
    private Barcode barcode; 
    private BarcodedProduct product; 
    private TransactionItem item;
    private BigDecimal initialTotalCost;
    private Mass initialExpectedMass;

    @Before
    public void setup() {
        // Initialize necessary objects
    	Software.uninitialize();
    	Software.initialize(new SelfCheckoutConfiguration(TestableSelfCheckoutStationGold.class, TestableAttendantStation.class), 1);
		userSession = Software.getInstance().startNewSession(0);
        uiHandler = new UIHandler(userSession);

        // Initialize barcode and product
        barcode = new Barcode(new Numeral[]{Numeral.one, Numeral.two, Numeral.three});
        product = new BarcodedProduct(barcode, "Test Product", 1000, 100.0); // Example data
        item = userSession.getTransaction().addItem(product); // Add the product to the transaction
        
        // Save initial total cost and expected mass
        initialTotalCost = userSession.getTransaction().getTotalCost();
        initialExpectedMass = userSession.getTransaction().getExpectedMass();
        
        userSession.getHardware().getBaggingArea().addAnItem(new Item(initialExpectedMass) {});
    }

    @Test
    public void removeItemTest() {
        // Set initial state
        assertEquals(UserSessionState.READY_FOR_ITEM, userSession.getState());

        // Call the method under test
        uiHandler.removeItemSelected(item);

        // Assertions
        assertEquals(UserSessionState.WAITING_FOR_BAGGING, userSession.getState());
        assertNotNull(userSession.getTransaction());
        
        // Calculate the new total cost without the removed item
        BigDecimal newTotalCost = initialTotalCost.subtract(BigDecimal.valueOf(product.getPrice()).divide(BigDecimal.valueOf(100)));
        
        // Calculate the new expected mass without the removed item
        Mass newExpectedMass = initialExpectedMass.difference(new Mass(BigInteger.valueOf((int) (product.getExpectedWeight() * Mass.MICROGRAMS_PER_GRAM)))).abs();
        
        // Check if the total cost is updated correctly
        assertEquals(newTotalCost, userSession.getTransaction().getTotalCost());
        
        // Check if the expected mass is updated correctly
        assertEquals(newExpectedMass, userSession.getTransaction().getExpectedMass());
    }
}
