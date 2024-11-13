package com.thelocalmarketplace.software;

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
import java.util.Currency;
import java.util.Locale;

import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.AttendantStation;

public class SelfCheckoutConfiguration {
	
	public Currency currency;
	public BigDecimal[] coinDenominations;
	public BigDecimal[] banknoteDenominations;
	
	public int coinDispenserCapacity;
	public int coinStorageUnitCapacity;
	public int coinTrayCapacity;
	public int banknoteStorageCapacity;
	public int reusableBagDispenserCapacity;
	public BigDecimal reusableBagCost;
	
	public Class<? extends AbstractSelfCheckoutStation> machineType;
	public Class<? extends AttendantStation> attendantType;
	
	public SelfCheckoutConfiguration(Class<? extends AbstractSelfCheckoutStation> machineType, Class<? extends AttendantStation> attendantType, Currency currency, int coinDispenserCapacity, int coinStorageUnitCapacity, int coinTrayCapacity, BigDecimal[] coinDenominations, BigDecimal[] banknoteDenominations, int banknoteStorageCapacity, int reusableBagDispenserCapacity, BigDecimal reusableBagCost) {
		this.machineType = machineType;
		this.attendantType = attendantType;
		this.coinDenominations = coinDenominations;
		this.currency = currency;
		this.coinDispenserCapacity = coinDispenserCapacity;
		this.coinStorageUnitCapacity = coinStorageUnitCapacity;
		this.coinTrayCapacity = coinTrayCapacity;
		this.banknoteDenominations = banknoteDenominations;
		this.banknoteStorageCapacity = banknoteStorageCapacity;
		this.reusableBagDispenserCapacity = reusableBagDispenserCapacity;
		this.reusableBagCost = reusableBagCost;
	}
	
	public SelfCheckoutConfiguration(Class<? extends AbstractSelfCheckoutStation> machineType, Class<? extends AttendantStation> attendantType) {
		this(
			machineType,
			attendantType,
			Currency.getInstance(Locale.CANADA), 
			100, 
			1000, 
			25, 
			new BigDecimal[] {BigDecimal.ONE}, new BigDecimal[] {BigDecimal.valueOf(10)}, 
			100, 
			100,
			BigDecimal.valueOf(1.99)
		);
	}

}