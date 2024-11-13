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

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;

import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.IComponent;
import com.tdc.IComponentObserver;
import com.tdc.NoCashAvailableException;
import com.tdc.coin.Coin;
import com.tdc.coin.CoinDispenserObserver;
import com.tdc.coin.ICoinDispenser;
import com.thelocalmarketplace.hardware.CoinTray;
import com.thelocalmarketplace.software.Software;
import com.thelocalmarketplace.software.UI.components.ErrorPopup;

import ca.ucalgary.seng300.simulation.SimulationException;

public class CoinSystemTab extends AbstractHardwareSimTab implements CoinDispenserObserver {
	private static final long serialVersionUID = -7616750139837556826L;
	
	private DefaultListModel<Coin> collectedCoinModel;
	private Map<BigDecimal, JLabel> dispenserLabels;
	
	private JLabel countLabel;

	public CoinSystemTab(int machineId) {
		super(machineId, 2);
		
		dispenserLabels = new HashMap<BigDecimal, JLabel>();
		
		JPanel coinSlotPanel = new JPanel();
		coinSlotPanel.setLayout(new FlowLayout());
		
		JPanel coinDispenserPanel = new JPanel();
		coinDispenserPanel.setLayout(new GridLayout(0, 4));
		
		JScrollPane coinSlotScrollPane = new JScrollPane(coinSlotPanel);
		coinSlotScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		coinSlotScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		coinSlotScrollPane.setBorder(new TitledBorder("Coin Slot"));
		
		JScrollPane coinDispenserScrollPane = new JScrollPane(coinDispenserPanel);
		coinDispenserScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		coinDispenserScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		coinDispenserScrollPane.setBorder(new TitledBorder("Coin Dispensers"));
		
		for(BigDecimal denomination : Software.getInstance().getConfiguration().coinDenominations) {
			getHardware().getCoinDispensers().get(denomination).attach(this);
			JButton btn = new JButton("$" + denomination.toPlainString());
			btn.addActionListener((e) -> this.insertCoin(denomination));
			coinSlotPanel.add(btn);
			
			JLabel label = new JLabel("$" + denomination.toPlainString());
			JLabel amount = new JLabel("0");
			JButton reloadBtn = new JButton("reload");
			reloadBtn.addActionListener((e) -> this.reloadDispenser(denomination));
			JButton emitBtn = new JButton("emit");
			emitBtn.addActionListener((e) -> this.emitDispenser(denomination));
			coinDispenserPanel.add(label);
			coinDispenserPanel.add(amount);
			coinDispenserPanel.add(reloadBtn);
			coinDispenserPanel.add(emitBtn);
			dispenserLabels.put(denomination, amount);
		}
		updateCoinDispensers();
		
		add(coinSlotScrollPane);
		
		JPanel coinTrayPanel = new JPanel();
		coinTrayPanel.setBorder(new TitledBorder("Coin Tray"));
		coinTrayPanel.setLayout(new GridLayout(1, 2));
		collectedCoinModel = new DefaultListModel<Coin>();
		JList<Coin> collectedCoinList = new JList<>(collectedCoinModel);
		JScrollPane collectedCoinScrollPane = new JScrollPane(collectedCoinList);
		JButton collectButton = new JButton("Collect Coins");
		collectButton.addActionListener(this::updateCoinTray);
		
		coinTrayPanel.add(collectedCoinScrollPane);
		coinTrayPanel.add(collectButton);
		
		add(coinTrayPanel);
		
		add(coinDispenserScrollPane);
		
		JPanel coinStoragePanel = new JPanel();
		coinStoragePanel.setLayout(new GridLayout(0, 2));
		coinStoragePanel.setBorder(new TitledBorder("Coin Storage"));
		coinStoragePanel.add(new JLabel("Coin Count: "));
		countLabel = new JLabel("");
		updateStorageCount();
		coinStoragePanel.add(countLabel);
		coinStoragePanel.add(new JLabel("Coin Capacity: "));
		JLabel capcityLabel = new JLabel("" + Software.getInstance().getConfiguration().coinStorageUnitCapacity);
		coinStoragePanel.add(capcityLabel);
		JButton emptyButton = new JButton("Empty Storage");
		emptyButton.addActionListener((e) -> emptyStorageUnit());
		coinStoragePanel.add(emptyButton);
		
		add(coinStoragePanel);
	}
	
	public void insertCoin(BigDecimal denomination) {
		Currency currency = Software.getInstance().getConfiguration().currency;
		Coin coin = new Coin(currency, denomination);
		try {
			getHardware().getCoinSlot().receive(coin);
		} catch (DisabledException e) {
			ErrorPopup.showError("Failed to insert coin", "The coin slot is disabled.");
		} catch(CashOverloadException e) {
			ErrorPopup.showError("Failed to insert coin", "The coin slot is overloaded.");
		} catch(RuntimeException e) {}
		updateStorageCount();
		updateCoinDispensers();
	}
	
	public void updateCoinTray(ActionEvent e) {
		CoinTray tray = getHardware().getCoinTray();
		List<Coin> collected = tray.collectCoins();
		collectedCoinModel.clear();
		collectedCoinModel.addAll(collected);
	}
	
	public void reloadDispenser(BigDecimal denomination) {
		Currency currency = Software.getInstance().getConfiguration().currency;
		ICoinDispenser dispenser = getHardware().getCoinDispensers().get(denomination);
		while(dispenser.hasSpace()) {
			try {
				dispenser.load(new Coin(currency, denomination));
			} catch (SimulationException | CashOverloadException e) {
				// This should never happen
				e.printStackTrace();
			}
		}
		updateCoinDispensers();
	}
	
	public void emitDispenser(BigDecimal denomination) {
		ICoinDispenser dispenser = getHardware().getCoinDispensers().get(denomination);
		
		try {
			dispenser.emit();
		} catch (DisabledException e) {
			ErrorPopup.showError("Failed to Dispsense Coin", "The coin dispenser is disabled.");
		} catch (CashOverloadException e) {
			ErrorPopup.showError("Failed to Dispsense Coin", "The coin dispenser is overloaded.");
		} catch (NoCashAvailableException e) {
			ErrorPopup.showError("Failed to Dispsense Coin", "The coin dispenser is empty!");
		}
		updateCoinDispensers();
	}
	
	public void updateCoinDispensers() {
		for(BigDecimal denomination : dispenserLabels.keySet()) {
			ICoinDispenser dispenser = getHardware().getCoinDispensers().get(denomination);
			int count = dispenser.size();
			dispenserLabels.get(denomination).setText(count + "");
		}
	}
	
	public void updateStorageCount() {
		int count = getHardware().getCoinStorage().getCoinCount();
		countLabel.setText("" + count);
	}
	
	public void emptyStorageUnit() {
		getHardware().getCoinStorage().unload();
		updateStorageCount();
	}

	@Override
	public void enabled(IComponent<? extends IComponentObserver> component) {}

	@Override
	public void disabled(IComponent<? extends IComponentObserver> component) {}

	@Override
	public void turnedOn(IComponent<? extends IComponentObserver> component) {}

	@Override
	public void turnedOff(IComponent<? extends IComponentObserver> component) {}

	@Override
	public void coinsFull(ICoinDispenser dispenser) {}

	@Override
	public void coinsEmpty(ICoinDispenser dispenser) {}

	@Override
	public void coinAdded(ICoinDispenser dispenser, Coin coin) {
		updateCoinDispensers();
	}

	@Override
	public void coinRemoved(ICoinDispenser dispenser, Coin coin) {
		updateCoinDispensers();
	}

	@Override
	public void coinsLoaded(ICoinDispenser dispenser, Coin... coins) {
		updateCoinDispensers();
	}

	@Override
	public void coinsUnloaded(ICoinDispenser dispenser, Coin... coins) {
		updateCoinDispensers();
	}
}