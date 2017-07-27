package com.qwertygid.deutschsim.GUI;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

public class CustomGatePrompt {
	public CustomGatePrompt(final JFrame frame) {
		this.frame = frame;
		panel = new JPanel(new BorderLayout());
		
		JTabbedPane tabbed_pane = new JTabbedPane();		
		tabbed_pane.add("Rotation", create_rotation_panel());
		
		panel.add(tabbed_pane);
	}
	
	public void show() {
		JOptionPane.showOptionDialog(frame, panel, "Create Custom Gate", JOptionPane.OK_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, new Object[] {"Create Gate"}, null);
	}
	
	private JPanel create_rotation_panel() {
		JPanel rotation_panel = new JPanel();
		rotation_panel.setLayout(new BoxLayout(rotation_panel, BoxLayout.Y_AXIS));
		
		x_angle = add_one_line_input("X axis: ", rotation_panel);
		y_angle = add_one_line_input("Y axis: ", rotation_panel);
		z_angle = add_one_line_input("Z axis: ", rotation_panel);
		
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
		
		return rotation_panel;
	}
	
	private JTextField add_one_line_input(final String label_text, final JPanel rotation_panel) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		JLabel label = new JLabel(label_text);
		panel.add(label);
		
		JTextField text = new JTextField("0");
		panel.add(text);
		
		rotation_panel.add(panel);
		
		return text;
	}
	
	private final JFrame frame;
	private final JPanel panel;
	
	private JTextField x_angle, y_angle, z_angle;
}
