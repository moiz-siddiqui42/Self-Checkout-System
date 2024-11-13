package com.thelocalmarketplace.software.UI.components;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.TitledBorder;

import com.jjjwelectronics.Mass;
import com.thelocalmarketplace.software.Software;
import com.thelocalmarketplace.software.payment.IPayment;
import com.thelocalmarketplace.software.payment.Transaction;
import com.thelocalmarketplace.software.payment.TransactionItem;
import com.thelocalmarketplace.software.payment.TransactionObserver;
import com.thelocalmarketplace.software.session.UserSession;

public class TransactionView extends JPanel implements TransactionObserver, ListCellRenderer<TransactionItem> {
	private static final long serialVersionUID = 2859060838426821981L;
	
	DefaultListModel<TransactionItem> model;
	JList<TransactionItem> list;
	
	private int machineID;
	
	public TransactionView(int machineID) {
		this.machineID = machineID;
		setLayout(new GridLayout(1, 1));
		model = new DefaultListModel<TransactionItem>();
		list = new JList<TransactionItem>(model);
		list.setCellRenderer(this);
		
		setBorder(new TitledBorder("Test"));
		
		add(list);
	}
	
	public void connect(Transaction transaction) {
		transaction.register(this);
		model.removeAllElements();
		
		for(TransactionItem product : transaction.getItems()) {
			model.addElement(product);
		}
	}
	
	public void disconnect(Transaction transaction) {
		transaction.deregister(this);
	}

	@Override
	public void itemAdded(TransactionItem item) {
		model.addElement(item);
	}

	@Override
	public void itemRemoved(TransactionItem item) {
		model.removeElement(item);
	}

	@Override
	public void paymentAdded(IPayment payment) {
	}

	@Override
	public void bagAdded(Mass bagMass) {
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends TransactionItem> list, TransactionItem value,
			int index, boolean isSelected, boolean cellHasFocus) {
		JPanel panel = new JPanel();
		if(isSelected) {
			panel.setBackground(list.getSelectionBackground());
		} else {
			panel.setBackground(list.getBackground());
		}
		panel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		panel.add(new JLabel(value.getDescription()), gbc);

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 0;
		gbc.anchor = GridBagConstraints.CENTER;
		panel.add(new JLabel(value.getFormattedPrice()), gbc);
		
		return panel;
	}
	
	public void deleteSelected() {
		TransactionItem item = list.getSelectedValue();
		if(item == null) return;
		
		UserSession session = Software.getInstance().getCurrentSession(machineID);
		session.getUIHandler().removeItemSelected(item);
	}
}