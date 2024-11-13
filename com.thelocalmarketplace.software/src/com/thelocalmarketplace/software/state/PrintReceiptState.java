package com.thelocalmarketplace.software.state;

import java.util.ArrayList;

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

import com.jjjwelectronics.EmptyDevice;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.printer.IReceiptPrinter;
import com.thelocalmarketplace.software.Software;
import com.thelocalmarketplace.software.payment.IPayment;
import com.thelocalmarketplace.software.payment.Transaction;
import com.thelocalmarketplace.software.payment.TransactionItem;
import com.thelocalmarketplace.software.session.UserSession;

public class PrintReceiptState implements IUserSessionState<UserSessionState> {
	// create a map that holds pointers for each machine printer, index corresponds to machine ID 
	public static ArrayList<Integer[]> machinePointers; 
	private Transaction finalTransactionRecord;
	private ArrayList<String> itemizedTransaction;
	private IReceiptPrinter hardwarePrinter;

	/**
     * Since the receipt to be printed as soon as a full payment has been made
     * The onStateSetMethod should be doing most of the work for this state
     */
    @Override
    public UserSessionState onStateSet(UserSession session) {
        // Disable the coin slot to prevent the user from inserting a coin while the software
        // is not in the correct state
        session.getHardware().getCoinSlot().disable();
        session.getHardware().getBanknoteInput().disable();
        hardwarePrinter = session.getHardware().getPrinter();
        
        finalTransactionRecord = session.getTransaction();//prob add a null check just incase
        
        int totalCharsToPrint = findWorkingString(); 
        int itemChars = totalCharsToPrint; 
        
        itemizedTransaction.add("Payment Methods:");
        totalCharsToPrint += "Payment Methods:".length(); 
        // add all the payment methods as well 
        for(IPayment payment : finalTransactionRecord.getPayments()) {
        	// only add the method if hasn't been added yet, don't want it to print cash multiple times 
        	if(!itemizedTransaction.contains(payment.toString()) ) {
        		itemizedTransaction.add(payment.toString());
        		totalCharsToPrint += payment.toString().length();
        	}
        }

        //because the printer can know how many more chars and lines it has left we can probably
        //use totalCharToPrint to see if the receipt is even printable
        //assuming it is then move on to the rest
        try {
            if(hardwarePrinter.inkRemaining() < totalCharsToPrint){
                session.getReceiptPrinterHandler().thePrinterIsOutOfInk();
                return UserSessionState.PRINTER_NEEDS_REFILL;
            } else if (hardwarePrinter.paperRemaining() < (totalCharsToPrint/60)) {
                session.getReceiptPrinterHandler().thePrinterIsOutOfPaper();
                return UserSessionState.PRINTER_NEEDS_REFILL;
            }
        } catch (UnsupportedOperationException e){
            //this means we have the bronze receipt printer, so we just have to print the receipt and wait until it goes empty
        }

        if(itemChars > 0) {
        	return printOut(session);
        }
        hardwarePrinter.cutPaper();

        //After receipt printing the use case states the station should return to a ready state
        Software.getInstance().endCurrentSession(session.getMachineID());
        return null;
    }
    
    private int findWorkingString() {
    	itemizedTransaction = new ArrayList<String>();        
        String workingString = "";
        int totalCharsToPrint = 0;
        
        for (TransactionItem product : finalTransactionRecord.getItems()){ 
        	workingString = product.getDescription();
            workingString += " : ";
            workingString += product.getFormattedPrice();
            itemizedTransaction.add(workingString);
            String strippedString = workingString.replaceAll("\\s", "");
            totalCharsToPrint += strippedString.length();
        }

    	return totalCharsToPrint; 
    }
    
    
    private UserSessionState printOut(UserSession session) {
        //loop through the formatted customer transaction
    	Integer[] pair = machinePointers.get(session.getMachineID());
        for (; pair[0] < itemizedTransaction.size(); pair[0]++) {
        	// get the pair of the machine pointers
        	String barcodePriceString = itemizedTransaction.get(pair[0]);
            char[] charArray = barcodePriceString.toCharArray();
                for (; pair[1] < charArray.length; pair[1]++) {
                    char c = charArray[pair[1]]; 
                	try {
                    	
                        hardwarePrinter.print(c);
                         
                    }
                    catch(EmptyDevice empty){
                    	// its not possible to tell if its the ink or paper that ran out so set both flags
                    	if(empty.getMessage().equals("There is no paper in the printer.")) {
                        	session.getReceiptPrinterHandler().thePrinterIsOutOfPaper();	
                        }
                        if(empty.getMessage().equals("There is no ink in the printer")) {
                        	session.getReceiptPrinterHandler().thePrinterIsOutOfInk();	
                        }
                        return UserSessionState.PRINTER_NEEDS_REFILL;
                    }
                    catch(OverloadedDevice overload){
                        try {
                            hardwarePrinter.print('\n');
                            hardwarePrinter.print(c);
                        } catch (OverloadedDevice e) {
                            throw new RuntimeException(e);//if another error happens here ill be surprised
                        }
                        catch(EmptyDevice empty){
                            //its not possible to tell if its the ink or paper that ran out so set both flags
                            if(empty.getMessage().equals("There is no paper in the printer.")) {
                            	session.getReceiptPrinterHandler().thePrinterIsOutOfPaper();
                            	
                            }
                            if(empty.getMessage().equals("There is no ink in the printer")) {
                            	session.getReceiptPrinterHandler().thePrinterIsOutOfInk();	
                            }
                            
                            return UserSessionState.PRINTER_NEEDS_REFILL;
                        }

                    }
                }
                try{
                    hardwarePrinter.print('\n');//once an item has been printed out fully move to the next line
                    // reset the character pointer 
                    pair[1] = 0; 
                } catch (EmptyDevice e) {
                    //newline char doesn't use ink but will throw out of paper
                     session.getReceiptPrinterHandler().thePrinterIsOutOfPaper();
                    return UserSessionState.PRINTER_NEEDS_REFILL;
                } catch (OverloadedDevice e) {
                    //this really should never happen based on what the printer class looks like but
                    System.out.println("something bad happened in print receipt state");
                    throw new RuntimeException(e);
                }

        }
        hardwarePrinter.cutPaper();
        
        // if we are able to cut the paper then set the machinepointers back to zero 
        pair = new Integer[] {0,0}; 
        Software.getInstance().endCurrentSession(session.getMachineID());
        return null;
    }
    
}
