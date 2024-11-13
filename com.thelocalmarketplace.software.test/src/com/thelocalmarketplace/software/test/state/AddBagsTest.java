package com.thelocalmarketplace.software.test.state;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.Item;
import com.jjjwelectronics.Mass;
import com.thelocalmarketplace.software.SelfCheckoutConfiguration;
import com.thelocalmarketplace.software.Software;
import com.thelocalmarketplace.software.session.UserSession;
import com.thelocalmarketplace.software.state.UserSessionState;
import com.thelocalmarketplace.software.test.stubs.TestableAttendantStation;
import com.thelocalmarketplace.software.test.stubs.TestableSelfCheckoutStationGold;

import powerutility.PowerGrid;

public class AddBagsTest {
	
	UserSession session;
	
	@Before
	public void setup() {
		PowerGrid.engageUninterruptiblePowerSource();
		Software.uninitialize();
		Software.initialize(new SelfCheckoutConfiguration(TestableSelfCheckoutStationGold.class, TestableAttendantStation.class), 1);
		session = Software.getInstance().startNewSession(0);
	}
	
	@Test
	public void TestDontAddBags() {
		session.getUIHandler().addBagSelected();
		
		assertEquals(UserSessionState.ADDING_BAGS_STATE, session.getState());
		
		session.getUIHandler().doneAddingBagsSelected();
		
		assertEquals(UserSessionState.WAITING_FOR_BAGGING, session.getState());
	}
	
	@Test
	public void TestAddExpectedBag() {
		session.getUIHandler().addBagSelected();
		
		assertEquals(UserSessionState.ADDING_BAGS_STATE, session.getState());
		
		session.getHardware().getBaggingArea().addAnItem(new Item(new Mass(15d)) {}); // add expected 15g bag
		
		session.getUIHandler().doneAddingBagsSelected();
		
		assertEquals(UserSessionState.READY_FOR_ITEM, session.getState());
	}
	
	@Test
	public void TestAddInRangeBag() {
		session.getUIHandler().addBagSelected();
		
		assertEquals(UserSessionState.ADDING_BAGS_STATE, session.getState());
		
		session.getHardware().getBaggingArea().addAnItem(new Item(new Mass(9d)) {}); // add within range 9g bag
		
		session.getUIHandler().doneAddingBagsSelected();
		
		assertEquals(UserSessionState.READY_FOR_ITEM, session.getState());
	}
	
	@Test
	public void TestAddHeavyBag() {
		session.getUIHandler().addBagSelected();
		
		assertEquals(UserSessionState.ADDING_BAGS_STATE, session.getState());
		
		session.getHardware().getBaggingArea().addAnItem(new Item(new Mass(35d)) {}); // add within range 9g bag
		
		session.getUIHandler().doneAddingBagsSelected();
		
		assertEquals(UserSessionState.WAITING_FOR_BAGGING, session.getState());
	}
	
}
