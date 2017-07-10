package com.qwertygid.deutschsim.Logic;

import java.util.ArrayList;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexField;
import org.apache.commons.math3.linear.Array2DRowFieldMatrix;
import org.apache.commons.math3.linear.ArrayFieldVector;
import org.apache.commons.math3.linear.FieldMatrix;
import org.apache.commons.math3.linear.FieldMatrixChangingVisitor;
import org.apache.commons.math3.linear.FieldVector;
import org.apache.commons.math3.linear.MatrixUtils;

public class Circuit {
	public Circuit() {
		gates = new Table<Gate>();
	}
	
	public Circuit(final ArrayList<ArrayList<Gate>> gates) {
		this.gates = new Table<Gate>(gates);
		
		if (!valid())
			throw new IllegalArgumentException(
					"Invalid arguments passed to Circuit constructor");
	}
	
	public Circuit(final Table<Gate> gates) {
		this.gates = gates;
		
		if (!valid())
			throw new IllegalArgumentException(
					"Invalid arguments passed to Circuit constructor");
	}
	
	public boolean valid() {
		if (!gates.valid())
			return false;
		
		for (int row = 0; row < gates.get_row_count(); row++)		
			for (int col = 0; col < gates.get_col_count(); col++) {
				Gate current_gate = gates.get_element(row, col);
				
				if (current_gate != null)
				{
					if (!current_gate.valid())
						return false;
					
					// Checks if elements go out of bounds
					if (gates.get_row_count() - row < current_gate.get_ports_number())
						return false;
					
					// Checks if elements overlap
					for (int i = 1; i < current_gate.get_ports_number(); i++)
						if (gates.get_element(row + i, col) != null)
							return false;
				}
			}
		
		return true;
	}
	
	public FieldMatrix<Complex> evaluate_circuit_matrix() {
		if (!valid())
			throw new RuntimeException("Cannot evaluate a circuit matrix - the circuit is invalid");
		if (gates.is_empty())
			throw new RuntimeException("Cannot evaluate a circuit matrix - the gate table is empty");
		
		final FieldMatrix<Complex> identity = MatrixUtils.createFieldIdentityMatrix(ComplexField.getInstance(), 2);
		
		FieldMatrix<Complex> matrix = null;
		for (int col = 0; col < gates.get_col_count(); col++) {
			FieldMatrix<Complex> col_matrix = null;
			
			for (int row = 0; row < gates.get_row_count(); row++) {
				Gate current_gate = gates.get_element(row, col);
				if (current_gate == null)
					if (col_matrix == null)
						col_matrix = identity;
					else
						col_matrix = kronecker(col_matrix, identity);
				else {
					if (!current_gate.valid())
						throw new RuntimeException("current_gate is not a valid quantum gate");
					
					if (col_matrix == null)
						col_matrix = current_gate.get_matrix();
					else
						col_matrix = kronecker(col_matrix, current_gate.get_matrix());
					
					row += current_gate.get_ports_number() - 1;
				}
			}
			col_matrix.walkInOptimizedOrder(new ColumnMatrixFinalizer());
			
			if (matrix == null)
				matrix = col_matrix;
			else
				matrix = col_matrix.multiply(matrix);
		}
		
		return matrix;
	}
	
	public FieldVector<Complex> operate(final String qubits) {
		if (!valid_qubit_sequence(qubits))
			throw new IllegalArgumentException("Provided qubit sequence is invalid");
		
		return evaluate_circuit_matrix().operate(get_start_state(qubits));
	}
	
	private boolean valid_qubit_sequence(final String qubits) {		
		return qubits.matches("[01]+") && (qubits.length() == gates.get_row_count());
	}
	
	private FieldVector<Complex> get_start_state(final String qubits) {
		FieldVector<Complex> state = new ArrayFieldVector<Complex>(ComplexField.getInstance(), (int) Math.pow(2, qubits.length()));
		state.setEntry(Integer.parseInt(qubits, 2), new Complex(1));
		return state;
	}
	
	private static FieldMatrix<Complex> kronecker(FieldMatrix<Complex> lhs, FieldMatrix<Complex> rhs) {				
		FieldMatrix<Complex> result = new Array2DRowFieldMatrix<Complex>(ComplexField.getInstance(),
				lhs.getRowDimension() * rhs.getRowDimension(), lhs.getColumnDimension() * rhs.getColumnDimension());
		
		for (int i = 0; i < lhs.getRowDimension(); i++)
			for (int j = 0; j < lhs.getColumnDimension(); j++)
				for (int k = 0; k < rhs.getRowDimension(); k++)
					for (int l = 0; l < rhs.getColumnDimension(); l++) {
						int row = i * rhs.getRowDimension() + k, col = j * rhs.getColumnDimension() + l;
						
						// The control value alters Kronecker product's behavior to create controlled gates
						if (lhs.getEntry(i, j).getReal() == Tools.CONTROL_VALUE)
							if (row == col)
								result.setEntry(row, col, new Complex(Tools.CONTROL_VALUE));
							else
								result.setEntry(row, col, new Complex(0));
						else
							result.setEntry(row, col, lhs.getEntry(i, j).multiply(rhs.getEntry(k, l)));
						
					}
		
		return result;
	}
	
	private Table<Gate> gates;
	
	private static class ColumnMatrixFinalizer implements FieldMatrixChangingVisitor<Complex> {
		@Override
		public Complex end() {
			return null;
		}

		@Override
		public void start(int rows, int cols, int start_row, int end_row, int start_col, int end_col) {
			
		}

		@Override
		public Complex visit(int row, int col, Complex value) {
			if (value.equals(new Complex(Tools.CONTROL_VALUE)))
				return new Complex(1);
			
			return value;			
		}
		
	}
}


