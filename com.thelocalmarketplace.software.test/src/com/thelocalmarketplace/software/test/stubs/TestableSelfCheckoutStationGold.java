package com.thelocalmarketplace.software.test.stubs;

import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;

public class TestableSelfCheckoutStationGold extends SelfCheckoutStationGold {

	public TestableSelfCheckoutStationGold() {
		super();
		screen.getFrame().dispose();
	}
}
