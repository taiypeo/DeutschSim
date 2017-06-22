package com.qwertygid.deutschsim.Logic;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexField;
import org.apache.commons.math3.linear.ArrayFieldVector;
import org.apache.commons.math3.linear.FieldVector;

public class Qubit {
	public Qubit() {
		superposition = new ArrayFieldVector<Complex>(ComplexField.getInstance(), 2);
	}
	
	public Qubit(final Complex alpha, final Complex beta) {
		Complex[] coefficients_arr = new Complex[]{alpha, beta};
		superposition = new ArrayFieldVector<Complex>(coefficients_arr);
		
		if (!valid())
			throw new IllegalArgumentException(
					"Sum of squares of provided alpha and beta is not equal to 1");
	}	 
	
	public boolean valid() {
		final double EPSILON = 0.000001;
		
		double sum_of_squares = 
				Math.pow(superposition.getEntry(0).abs(), 2.0) +
				Math.pow(superposition.getEntry(1).abs(), 2.0);
		
		return (superposition.getDimension() == 2) &&
				(Math.abs(sum_of_squares - 1) < EPSILON);
	}
	
	public void set_superposition(FieldVector<Complex> superposition)
	{
		if (superposition.getDimension() != 2)
			throw new IllegalArgumentException(
					"Provided superposition is not a 2D vector");
		
		this.superposition = superposition;
	}
	
	public FieldVector<Complex> get_superposition() {
		return superposition;
	}
	
	private FieldVector<Complex> superposition;
}
