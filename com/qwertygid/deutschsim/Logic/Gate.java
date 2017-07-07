package com.qwertygid.deutschsim.Logic;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.linear.Array2DRowFieldMatrix;
import org.apache.commons.math3.linear.FieldMatrix;
import org.apache.commons.math3.linear.FieldMatrixChangingVisitor;
import org.apache.commons.math3.linear.FieldMatrixPreservingVisitor;

public class Gate {
	public Gate(final String id, double angle_x, double angle_y, double angle_z, final boolean in_degrees)
	{
		this.id = id;
		this.IO_ports = 1;
		
		if (in_degrees) {
			angle_x = Math.toRadians(angle_x);
			angle_y = Math.toRadians(angle_y);
			angle_z = Math.toRadians(angle_z);
		}
		
		Complex[][] data_x_rot = new Complex[][] {
			{new Complex(Math.cos(angle_x / 2)), new Complex(0.0, -Math.sin(angle_x / 2))},
			{new Complex(0.0, -Math.sin(angle_x / 2)), new Complex(Math.cos(angle_x / 2))}
		};
		FieldMatrix<Complex> x_rot = new Array2DRowFieldMatrix<Complex>(data_x_rot);
		
		Complex[][] data_y_rot = new Complex[][] {
			{new Complex(Math.cos(angle_y / 2)), new Complex(-Math.sin(angle_y / 2))},
			{new Complex(Math.sin(angle_y / 2)), new Complex(Math.cos(angle_y / 2))}
		};
		FieldMatrix<Complex> y_rot = new Array2DRowFieldMatrix<Complex>(data_y_rot);
		
		Complex[][] data_z_rot = new Complex[][] {
			{new Complex(Math.cos(angle_z / 2), -Math.sin(angle_z / 2)), new Complex(0.0)},
			{new Complex(0.0), new Complex(Math.cos(angle_z / 2), Math.sin(angle_z / 2))}
		};
		FieldMatrix<Complex> z_rot = new Array2DRowFieldMatrix<Complex>(data_z_rot);
		
		mat = x_rot.multiply(y_rot).multiply(z_rot);
		
		if (!valid())
			throw new IllegalArgumentException(
					"Matrix is not a valid quantum gate in Gate rotation constructor");
	}
	
	// TODO phase shift Gate constructor, CircuitGate constructor
	
	public Gate(final String id, final FieldMatrix<Complex> mat) {
		this.id = id;
		this.IO_ports = mat.getColumnDimension() / 2;
		this.mat = mat;
		
		if (!valid())
			throw new IllegalArgumentException(
					"Provided matrix is not a valid quantum gate");
	}
	
	public boolean valid()
	{
		// Checks if this is a control matrix
		Complex[][] data = new Complex[][] {
				{new Complex(Tools.CONTROL_VALUE), new Complex(0)},
				{new Complex(0), new Complex(1)}
		};
		
		FieldMatrix<Complex> control = new Array2DRowFieldMatrix<Complex>(data);
		
		if (mat.equals(control))
			return true;
		
		if (!mat.isSquare() || mat.getColumnDimension() % 2 != 0)
			return false;
		
		// Computes dagger of mat
		FieldMatrix<Complex> dagger = mat.transpose();
		dagger.walkInOptimizedOrder(new MatrixConjugator());
		
		// Checks if dagger * mat = identity matrix
		FieldMatrix<Complex> result = mat.multiply(dagger);
		Complex is_identity = result.walkInOptimizedOrder(new UnitaryChecker()); // 1+0i if true, 0+0i if false
		
		return is_identity.equals(new Complex(1));
	}
	
	public String get_id() {
		return id;
	}
	
	public int get_ports_number() {
		return IO_ports;
	}
	
	public FieldMatrix<Complex> get_matrix() {
		return mat;
	}
	
	private final FieldMatrix<Complex> mat;
	private final String id;
	private final int IO_ports;
	
	private class MatrixConjugator implements FieldMatrixChangingVisitor<Complex> {
		@Override
		public Complex end() {
			return null;
		}

		@Override
		public void start(int rows, int cols, int start_row, int end_row, int start_col, int end_col) {
			
		}

		@Override
		public Complex visit(int row, int col, Complex value) {
			return value.conjugate();			
		}
	}
	
	private class UnitaryChecker implements FieldMatrixPreservingVisitor<Complex> {
		public UnitaryChecker() {
			return_value = new Complex(1);
		}
		
		@Override
		public Complex end() {
			return return_value;
		}

		@Override
		public void start(int rows, int cols, int start_row, int end_row, int start_col, int end_col) {
			
		}

		@Override
		public void visit(int row, int col, Complex value) {
			int delta = (row == col ? 1 : 0);
			if (!Tools.equal(value.getReal(), delta)) {
				return_value = new Complex(0);
				end();
			}
		}
		
		private Complex return_value;
	}
}
