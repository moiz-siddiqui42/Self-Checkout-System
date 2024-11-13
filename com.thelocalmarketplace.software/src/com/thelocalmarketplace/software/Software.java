package com.thelocalmarketplace.software;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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

import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.AttendantStation;
import com.thelocalmarketplace.software.membership.MembershipDatabase;
import com.thelocalmarketplace.software.session.AttendantKeyboardHandler;
import com.thelocalmarketplace.software.session.UserSession;
import com.thelocalmarketplace.software.state.PrintReceiptState;
import com.thelocalmarketplace.software.state.UserSessionState;

import powerutility.PowerGrid;

public class Software {
	
	private static Software instance;
	
	private UserSession[] currentSession;
	
	private AttendantKeyboardHandler keyboardHandler;
	
	private SelfCheckoutConfiguration configuration;
	
	private AttendantStation attendantStation;
	private AbstractSelfCheckoutStation[] selfCheckoutStations;

	private boolean[] isStationEnabled;
	private boolean[] disableStationQueued;

	public boolean attendantStationFlagged; //placeholder for any case where the attendant station may be flagged
	
	private List<List<SoftwareObserver>> observers;
	
	private Software(SelfCheckoutConfiguration configuration, int stationCount) {
		this.configuration = configuration;
		currentSession = new UserSession[stationCount];
		selfCheckoutStations = new AbstractSelfCheckoutStation[stationCount];
		isStationEnabled = new boolean[stationCount];
		disableStationQueued = new boolean[stationCount];
		try {
			attendantStation = configuration.attendantType.getConstructor().newInstance();
			attendantStation.plugIn(PowerGrid.instance());
			attendantStation.turnOn();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			System.out.println("Failed to create the attendant station. The given class does not have a constructor with no formal parameters.");
			e.printStackTrace();
			return;
		}
		for(int i = 0; i < stationCount; i++) {
			AbstractSelfCheckoutStation station;
			try {
				station = configuration.machineType.getConstructor().newInstance();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				System.out.println("Failed to create the self checkout station. The given class does not have a constructor with no formal parameters.");
				e.printStackTrace();
				return;
			}
			isStationEnabled[i] = true;
			station.plugIn(PowerGrid.instance());
			station.turnOn();
			selfCheckoutStations[i] = station;
			attendantStation.add(station);
		}
		
		this.observers = new LinkedList<List<SoftwareObserver>>();
		for(int i = 0; i < stationCount; i++) {
			this.observers.add(new ArrayList<SoftwareObserver>());
		}
	}
	
	/**
	 * Get the instance of the self checkout
	 * @return The instance of the self checkout
	 * @throws RuntimeException if there is no current instance
	 */
	public static Software getInstance() throws RuntimeException {
		if(instance == null) {
			throw new RuntimeException("The self checkout machine has not been initialized yet.");
		}
		return instance;
	}
	
	/**
	 * Initializes the self checkout machine
	 * @param configuration The type of machine
	 * @return The instance of the self checkout
	 * @throws RuntimeException If there is already a self checkout instance
	 */
	public static Software initialize(SelfCheckoutConfiguration configuration, int selfCheckoutCount) throws RuntimeException {
		if(instance != null) throw new RuntimeException("There is already a self checkout initialized!");
		MembershipDatabase.initialize();
		// Initialize the hardware
		AbstractSelfCheckoutStation.configureCurrency(configuration.currency);
		AbstractSelfCheckoutStation.configureBanknoteDenominations(configuration.banknoteDenominations);
		AbstractSelfCheckoutStation.configureBanknoteStorageUnitCapacity(configuration.banknoteStorageCapacity);
		AbstractSelfCheckoutStation.configureReusableBagDispenserCapacity(configuration.reusableBagDispenserCapacity);
		AbstractSelfCheckoutStation.configureCoinDenominations(configuration.coinDenominations);
		AbstractSelfCheckoutStation.configureCoinDispenserCapacity(configuration.coinDispenserCapacity);
		AbstractSelfCheckoutStation.configureCoinStorageUnitCapacity(configuration.coinStorageUnitCapacity);
		AbstractSelfCheckoutStation.configureCoinTrayCapacity(configuration.coinTrayCapacity);
		instance = new Software(configuration, selfCheckoutCount);
		
		PrintReceiptState.machinePointers = new ArrayList<Integer[]>(); 
		return instance;
	}
	
	/**
	 * Uninitializes the self checkout machine.
	 */
	public static void uninitialize() {
		MembershipDatabase.uninitialize();
		if(instance == null) return;
		
		for(int i = 0; i < instance.currentSession.length; i++) {
			instance.endCurrentSession(i);
		}
		
		instance = null;
	}

	public SelfCheckoutConfiguration getConfiguration() {
		return configuration;
	}
	
	/**
	 * Returns the current user session, or null if
	 * there is no current session
	 */
	public UserSession getCurrentSession(int machineID) {
		return currentSession[machineID];
	}
	
	/**
	 * Starts a new user session
	 * @return The user session that was started
	 * @throws RuntimeException If there is already a session in progress
	 */
	public UserSession startNewSession(int machineID) throws RuntimeException {
		if(!isStationEnabled[machineID]) {
			throw new RuntimeException("The self checkout station is disabled");
		}

		if(currentSession[machineID] != null) {
			throw new RuntimeException("There is already an active user session.");
		}
		currentSession[machineID] = new UserSession(machineID);
    	
		currentSession[machineID].setState(UserSessionState.READY_FOR_ITEM);
		
		for(SoftwareObserver obs : this.observers.get(machineID)) {
			obs.onSessionStart();
		}
		
		// Register new listeners
		selfCheckoutStations[machineID].getMainScanner().register(currentSession[machineID].getBarcodeHandler());
		selfCheckoutStations[machineID].getHandheldScanner().register(currentSession[machineID].getBarcodeHandler());
		selfCheckoutStations[machineID].getBaggingArea().register(currentSession[machineID].getElectronicScaleHandler());
		selfCheckoutStations[machineID].getCardReader().register(currentSession[machineID].getCardReaderHandler());
		selfCheckoutStations[machineID].getCoinValidator().attach(currentSession[machineID].getCoinValidatorHandler());
		selfCheckoutStations[machineID].getPrinter().register(currentSession[machineID].getReceiptPrinterHandler());
		selfCheckoutStations[machineID].getBanknoteValidator().attach(currentSession[machineID].getBanknoteValidatorHandler());
		
		// set machine pointer to 0 initially
		PrintReceiptState.machinePointers.add(machineID, new Integer[] {0,0});
		
		return currentSession[machineID];
	}
	
	/**
	 * Ends the current session
	 * @return true, if a session was ended. false, if there was
	 * no active session
	 */
	public boolean endCurrentSession(int machineID) {
		if(currentSession[machineID] == null) return false;
		
		if(disableStationQueued[machineID]) {
			disableStation(machineID);
		}
		
		for(SoftwareObserver obs : this.observers.get(machineID)) {
			obs.onSessionEnd();
		}
		
		currentSession[machineID].deregisterAll();
		
		currentSession[machineID].getTransaction().applyPoints();
		
		// Remove old listeners
		selfCheckoutStations[machineID].getMainScanner().deregister(currentSession[machineID].getBarcodeHandler());
		selfCheckoutStations[machineID].getHandheldScanner().deregister(currentSession[machineID].getBarcodeHandler());
		selfCheckoutStations[machineID].getBaggingArea().deregister(currentSession[machineID].getElectronicScaleHandler());
		selfCheckoutStations[machineID].getCardReader().deregister(currentSession[machineID].getCardReaderHandler());
		selfCheckoutStations[machineID].getCoinValidator().detach(currentSession[machineID].getCoinValidatorHandler());
		selfCheckoutStations[machineID].getPrinter().deregister(currentSession[machineID].getReceiptPrinterHandler());
		selfCheckoutStations[machineID].getBanknoteValidator().detach(currentSession[machineID].getBanknoteValidatorHandler());
		
		currentSession[machineID] = null;

		if(PredictIssue.predictAllIssues(selfCheckoutStations[machineID])) {
			disableStation(machineID);
		}
		return true;
	}

	/**
	 * Gets the hardware for the self checkout station.
	 * @return The hardware of the self checkout station.
	 */
	public AbstractSelfCheckoutStation getHardware(int machineID) {
		return selfCheckoutStations[machineID];
	}

	/**
	 * Gets the value of isStationEnabled.
	 * @return true if the station is currently enabled, false if it is currently disabled.
	 */
	public boolean getStationEnabledState(int machineId) {
		return isStationEnabled[machineId];
	}

	/**
	 * Enables the self checkout station.
	 * @return false if the station was already enabled; true if the station was changed from disabled to enabled.
	 */
	public boolean enableStation(int machineId) {
		if(isStationEnabled[machineId]) return false; // The machine is already enabled
		boolean returnBool = !isStationEnabled[machineId];
		isStationEnabled[machineId] = true;
		for(SoftwareObserver obs : this.observers.get(machineId)) {
			obs.onMachineEnabled();
		}
		return returnBool;
	}

	/**
	 * Attempts to disable the self checkout station.
	 * @return true if the station was successfully disabled, false if it could not be disabled.
	 */
	public boolean disableStation(int machineId) {
		if(!isStationEnabled[machineId]) return false; // The machine is already disabled
		if(currentSession[machineId] == null) {
			isStationEnabled[machineId] = false;
			disableStationQueued[machineId] = false;
			for(SoftwareObserver obs : this.observers.get(machineId)) {
				obs.onMachineDisabled();
			}
			return true;
		} else {
			disableStationQueued[machineId] = true;
			return false;
		}
	}
	
	/**
	 * Registers a observer for a particular machine
	 * @param machineID The machine to observe
	 * @param observer The observer
	 */
	public void register(int machineID, SoftwareObserver observer) {
		this.observers.get(machineID).add(observer);
	}
	
	/**
	 * Deregisters an observer for a particular machine
	 * @param machineID The machine to observe
	 * @param observer The observer
	 */
	public void deregister(int machineID, SoftwareObserver observer) {
		this.observers.get(machineID).remove(observer);
	}
	
	/**
	 * Deregisters all observer for a particular machine
	 * @param machineID The machine to observe
	 */
	public void deregisterAll(int machineID) {
		this.observers.get(machineID).removeAll(this.observers.get(machineID));
	}
	/**
	 * Provides Access to the instance of attendant station
	 * @return Attendant station that corresponds to the software
	 */
	public AttendantStation getAttendantStation() {
		return attendantStation; 
	}
	
	public void startReadKeyboard(int machineID) {
		this.keyboardHandler = new AttendantKeyboardHandler(getCurrentSession(machineID));
		attendantStation.keyboard.register(keyboardHandler);
	}
	
	public void stopReadKeyboard() {
		attendantStation.keyboard.deregister(keyboardHandler);
		this.keyboardHandler = null;
	}
	
	public AttendantKeyboardHandler getKeyboardHandler() {
		return this.keyboardHandler;
	}
	
}
