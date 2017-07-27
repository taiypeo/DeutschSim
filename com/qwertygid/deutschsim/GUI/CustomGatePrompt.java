package com.qwertygid.deutschsim.GUI;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

public class CustomGatePrompt {
	public CustomGatePrompt(final JFrame frame) {
		this.frame = frame;
		panel = new JPanel(new BorderLayout());
		
		JTabbedPane tabbed_pane = new JTabbedPane(); //?
		
		JPanel rotation_panel = new JPanel();
		rotation_panel.setLayout(new BoxLayout(rotation_panel, BoxLayout.Y_AXIS));
		
		JTextField x_angle = new JTextField();
		rotation_panel.add(x_angle);
		
		JTextField y_angle = new JTextField();
		rotation_panel.add(y_angle);

		JTextField z_angle = new JTextField();
		rotation_panel.add(z_angle);
		
		JPanel radio_buttons_panel = new JPanel();
		radio_buttons_panel.setLayout(new BoxLayout(radio_buttons_panel, BoxLayout.X_AXIS));
		
		ButtonGroup group = new ButtonGroup();
		
		JRadioButton degrees = new JRadioButton("Degrees", true);
		group.add(degrees);
		radio_buttons_panel.add(degrees);
		
		JRadioButton radians = new JRadioButton("Radians", false);
		group.add(radians);
		radio_buttons_panel.add(radians);
		
		rotation_panel.add(radio_buttons_panel);
		
		tabbed_pane.add("Rotation", rotation_panel);
		
		panel.add(tabbed_pane);
	}
	
	public void show() {
		JOptionPane.showOptionDialog(frame, panel, "Create Custom Gate", JOptionPane.OK_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, new Object[] {"Create Gate"}, null);
	}
	
	private final JFrame frame;
	private final JPanel panel;
}
