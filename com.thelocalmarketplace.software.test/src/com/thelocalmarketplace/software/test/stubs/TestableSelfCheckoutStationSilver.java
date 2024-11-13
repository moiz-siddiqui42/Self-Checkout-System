package com.thelocalmarketplace.software.test.stubs;

import com.thelocalmarketplace.hardware.SelfCheckoutStationSilver;

public class TestableSelfCheckoutStationSilver extends SelfCheckoutStationSilver {

	public TestableSelfCheckoutStationSilver() {
		super();
		screen.getFrame().dispose();
	}
}
