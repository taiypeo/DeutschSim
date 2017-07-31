package com.qwertygid.deutschsim.Miscellaneous;

import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Tools {
	public static final int CONTROL_VALUE = 1337;
	public static final String CONTROL_ID = "_ID_CONTROL";
	
	public static final String dot_image_path = "/img/dot.png";
	
	public static final Font gate_font = new Font("Tahoma", Font.PLAIN, 20);
	
	public enum AngleType {
		DEGREES, RADIANS
	}
	
	public static boolean equal(final double a, final double b) {
		final double EPSILON = 0.00000001;
		
		return Math.abs(a - b) < EPSILON;
	}
	
	public static void error(final JFrame frame, final String msg) {
		JOptionPane.showMessageDialog(frame, msg,
				"Error", JOptionPane.ERROR_MESSAGE);
	}
}
