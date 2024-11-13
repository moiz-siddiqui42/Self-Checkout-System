package com.thelocalmarketplace.software.session;

import com.thelocalmarketplace.software.state.UserSessionState;

public interface SessionObserver {
	void onStateChanged(UserSessionState newState);
}
