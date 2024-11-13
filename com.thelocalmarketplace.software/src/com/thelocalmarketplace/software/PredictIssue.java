package com.thelocalmarketplace.software;

import com.tdc.banknote.IBanknoteDispenser;
import com.tdc.coin.ICoinDispenser;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;

/**
 * Use cases predict low ink/paper were not implemented as the hardware already signals if current levels are <= 10% of the threshold
 */
public class PredictIssue {
	
	/**
	 * Predicts if coinStorage is full
	 */
	public static boolean predictCoinsFull(AbstractSelfCheckoutStation station) {
		if (!station.getBanknoteStorage().hasSpace()) return true;
		return false;
	}
	
	/**
	 * Predicts if banknoteStorage is full
	 */
	public static boolean predictBanknotesFull(AbstractSelfCheckoutStation station) {
		if (!station.getBanknoteStorage().hasSpace()) return true;
		return false;
	}
	
	/**
	 * Predicts if coinDispenser has low coins (25% of threshold)
	 */
	public static boolean predictLowCoins(AbstractSelfCheckoutStation station) {
		
		for (ICoinDispenser dispenser : station.getCoinDispensers().values()) {
			int maxCoins = dispenser.getCapacity();
			int currentCoins = dispenser.size();
			
			if (currentCoins <= Math.floorDiv(maxCoins, 4)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Predicts if banknoteDispenser has low bank notes (25% of threshold)
	 */
	public static boolean predictLowBankNotes(AbstractSelfCheckoutStation station) {
		
		for (IBanknoteDispenser dispenser : station.getBanknoteDispensers().values()) {
			int maxNotes = dispenser.getCapacity();
			int currentNotes = dispenser.size();
			
			if (currentNotes <= Math.floorDiv(maxNotes, 4)) {
				return true;
			}
		}

		return false;
	}
	
	
	/**
	 * Predicts all issues 
	 */
	public static boolean predictAllIssues(AbstractSelfCheckoutStation station) {
		if(predictCoinsFull(station)) return true;
		if(predictBanknotesFull(station)) return true;
		if(predictLowCoins(station)) return true;
		if(predictLowBankNotes(station)) return true;
		return false;
	}
}
