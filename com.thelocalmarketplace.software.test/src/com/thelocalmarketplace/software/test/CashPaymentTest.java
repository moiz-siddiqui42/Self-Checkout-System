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
 * Connor Eall - 30073291
 * Saif Farag - 30195046
 * Ivan Agalakov - 30172107
 * Samuel Turner - 10064857
 * Stephanie Sevilla - 30176781
 * Winston Wang - 301853211
 */

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import com.thelocalmarketplace.software.payment.CashPayment;

import powerutility.PowerGrid;

public class CashPaymentTest {
    @Before
    public void setup() {
		PowerGrid.engageUninterruptiblePowerSource();
    }
    @Test
    public void testNewPayment() {
        CashPayment payment = new CashPayment(BigDecimal.ONE);
        BigDecimal amount = BigDecimal.ONE;
        assertEquals(amount, payment.getAmountPaid());
    }
    @Test
    public void testPayment() {
    	CashPayment newPayment = new CashPayment(BigDecimal.ONE);
    	BigDecimal newAmount = BigDecimal.ONE;
    	assertEquals(newPayment.getAmountPaid(), newAmount);
    }
}
