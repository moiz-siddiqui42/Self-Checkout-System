package com.thelocalmarketplace.software.session;

import java.util.ArrayList;
import java.util.List;

import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.software.Software;

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

import com.thelocalmarketplace.software.payment.Transaction;
import com.thelocalmarketplace.software.state.UserSessionState;

public class UserSession {

    private UserSessionState state = null;
    private Transaction transaction;

    private CoinValidatorHandler coinValidatorHandler;
    private BanknoteValidatorHandler banknoteValidatorHandler;
    private BarcodeHandler barcodeHandler;
    private ElectronicScaleHandler electronicScaleHandler;
    private ReceiptPrinterHandler receiptPrinterHandler;
    private UIHandler uiHandler;
    private CardReaderHandler cardReaderHandler;
    private int machineID;
    
    private List<SessionObserver> observers;
    
    /**
     * Create a user session. This holds all data pertaining
     * to the user during a transaction at a self checkout machine.
     */
    public UserSession(int machineID) {
    	this.transaction = new Transaction(this);
		
    	// Initialize the event handlers
    	this.coinValidatorHandler = new CoinValidatorHandler(this);
    	this.banknoteValidatorHandler = new BanknoteValidatorHandler(this);
    	this.barcodeHandler = new BarcodeHandler(this);
    	this.electronicScaleHandler = new ElectronicScaleHandler(this);
		this.receiptPrinterHandler = new ReceiptPrinterHandler(this);
    	this.uiHandler = new UIHandler(this);
    	this.cardReaderHandler = new CardReaderHandler(this);
    	this.machineID = machineID;
    	
    	this.observers = new ArrayList<>();
    }
    
    /**
     * Set the state to a new value
     * @param newState The new state to set
     */
    public void setState(UserSessionState newState) {
		if(newState == this.state) return;
    	
    	// Send relevant events and update the state field.
    	if(this.state != null) this.state.onStateUnset(this);
    	this.state = newState;
    	newState = this.state.onStateSet(this);
    	if(newState != null) {
    		setState(newState);
    	} else {
    		for(SessionObserver obs : this.observers) {
    			obs.onStateChanged(this.state);
    		}
    	}
    }
    
    /**
     * Get the state
     * @return The state
     */
    public UserSessionState getState() {
		return state;
	}

	/**
     * Get the transaction related to this state
     * @return The transaction related to this state
     */
    public Transaction getTransaction() {
    	return this.transaction;
    } 
    
    /**
     * Get the CoinValidatorObserver for the current session
     * @return The Coin ValidatorObserver
     */
    public CoinValidatorHandler getCoinValidatorHandler() {
    	return this.coinValidatorHandler;
    }
    
    /**
     * Get the BanknoteValidatorObserver for the current session
     * @return The Banknote ValidatorObserver
     */
    public BanknoteValidatorHandler getBanknoteValidatorHandler() {
    	return this.banknoteValidatorHandler;
    }
    
    /**
     * Get the BarcodeScannerListener for the current session
     * @return The BarcodeScannerListener
     */
	public BarcodeHandler getBarcodeHandler() {
		return barcodeHandler;
	}

	/**
	 * Get the ElectronicScaleListener for the current session
	 * @return The ElectronicScaleListener
	 */
	public ElectronicScaleHandler getElectronicScaleHandler() {
		return electronicScaleHandler;
	}

	/**
	 * Get the ReceiptPrinterHandler for the current session
	 * @return Current ReceiptPrinterHandler
	 */
	public ReceiptPrinterHandler getReceiptPrinterHandler() { return receiptPrinterHandler; }
	
	/**
	 * Get the UIListener for the current session
	 * @return The UIListener
	 */
	public UIHandler getUIHandler() {
		return uiHandler;
	}
	
	/*
	 * Get the CardReaderListener for the current session
	 * @return The CardReaderHandlerListener
	 */
	public CardReaderHandler getCardReaderHandler() {
		return this.cardReaderHandler; 
	}
	
	/**
	 * Gets the machine ID for the self checkout station of the current session
	 * @return
	 */
	public int getMachineID() {
		return this.machineID;
	}
	
	/**
	 * Gets the hardware for the self checkout station.
	 * @return
	 */
	public AbstractSelfCheckoutStation getHardware() {
		return Software.getInstance().getHardware(machineID);
	}
	
	/*
	 * Registers a listener for this user session
	 * @param observer The observer to register
	 */
	public void register(SessionObserver observer) {
		this.observers.add(observer);
	}
	
	/**
	 * Deregister a listener for this user session
	 * @param observer The observer to deregister
	 */
	public void deregister(SessionObserver observer) {
		this.observers.remove(observer);
	}
	
	/**
	 * Degregister all listeners for this user session
	 */
	public void deregisterAll() {
		this.observers.removeAll(this.observers);
	}
}
