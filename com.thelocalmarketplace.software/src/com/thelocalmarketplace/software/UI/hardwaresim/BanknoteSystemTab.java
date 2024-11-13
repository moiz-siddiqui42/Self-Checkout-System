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
import com.tdc.banknote.Banknote;
import com.tdc.banknote.BanknoteDispensationSlot;
import com.tdc.banknote.BanknoteDispenserObserver;
import com.tdc.banknote.IBanknoteDispenser;
import com.thelocalmarketplace.software.Software;
import com.thelocalmarketplace.software.UI.components.ErrorPopup;

import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import ca.ucalgary.seng300.simulation.SimulationException;

public class BanknoteSystemTab extends AbstractHardwareSimTab implements BanknoteDispenserObserver {
	private static final long serialVersionUID = -7616750139837556826L;
	
	private DefaultListModel<Banknote> collectedBanknoteModel;
	private Map<BigDecimal, JLabel> dispenserLabels;
	
	private JLabel countLabel;
	private JLabel isDangling;

	public BanknoteSystemTab(int machineId) {
		super(machineId, 2);
		
		dispenserLabels = new HashMap<BigDecimal, JLabel>();
		
		JPanel masterBanknoteInputPanel = new JPanel();
		masterBanknoteInputPanel.setLayout(new GridLayout(0, 1));
		JPanel banknoteInputPanel = new JPanel();
		banknoteInputPanel.setLayout(new FlowLayout());
		
		JPanel banknoteDispenserPanel = new JPanel();
		banknoteDispenserPanel.setLayout(new GridLayout(0, 4));
		
		JScrollPane banknoteInputScrollPane = new JScrollPane(banknoteInputPanel);
		banknoteInputScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		banknoteInputScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		banknoteInputScrollPane.setBorder(new TitledBorder("Banknote Input"));
		
		JScrollPane banknoteDispenserScrollPane = new JScrollPane(banknoteDispenserPanel);
		banknoteDispenserScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		banknoteDispenserScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		banknoteDispenserScrollPane.setBorder(new TitledBorder("Banknote Dispensers"));
		
		for(BigDecimal denomination : Software.getInstance().getConfiguration().banknoteDenominations) {
			getHardware().getBanknoteDispensers().get(denomination).attach(this);
			
			JButton btn = new JButton("$" + denomination.toPlainString());
			btn.addActionListener((e) -> this.insertBanknote(denomination));
			banknoteInputPanel.add(btn);
			
			JLabel label = new JLabel("$" + denomination.toPlainString());
			JLabel amount = new JLabel("0");
			JButton reloadBtn = new JButton("reload");
			reloadBtn.addActionListener((e) -> this.reloadDispenser(denomination));
			JButton emitBtn = new JButton("emit");
			emitBtn.addActionListener((e) -> this.emitDispenser(denomination));
			banknoteDispenserPanel.add(label);
			banknoteDispenserPanel.add(amount);
			banknoteDispenserPanel.add(reloadBtn);
			banknoteDispenserPanel.add(emitBtn);
			dispenserLabels.put(denomination, amount);
		}
		updateBanknoteDispensers();
		
		masterBanknoteInputPanel.add(banknoteInputScrollPane);
		
		JButton removeDanglingButton = new JButton("Remove Dangling");
		removeDanglingButton.addActionListener(this::removeDanglingFromInput);
		isDangling = new JLabel("false");
		
		JPanel inputDangling = new JPanel();
		
		inputDangling.add(new JLabel("Has Dangling banknote: "));
		inputDangling.add(isDangling);
		inputDangling.add(removeDanglingButton);
		
		masterBanknoteInputPanel.add(inputDangling);
		
		add(masterBanknoteInputPanel);
		
		JPanel banknoteOutputPanel = new JPanel();
		banknoteOutputPanel.setBorder(new TitledBorder("Banknote Output"));
		banknoteOutputPanel.setLayout(new GridLayout(1, 2));
		collectedBanknoteModel = new DefaultListModel<Banknote>();
		JList<Banknote> collectedBanknoteList = new JList<>(collectedBanknoteModel);
		JScrollPane collectedBanknoteScrollPane = new JScrollPane(collectedBanknoteList);
		JPanel outputControlPanel = new JPanel();
		outputControlPanel.setLayout(new GridLayout(0, 1));
		JButton releaseButton = new JButton("Release Banknotes");
		JButton collectButton = new JButton("Collect Banknotes");
		releaseButton.addActionListener(this::releaseBanknoteOutput);
		collectButton.addActionListener(this::updateBanknoteOutput);
		
		outputControlPanel.add(releaseButton);
		outputControlPanel.add(collectButton);
		
		banknoteOutputPanel.add(collectedBanknoteScrollPane);
		banknoteOutputPanel.add(outputControlPanel);
		
		add(banknoteOutputPanel);
		
		add(banknoteDispenserScrollPane);
		
		JPanel banknoteStoragePanel = new JPanel();
		banknoteStoragePanel.setLayout(new GridLayout(0, 2));
		banknoteStoragePanel.setBorder(new TitledBorder("Banknote Storage"));
		banknoteStoragePanel.add(new JLabel("Banknote Count: "));
		countLabel = new JLabel("");
		updateStorageCount();
		banknoteStoragePanel.add(countLabel);
		banknoteStoragePanel.add(new JLabel("Banknote Capacity: "));
		JLabel capcityLabel = new JLabel("" + Software.getInstance().getConfiguration().banknoteStorageCapacity);
		banknoteStoragePanel.add(capcityLabel);
		JButton emptyButton = new JButton("Empty Storage");
		emptyButton.addActionListener((e) -> emptyStorageUnit());
		banknoteStoragePanel.add(emptyButton);
		
		add(banknoteStoragePanel);
	}
	
	public void insertBanknote(BigDecimal denomination) {
		Currency currency = Software.getInstance().getConfiguration().currency;
		Banknote banknote = new Banknote(currency, denomination);
		try {
			getHardware().getBanknoteInput().receive(banknote);
		} catch (DisabledException e) {
			ErrorPopup.showError("Failed to insert banknote", "The banknote input is disabled.");
		} catch(CashOverloadException e) {
			ErrorPopup.showError("Failed to insert banknote", "The banknote input is overloaded.");
		} catch(RuntimeException e) {}
		updateStorageCount();
		updateInputDanglingStatus();
	}
	
	public void removeDanglingFromInput(ActionEvent e) {
		try {
			getHardware().getBanknoteInput().removeDanglingBanknote();
		} catch(NullPointerSimulationException e1) {
			ErrorPopup.showError("Failed to collect dangling banknotes", "There are no banknotes dangling to collect!");
		}
		updateInputDanglingStatus();
	}
	
	public void updateInputDanglingStatus() {
		boolean dangling = getHardware().getBanknoteInput().hasDanglingBanknotes();
		isDangling.setText("" + dangling);
	}
	
	public void updateBanknoteOutput(ActionEvent e) {
		BanknoteDispensationSlot slot = getHardware().getBanknoteOutput();
		try {
			List<Banknote> collected = slot.removeDanglingBanknotes();
			collectedBanknoteModel.clear();
			collectedBanknoteModel.addAll(collected);
		} catch(NullPointerSimulationException e1) {
			ErrorPopup.showError("Failed to collect dangling banknotes", "There are no banknotes dangling to collect!");
		}
	}
	
	public void releaseBanknoteOutput(ActionEvent e) {
		BanknoteDispensationSlot slot = getHardware().getBanknoteOutput();
		try {
			slot.dispense();
		} catch(Exception e1) {
			ErrorPopup.showError("Failed to release", "Please collect the dangling banknotes first!");
		}
	}
	
	public void reloadDispenser(BigDecimal denomination) {
		Currency currency = Software.getInstance().getConfiguration().currency;
		IBanknoteDispenser dispenser = getHardware().getBanknoteDispensers().get(denomination);
		while(dispenser.size() < dispenser.getCapacity()) {
			try {
				dispenser.load(new Banknote(currency, denomination));
			} catch (SimulationException | CashOverloadException e) {
				// This should never happen
				e.printStackTrace();
			}
		}
		updateBanknoteDispensers();
	}
	
	public void emitDispenser(BigDecimal denomination) {
		IBanknoteDispenser dispenser = getHardware().getBanknoteDispensers().get(denomination);
		
		try {
			dispenser.emit();
		} catch (DisabledException e) {
			ErrorPopup.showError("Failed to Dispsense Banknote", "The banknote dispenser is disabled.");
		} catch (CashOverloadException e) {
			ErrorPopup.showError("Failed to Dispsense Banknote", "The banknote dispenser is overloaded.");
		} catch (NoCashAvailableException e) {
			ErrorPopup.showError("Failed to Dispsense Banknote", "The banknote dispenser is empty!");
		}
		updateBanknoteDispensers();
	}
	
	public void updateBanknoteDispensers() {
		for(BigDecimal denomination : dispenserLabels.keySet()) {
			IBanknoteDispenser dispenser = getHardware().getBanknoteDispensers().get(denomination);
			int count = dispenser.size();
			dispenserLabels.get(denomination).setText(count + "");
		}
	}
	
	public void updateStorageCount() {
		int count = getHardware().getBanknoteStorage().getBanknoteCount();
		countLabel.setText("" + count);
	}
	
	public void emptyStorageUnit() {
		getHardware().getBanknoteStorage().unload();
		updateStorageCount();
	}

	@Override
	public void enabled(IComponent<? extends IComponentObserver> component) {
	}

	@Override
	public void disabled(IComponent<? extends IComponentObserver> component) {
	}

	@Override
	public void turnedOn(IComponent<? extends IComponentObserver> component) {
	}

	@Override
	public void turnedOff(IComponent<? extends IComponentObserver> component) {
	}

	@Override
	public void moneyFull(IBanknoteDispenser dispenser) {
	}

	@Override
	public void banknotesEmpty(IBanknoteDispenser dispenser) {
	}

	@Override
	public void banknoteAdded(IBanknoteDispenser dispenser, Banknote banknote) {
		updateBanknoteDispensers();
	}

	@Override
	public void banknoteRemoved(IBanknoteDispenser dispenser, Banknote banknote) {
		updateBanknoteDispensers();
	}

	@Override
	public void banknotesLoaded(IBanknoteDispenser dispenser, Banknote... banknotes) {
		updateBanknoteDispensers();
	}

	@Override
	public void banknotesUnloaded(IBanknoteDispenser dispenser, Banknote... banknotes) {
		updateBanknoteDispensers();
	}
}