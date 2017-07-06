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

public class Simulator {
	public Simulator() {
		qubits = "";
		gates = new Table<Gate>();
	}
	
	public Simulator(final String qubits, final ArrayList<ArrayList<Gate>> gates) {
		this.qubits = qubits;
		this.gates = new Table<Gate>(gates);
		
		if (!valid())
			throw new IllegalArgumentException(
					"Invalid arguments passed to Simulator constructor");
	}
	
	public Simulator(final String qubits, final Table<Gate> gates) {
		this.qubits = qubits;
		this.gates = gates;
		
		if (!valid())
			throw new IllegalArgumentException(
					"Invalid arguments passed to Simulator constructor");
	}
	
	public FieldVector<Complex> simulate() {
		if (gates.is_empty())
			throw new RuntimeException("Cannot simulate an empty circuit");
		
		FieldVector<Complex> state = get_start_state();
		
		FieldMatrix<Complex> identity = MatrixUtils.createFieldIdentityMatrix(ComplexField.getInstance(), 2);
		
		for (int col = 0; col < gates.get_col_count(); col++) {
			FieldMatrix<Complex> matrix = null;
			
			for (int row = 0; row < gates.get_row_count(); row++) {
				Gate current_gate = gates.get_element(row, col);
				
				if (current_gate == null)
					if (matrix == null)
						matrix = identity;
					else
						matrix = kronecker(matrix, identity);
				else if (current_gate instanceof MatrixGate) {
					if (!((MatrixGate) current_gate).valid())
						throw new RuntimeException("current_gate is not a valid quantum matrix gate");
					
					FieldMatrix<Complex> current_matrix = ((MatrixGate) current_gate).get_matrix();
					
					if (matrix == null)
						matrix = current_matrix; 
					else
						matrix = kronecker(matrix, current_matrix);
					
					row += current_gate.get_ports_number() - 1;
				} //else if (current_gate instanceof CircuitGate) TODO 
			}
			
			matrix.walkInOptimizedOrder(new MatrixFinalizer());
			
			state = matrix.operate(state);
		}
		
		return state;
	}
	
	public boolean valid() {
		if (qubits.length() != gates.get_row_count())
			return false;
		
		for (int row = 0; row < gates.get_row_count(); row++)
		{
			if (!gates.valid())
				return false;
			
			for (int col = 0; col < gates.get_col_count(); col++)
			{
				if (gates.get_element(row, col) != null)
				{
					// Checks if elements go out of bounds
					if (gates.get_row_count() - row < gates.get_element(row, col).get_ports_number())
						return false;
					
					// Checks if elements overlap
					for (int i = 1; i < gates.get_element(row, col).get_ports_number(); i++)
						if (gates.get_element(row + i, col) != null)
							return false;
				}
			}
		}
		
		return true;
	}
	
	private FieldVector<Complex> get_start_state() {
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
	
	private String qubits; // big-endian
	private Table<Gate> gates;
}

class MatrixFinalizer implements FieldMatrixChangingVisitor<Complex> {

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