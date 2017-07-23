package com.qwertygid.deutschsim.misc;

import java.awt.Font;

public class Tools {
	public static final int CONTROL_VALUE = 1337;
	public static final String CONTROL_ID = "_ID_CONTROL";
	
	public static final String dot_image_path = "/img/dot.png";
	
	public static final Font gate_font = new Font("Tahoma", Font.PLAIN, 12);
	
	public static boolean equal(final double a, final double b) {
		final double EPSILON = 0.00000001;
		
		return Math.abs(a - b) < EPSILON;
	}
}