package com.qwertygid.deutschsim;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.linear.Array2DRowFieldMatrix;
import org.apache.commons.math3.linear.FieldMatrix;
import org.apache.commons.math3.linear.FieldVector;

import com.qwertygid.deutschsim.Logic.*;

public class DeutschSim {

	public static void main(String[] args) {
		String qubits = "01";
		Table<Gate> gates = new Table<Gate>();
		gates.add_row();
		gates.add_row();
		gates.add_col();
		gates.add_col();
		
		Complex[][] data1 = new Complex[][] {
				{new Complex(Tools.CONTROL_VALUE), new Complex(0)},
				{new Complex(0), new Complex(1)}
		};
		FieldMatrix<Complex> c = new Array2DRowFieldMatrix<Complex>(data1);
		Gate control = new Gate("control", c);
		
		Complex[][] data2 = new Complex[][] {
				{new Complex(0), new Complex(1)},
				{new Complex(1), new Complex(0)}
		};
		FieldMatrix<Complex> x = new Array2DRowFieldMatrix<Complex>(data2);
		Gate pauli_x = new Gate("X", x);
		
		Complex[][] data3 = new Complex[][] {
				{new Complex(1), new Complex(1)},
				{new Complex(1), new Complex(-1)}
		};
		FieldMatrix<Complex> had = new Array2DRowFieldMatrix<Complex>(data3);
		Complex coefficient = new Complex(1/Math.sqrt(2));
		had = had.scalarMultiply(coefficient);
		Gate hadamard = new Gate("hadamard", had);
		
		gates.insert_element(hadamard, 0, 0);
		gates.insert_element(control, 0, 1);
		gates.insert_element(pauli_x, 1, 1);
		
		Circuit sim = new Circuit(gates);
		FieldVector<Complex> state = sim.operate(qubits);
		System.out.println(state.toString());
	}

}
