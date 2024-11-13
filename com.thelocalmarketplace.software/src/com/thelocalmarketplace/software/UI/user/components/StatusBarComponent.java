package com.thelocalmarketplace.software.UI.user.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

public class StatusBarComponent extends JPanel {

	private static final long serialVersionUID = -4285786195969605003L;
	
	private JLabel statusLabel;
	private JButton btn;
	
	private Color background;
	
	public StatusBarComponent(ActionListener onSelectAddItem) {
		background = getBackground();
		
		setLayout(new GridBagLayout());
		setBackground(new Color(128, 128, 128));
		
		statusLabel = new JLabel();
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		
		add(statusLabel, gbc);

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.EAST;
		
		btn = new JButton("Search or Key in Item");
		btn.addActionListener(onSelectAddItem);
		
		add(btn, gbc);
	}
	
	public void setErrorStatus(String message) {
		btn.setEnabled(false);
		statusLabel.setText(message);
		setBorder(new CompoundBorder(new MatteBorder(new Insets(0, 10, 10, 10), background), new MatteBorder(new Insets(5, 0, 0, 0), Color.RED)));

		revalidate();
		repaint();
	}
	
	public void setInfoStatus(String message) {
		btn.setEnabled(false);
		statusLabel.setText(message);
		setBorder(new CompoundBorder(new MatteBorder(new Insets(0, 10, 10, 10), background), new MatteBorder(new Insets(5, 0, 0, 0), Color.BLUE)));

		revalidate();
		repaint();
	}
	
	public void setNormalStatus() {
		btn.setEnabled(true);
		statusLabel.setText("Please scan your next item");
		setBorder(new CompoundBorder(new MatteBorder(new Insets(0, 10, 10, 10), background), new MatteBorder(new Insets(5, 0, 0, 0), Color.GREEN)));

		revalidate();
		repaint();
	}

}
