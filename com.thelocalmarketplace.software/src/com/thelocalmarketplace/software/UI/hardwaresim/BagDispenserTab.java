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
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.bag.IReusableBagDispenser;
import com.jjjwelectronics.bag.ReusableBag;
import com.jjjwelectronics.bag.ReusableBagDispenserListener;

public class BagDispenserTab extends AbstractHardwareSimTab implements ReusableBagDispenserListener {
	
	private static final long serialVersionUID = -5254888691666944228L;
	
	JLabel label;

	public BagDispenserTab(int machineID) {
		super(machineID, 1);
		
		getHardware().getReusableBagDispenser().register(this);
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 1));
		panel.setBorder(new TitledBorder("Reusable Bag Dispenser"));
		
		label = new JLabel("Current Number of Bags:");
		
		panel.add(label);
		JButton refillButton = new JButton("Refill");
		refillButton.addActionListener(this::refill);
		panel.add(refillButton);
		add(panel);
		
		updateLabel();
	}
	
	public void refill(ActionEvent e) {
		IReusableBagDispenser dispenser = getHardware().getReusableBagDispenser();
		while(dispenser.getQuantityRemaining() < dispenser.getCapacity()) {
			try {
				dispenser.load(new ReusableBag());
			} catch (OverloadedDevice e1) {
				// This should never happen
			}
		}
	}
	
	private void updateLabel() {
		IReusableBagDispenser dispenser = getHardware().getReusableBagDispenser();
		label.setText("Current Number of Bags: " + dispenser.getQuantityRemaining());
		revalidate();
		repaint();
	}

	@Override
	public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {
	}

	@Override
	public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {
	}

	@Override
	public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {
	}

	@Override
	public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {
	}

	@Override
	public void aBagHasBeenDispensedByTheDispenser() {
		updateLabel();
	}

	@Override
	public void theDispenserIsOutOfBags() {
	}

	@Override
	public void bagsHaveBeenLoadedIntoTheDispenser(int count) {
		updateLabel();
	}
}