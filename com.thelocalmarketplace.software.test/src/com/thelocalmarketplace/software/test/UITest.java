package com.thelocalmarketplace.software.test;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import com.thelocalmarketplace.software.Software;
import com.thelocalmarketplace.software.UI.Simulation;
import com.thelocalmarketplace.software.UI.components.TransactionView;
import com.thelocalmarketplace.software.UI.hardwaresim.BanknoteSystemTab;
import com.thelocalmarketplace.software.UI.hardwaresim.components.BarcodedProductDatabaseTab;
import com.thelocalmarketplace.software.UI.hardwaresim.components.CardIssuerTabDetails;
import com.thelocalmarketplace.software.UI.user.screens.AddingBagsScreen;
import com.thelocalmarketplace.software.UI.user.screens.DisabledScreen;
import com.thelocalmarketplace.software.UI.user.screens.PrintingScreen;
import com.thelocalmarketplace.software.UI.user.screens.ReadyForItemScreen;
import com.thelocalmarketplace.software.UI.user.screens.ReadyForPaymentScreen;
import com.thelocalmarketplace.software.UI.user.screens.SystemErrorScreen;
import com.thelocalmarketplace.software.UI.user.screens.WaitingForAttendantScreen;
import com.thelocalmarketplace.software.UI.user.screens.WaitingForBaggingScreen;
import com.thelocalmarketplace.software.UI.user.screens.WelcomeScreen;
import com.thelocalmarketplace.software.payment.BankDataBase;

public class UITest {
	
	@Before
	public void before() {
		BankDataBase.uninitialize();
		Software.uninitialize();
	}
	
	@Test
	public void testSimulationUINoExceptions() {
		Simulation.main(new String[]{});
		Software.getInstance().startNewSession(0);
		
		new AddingBagsScreen(0);
		new DisabledScreen(0);
		new PrintingScreen(0);
		new ReadyForItemScreen(0);
		new ReadyForPaymentScreen(0);
		new SystemErrorScreen(0);
		new WaitingForAttendantScreen(0);
		new WaitingForBaggingScreen(0);
		new WelcomeScreen(0);
	}
	
	@Test
	public void testNoUIComponentsCrash() {
		Simulation.main(new String[]{});
		new TransactionView(0);
		new CardIssuerTabDetails("test");
	}
	
	@Test
	public void TestBanknoteSystem() {
		Simulation.main(new String[]{});
		BanknoteSystemTab tab = new BanknoteSystemTab(0);
		tab.insertBanknote(BigDecimal.TEN);
		tab.reloadDispenser(BigDecimal.TEN);
		tab.emitDispenser(BigDecimal.TEN);
		tab.emptyStorageUnit();
		tab.releaseBanknoteOutput(null);
		tab.updateBanknoteOutput(null);
	}
	
}
