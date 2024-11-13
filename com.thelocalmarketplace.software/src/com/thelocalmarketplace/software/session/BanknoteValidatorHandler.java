package com.thelocalmarketplace.software.session;

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

import java.math.BigDecimal;
import java.util.Currency;

import com.tdc.IComponent;
import com.tdc.IComponentObserver;
import com.tdc.banknote.BanknoteValidator;
import com.tdc.banknote.BanknoteValidatorObserver;
import com.thelocalmarketplace.software.state.UserSessionState;

public class BanknoteValidatorHandler extends AbstractUserSessionHandler implements BanknoteValidatorObserver {

	public BanknoteValidatorHandler(UserSession session) {
		super(session);
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
	public void goodBanknote(BanknoteValidator validator, Currency currency, BigDecimal denomination) {
		UserSessionState newState = getUserSession().getState().onBanknoteInserted(getUserSession(), denomination);
		if(newState != null) {
			getUserSession().setState(newState); 
		}
	}

	@Override
	public void badBanknote(BanknoteValidator validator) {
		// TODO Auto-generated method stub

	}

}
