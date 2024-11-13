package com.thelocalmarketplace.software.UI;

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
import java.util.HashMap;
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.thelocalmarketplace.hardware.AttendantStation;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.external.CardIssuer;
import com.thelocalmarketplace.software.SelfCheckoutConfiguration;
import com.thelocalmarketplace.software.Software;
import com.thelocalmarketplace.software.UI.Attendant.AttendantUI;
import com.thelocalmarketplace.software.UI.hardwaresim.UIHardwareSimulation;
import com.thelocalmarketplace.software.UI.user.MainUserPanel;
import com.thelocalmarketplace.software.payment.BankDataBase;

import powerutility.PowerGrid;

public class Simulation {
	
	private static int MACHINE_COUNT = 1;
	
	public static void main(String[] args) {
		PowerGrid.engageUninterruptiblePowerSource();
		Software.initialize(new SelfCheckoutConfiguration(
			SelfCheckoutStationGold.class,
			AttendantStation.class,
			Currency.getInstance(Locale.CANADA), 
			100, 
			1000, 
			1000, // Bypass bug in hardware.
			new BigDecimal[] {
					BigDecimal.valueOf(0.05),
					BigDecimal.valueOf(0.10),
					BigDecimal.valueOf(0.25),
					BigDecimal.valueOf(1.00),
					BigDecimal.valueOf(2.00)
			}, 
			new BigDecimal[] {
					BigDecimal.valueOf(5),
					BigDecimal.valueOf(10),
					BigDecimal.valueOf(20),
					BigDecimal.valueOf(50),
					BigDecimal.valueOf(100)
			}, 
			100, 
			100,
			BigDecimal.valueOf(1.99)
		), MACHINE_COUNT);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			// this should never happen!
			e.printStackTrace();
		}
		
		new AttendantUI(); 
		BankDataBase.initialize(new HashMap<String, CardIssuer>());
		UIHardwareSimulation.startHardwareSimulationUI(1);
		
		for(int i = 0; i < MACHINE_COUNT; i++) {
			Software.getInstance().getHardware(i).getScreen().getFrame().add(new MainUserPanel(0));
			Software.getInstance().getHardware(i).getScreen().setVisible(true);
			Software.getInstance().getHardware(i).getScreen().getFrame().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
	}
}