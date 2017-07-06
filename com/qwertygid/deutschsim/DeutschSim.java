package com.qwertygid.deutschsim;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.linear.Array2DRowFieldMatrix;
import org.apache.commons.math3.linear.FieldMatrix;
import org.apache.commons.math3.linear.FieldVector;

import com.qwertygid.deutschsim.Logic.*;

public class DeutschSim {

	public static void main(String[] args) {
		String qubits = "100";
		Table<Gate> gates = new Table<Gate>();
		gates.add_row();
		gates.add_row();
		gates.add_row();
		gates.add_col();
		
		Complex[][] data1 = new Complex[][] {
				{new Complex(Tools.CONTROL_VALUE), new Complex(0)},
				{new Complex(0), new Complex(1)}
		};
		FieldMatrix<Complex> had = new Array2DRowFieldMatrix<Complex>(data1);
		MatrixGate control = new MatrixGate("hadamard", had);
		
		Complex[][] data2 = new Complex[][] {
				{new Complex(0), new Complex(1)},
				{new Complex(1), new Complex(0)}
		};
		FieldMatrix<Complex> x = new Array2DRowFieldMatrix<Complex>(data2);
		MatrixGate pauli_x = new MatrixGate("X", x);
		
		gates.insert_element(control, 0, 0);
		gates.insert_element(pauli_x, 1, 0);
		gates.insert_element(pauli_x, 2, 0);
		
		Simulator sim = new Simulator(qubits, gates);
		FieldVector<Complex> state = sim.simulate();
		System.out.println(state.toString());
	}

}
