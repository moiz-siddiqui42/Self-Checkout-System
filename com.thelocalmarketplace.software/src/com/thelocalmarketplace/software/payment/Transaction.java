package com.thelocalmarketplace.software.payment;

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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.jjjwelectronics.EmptyDevice;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.bag.ReusableBag;
import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.NoCashAvailableException;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.software.Software;
import com.thelocalmarketplace.software.membership.MembershipDatabase;
import com.thelocalmarketplace.software.session.UserSession;

public class Transaction {
    /**
     * Items contained in an instance of transaction TODO Create constructor
     */
	private final ArrayList<TransactionItem> items = new ArrayList<>();
    
    private Mass expectedMass = Mass.ZERO;
    
    private BigDecimal totalCost = BigDecimal.ZERO;

    private final HashMap<UUID, IPayment> payments = new HashMap<>();

    private UserSession session;

    private long transactionMembershipID;
    
    private List<TransactionObserver> observers;
    
    public Transaction(UserSession session) {
    	this.session = session;
    	this.observers = new ArrayList<TransactionObserver>();
    	transactionMembershipID = -1;
    }
    
    /**
     * Adds a product into the current transaction
     * Adds weight to total expected weight
     * Adds cost of item to total cost
     * @param product item being added to transaction/products
     */
    public void addItem(TransactionItem item) {
    	if(item == null) throw new NullPointerException("item");
    	
    	items.add(item);
    	totalCost = totalCost.add(item.getPrice());
        expectedMass = expectedMass.sum(item.getMass());
        
        for(TransactionObserver obs : this.observers) {
        	obs.itemAdded(item);
        }
    }

    /**
     * Adds a product into the current transaction
     * Adds weight to total expected weight
     * Adds cost of item to total cost
     * @param product item being added to transaction/products
     */
    public TransactionItem addItem(BarcodedProduct product) {
    	if(product == null) throw new NullPointerException("product");
    	
    	TransactionItem item = TransactionItem.from(product);
        
    	addItem(item);
    	
    	return item;
    }

    /**
     * Adds a PLUcoded product to current transaction
     * calculates cost based on weight on scale
     * @param product
     * @param mass on scale
     */
    public TransactionItem addItem(PLUCodedProduct product, Mass mass) {
    	if(product == null) throw new NullPointerException("product");
    	
        //convert mass to kilograms
        BigDecimal massInKilo = new BigDecimal(mass.inGrams().longValue() / 1000.0);
        BigDecimal pricePerKilo = BigDecimal.valueOf(product.getPrice()).divide(BigDecimal.valueOf(100));
        BigDecimal itemCost = massInKilo.multiply(pricePerKilo);
        
        TransactionItem item = TransactionItem.from(product, mass, itemCost);
        
        addItem(item);
        
        return item;
    }
    
    public void addOwnBag() {
		expectedMass = expectedMass.sum(new Mass(BigInteger.valueOf(15_000_000)));
    }

    public void purchaseBags() throws Exception {
		try {
			ReusableBag bag = session.getHardware().getReusableBagDispenser().dispense();
			addItem(new TransactionItem("Reusable Bag Charge", bag.getMass(), Software.getInstance().getConfiguration().reusableBagCost));
		} catch (EmptyDevice e) {
			throw new Exception("Bag Dispenser is empty");
		}
    }
  
    /**
     *
     * Adds a payment to the transaction by storing in HashMap payments
     * @param payment, type of payment method used, must be initialized so amountPaid is already defined
     */
    public void addPayment(IPayment payment) {
    	UUID transactionId = UUID.randomUUID(); // Generate a unique ID for this transaction/payment
    	payments.put(transactionId, payment); // Add payment to HashMap
    	totalCost = totalCost.subtract(payment.getAmountPaid());
    	
    	for(TransactionObserver obs : this.observers) {
        	obs.paymentAdded(payment);
        }
    }

    /**
     * Removes an item from the transaction
     * @param product item being removed from transaction/products
     */
    public void removeItem(TransactionItem item) {
    	items.remove(item);
    	totalCost = totalCost.subtract(item.getPrice());
    	expectedMass = expectedMass.difference(item.getMass()).abs();
        
        for(TransactionObserver obs : this.observers) {
        	obs.itemRemoved(item);
        }
    }

    /**
     * Getter method for expected weight
     * @return expectedWeight
     */
    public Mass getExpectedMass() {
		return expectedMass;
    }
    
    /**
     * Sets the expected mass for this transaction
     * @param mass
     */
    public void setExpectedMass(Mass mass) {
    	this.expectedMass = mass;
    }
    
    /**
     * Getter method for total cost
     * @return totalCost
     */
    public BigDecimal getTotalCost() {
    	return totalCost;
    }

    public TransactionItem[] getItems() {
    	TransactionItem[] items = new TransactionItem[0];
    	return this.items.toArray(items);
    }

	public IPayment[] getPayments() {
		IPayment[] payments = new IPayment[0];
		return this.payments.values().toArray(payments);
	}

    public void calculateChange() throws Exception  {
        if (totalCost.compareTo(BigDecimal.ZERO) < 0) {
            Software instance = Software.getInstance();
            BigDecimal change = totalCost.negate();
            BigDecimal[] banknoteDenominations = instance.getConfiguration().banknoteDenominations;
            BigDecimal[] coinDenominations = instance.getConfiguration().coinDenominations;
            
            final int BANKNOTE = 0;
            final int COIN = 1;
            
            try {
	            while (change.compareTo(BigDecimal.valueOf(0.05)) >= 0) {
	                int dispense = -1;
	                BigDecimal value = BigDecimal.valueOf(-1);
	                for (BigDecimal banknote : banknoteDenominations) {
	                    if (change.compareTo(banknote) >= 0 && banknote.compareTo(value) > 0 && session.getHardware().getBanknoteDispensers().get(banknote).size() > 0) {
	                        value = banknote;
	                        dispense = BANKNOTE;
	                    }
	                }
	                for (BigDecimal coin : coinDenominations) {
	                    if (change.compareTo(coin) >= 0 && coin.compareTo(value) > 0 && session.getHardware().getCoinDispensers().get(coin).size() > 0) {
	                        value = coin;
	                        dispense = COIN;
	                    }
	                }
	                if (dispense == -1) {           // unable to find anything to dispense
	                    throw new RuntimeException("No valid coins to dispense.");
	                } else {
	                    change = change.subtract(value);
	                    if (dispense == COIN) {
	                        // Coin Dispensation
	                        session.getHardware().getCoinDispensers().get(value).emit();
	                    } else {
	                        // Banknote Dispensation
	                        session.getHardware().getBanknoteDispensers().get(value).emit();
	                    }
	                }
	            }
            }catch (DisabledException | CashOverloadException | NoCashAvailableException e) {
            	// Print error message
                System.err.println("Exception occurred while calculating change: " + e.getMessage());
                // Rethrow the exception to be handled by the caller
                throw e;
            }
            session.getHardware().getBanknoteOutput().dispense();
        }
    }

    public long getTransactionMembershipID() {
        return transactionMembershipID;
    }

    public void setTransactionMembershipID(long transactionMembershipID) {
        this.transactionMembershipID = transactionMembershipID;
    }
	
	/**
	 * Registers a listener for this user session
	 * @param observer The observer to register
	 */
	public void register(TransactionObserver observer) {
		this.observers.add(observer);
	}
	
	/**
	 * Deregister a listener for this user session
	 * @param observer The observer to deregister
	 */
	public void deregister(TransactionObserver observer) {
		this.observers.remove(observer);
	}
	
	/**
	 * Degregister all listeners for this user session
	 */
	public void deregisterAll() {
		this.observers.removeAll(this.observers);
	}
	
	public void applyPoints() {
		if(transactionMembershipID == -1) return;
		BigDecimal amountPaid = BigDecimal.ZERO;
		for(IPayment payment : payments.values()) {
			amountPaid = amountPaid.add(payment.getAmountPaid());
		}
		amountPaid = amountPaid.add(totalCost); // Remove change
		long points = amountPaid.multiply(BigDecimal.valueOf(100)).longValue();
		MembershipDatabase.getInstance().adjustMemberPoints(transactionMembershipID, points);
	}
}