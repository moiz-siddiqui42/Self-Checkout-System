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

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.printer.ReceiptPrinterListener;
import com.thelocalmarketplace.software.state.UserSessionState;

public class ReceiptPrinterHandler extends AbstractUserSessionHandler implements ReceiptPrinterListener {

    private boolean fillInkFlag = false;
    private boolean fillPaperFlag = false;

    public ReceiptPrinterHandler(UserSession session) { super(session); }

    @Override
    public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {

    }

    @Override
    public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {

    }

    @Override
    public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {

    }

    @Override
    public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {

    }

    @Override
    public void thePrinterIsOutOfPaper() {
        fillPaperFlag = true;
    }

    @Override
    public void thePrinterIsOutOfInk() {
        fillInkFlag = true;
    }

    @Override
    public void thePrinterHasLowInk() {
        fillInkFlag = true;
    }

    @Override
    public void thePrinterHasLowPaper() {
        fillPaperFlag = true;
    }

    @Override
    public void paperHasBeenAddedToThePrinter() {
        fillPaperFlag = false;
        if(!refillFlagsSet()){
        	UserSessionState newState  = getUserSession().getState().onPrinterRefilled(getUserSession());
            if(newState != null) {
            	getUserSession().setState(newState);
            }
        }
    }

    @Override
    public void inkHasBeenAddedToThePrinter() {
        fillInkFlag = false;
        if(!refillFlagsSet()){
            UserSessionState newState  = getUserSession().getState().onPrinterRefilled(getUserSession());
            if(newState != null) {
            	getUserSession().setState(newState);
            }
        }
    }

    /**
     * checks to see if the printer need refilling
     * @return true if needs refill flags are set
     */
    public boolean refillFlagsSet(){
        if (fillInkFlag | fillPaperFlag){
            return true;
        }
        else {
            return false;
        }
    }
}
