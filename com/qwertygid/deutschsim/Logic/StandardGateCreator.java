package com.qwertygid.deutschsim.Logic;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.linear.Array2DRowFieldMatrix;

public class StandardGateCreator {
	public static Gate create_pauli_x() {
		Complex[][] data = new Complex[][] {
			{new Complex(0), new Complex(1)},
			{new Complex(1), new Complex(0)}
		};
		
		return new Gate("X", new Array2DRowFieldMatrix<Complex>(data));
	}
	
	public static Gate create_pauli_y() {
		Complex[][] data = new Complex[][] {
			{new Complex(0), new Complex(0, -1)},
			{new Complex(0, 1), new Complex(0)}
		};
		
		return new Gate("Y", new Array2DRowFieldMatrix<Complex>(data));
	}
	
	public static Gate create_pauli_z() {
		Complex[][] data = new Complex[][] {
			{new Complex(1), new Complex(0)},
			{new Complex(0), new Complex(-1)}
		};
		
		return new Gate("Z", new Array2DRowFieldMatrix<Complex>(data));
	}
	
	public static Gate create_hadamard() {
		Complex[][] data = new Complex[][] {
			{new Complex(1), new Complex(1)},
			{new Complex(1), new Complex(-1)}
		};
		
		Array2DRowFieldMatrix<Complex> matrix = new Array2DRowFieldMatrix<Complex>(data);
		Complex multiplier = new Complex(1 / Math.sqrt(2));
		
		return new Gate("H", matrix.scalarMultiply(multiplier));
	}
	
	public static Gate create_control() {
		Complex[][] data = new Complex[][] {
			{new Complex(Tools.CONTROL_VALUE), new Complex(0)},
			{new Complex(0), new Complex(1)}
		};
		
		return new Gate(Tools.CONTROL_ID, new Array2DRowFieldMatrix<Complex>(data));
	}
}
