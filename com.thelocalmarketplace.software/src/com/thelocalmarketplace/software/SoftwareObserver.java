package com.thelocalmarketplace.software;

public interface SoftwareObserver {
	void onSessionStart();
	void onSessionEnd();
	
	void onMachineDisabled();
	void onMachineEnabled();
}
