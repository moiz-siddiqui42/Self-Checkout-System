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

import java.util.HashMap;

import com.thelocalmarketplace.hardware.external.CardIssuer;

/**
 * Singleton that represents all the possible banks (CardIssuers) that can be accessed, 
 * Is a hashmap with the keys as the type of the card and values being the associated bank 
 * ex. if the card is visa then associated cardissuer will be visa
 */

public class BankDataBase {

	private static BankDataBase instance; 
	private HashMap<String, CardIssuer> database; 
	
	
	private BankDataBase(HashMap<String, CardIssuer>  database) {
		this.database = database; 
	}
	
	public static void initialize(HashMap<String, CardIssuer> database) throws RuntimeException {
		if(instance != null){
			throw new RuntimeException("Database already exists");
		}
		
		instance = new BankDataBase(database);
	}
	
	public static BankDataBase getInstance() {
		return instance; 
	}
	
	public HashMap<String, CardIssuer> getDataBase(){
		return this.database; 
	}
	
	/**
	 * Uninitializes BankDataBase
	 * @return
	 */
	
	public static void uninitialize() {
		if(instance == null) return;
		
		instance = null;
	}
	

}
