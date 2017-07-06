package com.qwertygid.deutschsim.Logic;

public class Tools {
	public static final int CONTROL_VALUE = 1337;
	
	public static boolean equal(final double a, final double b) {
		final double EPSILON = 0.00000001;
		
		return Math.abs(a - b) < EPSILON;
	}
}
