package com.thelocalmarketplace.software.UI.hardwaresim;

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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.border.TitledBorder;

import com.thelocalmarketplace.software.UI.components.WrappedJComponent;
import com.thelocalmarketplace.software.membership.Membership;
import com.thelocalmarketplace.software.membership.MembershipDatabase;

public class MembershipDatabaseTab extends AbstractAttendantTab implements ListCellRenderer<Long> {

	private static final long serialVersionUID = -7987102304845860524L;

	JList<Long> list;
	DefaultListModel<Long> listModel;
	
	WrappedJComponent<JTextField> name;
	
	public MembershipDatabaseTab() {
		super(2);
		
		listModel = new DefaultListModel<Long>();
		list = new JList<Long>(listModel);
		list.setCellRenderer(this);
		
		name = WrappedJComponent.create(JTextField.class, Integer.TYPE, 20);

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(new TitledBorder("Create new Entry"));
		JPanel entries = new JPanel();
		entries.setLayout(new GridLayout(0, 2, 20, 20));
		entries.add(new JLabel("Name:"));
		entries.add(name);
		panel.add(entries);
		JButton add = new JButton("Add to Database");
		add.addActionListener(this::handleNewMembership);
		JButton query = new JButton("Query Selected");
		query.addActionListener(this::querySelected);
		panel.add(add);
		panel.add(query);
		
		add(list);
		add(panel);
		
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends Long> list, Long value, int index,
			boolean isSelected, boolean cellHasFocus) {
		JPanel panel = new JPanel();
		if(isSelected) {
			panel.setBackground(list.getSelectionBackground());
		} else {
			panel.setBackground(list.getBackground());
		}
		panel.setLayout(new GridLayout(0, 2));
		panel.add(new JLabel(value.toString()));
		panel.add(new JLabel(MembershipDatabase.getInstance().getMemberName(value)));
		return panel;
	}
	
	private void handleNewMembership(ActionEvent e) {
		String name = this.name.getComponent().getText();
		long id = MembershipDatabase.getInstance().addNewMembership(name);
		if(id == -1) return;
		listModel.addElement(id);
		revalidate();
		repaint();
	}
	
	private void querySelected(ActionEvent e) {
		long selected = list.getSelectedValue();
		long points = MembershipDatabase.getInstance().getMemberPoints(selected);
		JOptionPane.showMessageDialog(null, "Points: " + points, "Member #" + selected, JOptionPane.INFORMATION_MESSAGE);
	}
}
