package com.thelocalmarketplace.software.UI.Attendant;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import com.jjjwelectronics.DisabledDevice;
import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.keyboard.Key;
import com.jjjwelectronics.keyboard.KeyboardListener;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.software.Software;
import com.thelocalmarketplace.software.session.AttendantKeyboardHandler;

public class AddItemPanel extends JPanel implements KeyboardListener{

	private static final long serialVersionUID = -297557288554678948L;

	private int machineID;
	
	private DefaultListModel<String> model = new DefaultListModel<String>();
	private JList<String> results = new JList<>(model);
	private AttendantKeyboardHandler handler;
	private JLabel input;
	
	private KeyEventDispatcher dispatcher;
	
	public AddItemPanel(int machineID, Component parent) {
		setLayout(new GridLayout(0, 1));
		Software.getInstance().startReadKeyboard(machineID);
		handler = Software.getInstance().getKeyboardHandler();
		Software.getInstance().getAttendantStation().keyboard.register(this);
		
		dispatcher = new KeyEventDispatcher() {
			
			@Override
			public boolean dispatchKeyEvent(KeyEvent e) {
				if(e.getID() != KeyEvent.KEY_RELEASED) return false;
				// TODO Auto-generated method stub
				String keyCode = e.getKeyChar() + "";
				keyCode = keyCode.toUpperCase();
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					keyCode = "Enter";
				} else if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
					keyCode = "Backspace";
				}
				Key key = Software.getInstance().getAttendantStation().keyboard.getKey(keyCode);
				
				if(key == null) return false;
				
				try {
					key.press();
					key.release();
				} catch (DisabledDevice e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				return true;
			}
		};
		
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(dispatcher);
		
		input = new JLabel("");
		add(input);
		add(results);
		
		redraw();
	}
	
	public void redraw() {
		model.removeAllElements();
		if(handler.getMatchingItems() != null) {
			for(Product product : handler.getMatchingItems()) {
				if(product instanceof BarcodedProduct) {
					model.addElement(((BarcodedProduct) product).getDescription());
				} else {
					model.addElement(((PLUCodedProduct) product).getDescription());
				}
			}
		}
		
		input.setText(handler.getInput());
		
		revalidate();
		repaint();
	}
	
	public void OnBack() {
		Software.getInstance().stopReadKeyboard();
		Software.getInstance().getAttendantStation().keyboard.deregister(this);
		KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(dispatcher);
	}

	@Override
	public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void aKeyHasBeenPressed(String label) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void aKeyHasBeenReleased(String label) {
		redraw();
	}
	
}
