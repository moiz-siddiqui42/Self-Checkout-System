package com.thelocalmarketplace.software.UI.hardwaresim;

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

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.software.Software;

public abstract class AbstractHardwareSimTab extends JPanel {
	
	private static final long serialVersionUID = 5053495616877039194L;
	private int machineId;
	
	public AbstractHardwareSimTab(int machineId, int columns) {
		this.machineId = machineId;
		setLayout(new GridLayout(0, columns, 20, 20));
		setBorder(new EmptyBorder(10, 10, 10, 10));
	}
	
	public AbstractSelfCheckoutStation getHardware() {
		return Software.getInstance().getHardware(machineId);
	}
}
