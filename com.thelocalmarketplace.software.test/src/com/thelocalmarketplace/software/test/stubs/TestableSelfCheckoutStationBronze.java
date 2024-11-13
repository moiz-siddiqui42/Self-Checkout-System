package com.thelocalmarketplace.software.test.stubs;

import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;

public class TestableSelfCheckoutStationBronze extends SelfCheckoutStationBronze {

	public TestableSelfCheckoutStationBronze() {
		super();
		screen.getFrame().dispose();
	}
	
}
