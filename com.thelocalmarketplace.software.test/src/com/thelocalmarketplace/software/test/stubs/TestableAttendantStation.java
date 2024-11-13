package com.thelocalmarketplace.software.test.stubs;

import com.thelocalmarketplace.hardware.AttendantStation;

public class TestableAttendantStation extends AttendantStation {

	public TestableAttendantStation() {
		super();
		screen.getFrame().dispose();
	}
}
