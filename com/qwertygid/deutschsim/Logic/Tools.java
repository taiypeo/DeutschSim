package com.qwertygid.deutschsim.Logic;

public class Tools {
	public static boolean equal(final double a, final double b) {
		final double EPSILON = 0.00000001;
		
		return Math.abs(a - b) < EPSILON;
	}
}
