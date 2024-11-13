package com.thelocalmarketplace.software.UI.components;

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

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.lang.reflect.InvocationTargetException;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;

public class WrappedJComponent<T extends Component> extends JPanel {
	private static final long serialVersionUID = 7945193115284784435L;
	private T component;

	
	public static <U extends Component> WrappedJComponent<U> create(Class<U> clazz, Object...args) {
		Object[] constructorArgs = new Object[args.length / 2];
		Class<?>[] constructorTypes = new Class<?>[args.length / 2];
		
		for(int i = 0; i < args.length/2; i ++) {
			constructorTypes[i] = (Class<?>) args[2 * i];
			constructorArgs[i] = args[2 * i + 1];
		}
		
		return new WrappedJComponent<U>(clazz, constructorArgs, constructorTypes);
	}

	public WrappedJComponent(Class<T> clazz, Object[] args, Class<?>[] types) {
		setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
		try {
			component = clazz.getConstructor(types).newInstance(args);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		add(component, gbc);
	}

	public T getComponent() {
		return component;
	}
	
}
