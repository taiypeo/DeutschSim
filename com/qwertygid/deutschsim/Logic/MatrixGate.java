package com.qwertygid.deutschsim.Logic;

import java.util.ArrayList;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.linear.Array2DRowFieldMatrix;
import org.apache.commons.math3.linear.FieldMatrix;

public class MatrixGate extends Gate {

	public MatrixGate(String id, FieldMatrix<Complex> mat) {
		super(id, mat.getColumnDimension() / 2);
		this.mat = mat;
		
		if (!valid())
			throw new IllegalArgumentException(
					"Provided matrix is not a valid quantum gate");
	}
	
	public MatrixGate(String id, double angle_x, double angle_y, double angle_z, boolean in_degrees)
	{
		super(id, 1);
		
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
	}

	@Override
	public void operate(ArrayList<Qubit> qubits) {
		if (qubits.size() != mat.getColumnDimension() / 2)
			throw new IllegalArgumentException(
					"Invalid qubit input length to a matrix gate");
		
		for (Qubit q : qubits) {
			q.set_superposition(mat.operate(q.get_superposition()));
		}
	}
	
	public boolean valid()
	{	
		final double EPSILON = 0.000001;
		
		if (!mat.isSquare() || mat.getColumnDimension() % 2 != 0)
			return false;
		
		// Computes dagger of mat
		FieldMatrix<Complex> dagger = mat.transpose();
		for (int i = 0; i < dagger.getRowDimension(); i++)
			for (int j = 0; j < dagger.getColumnDimension(); j++)
				dagger.setEntry(i, j, dagger.getEntry(i, j).conjugate());
		
		// Checks if dagger * mat = identity matrix
		FieldMatrix<Complex> result = mat.multiply(dagger);
		for (int i = 0; i < result.getRowDimension(); i++)
			for (int j = 0; j < result.getColumnDimension(); j++)
			{
				int delta = (i == j ? 1 : 0);
				if (result.getEntry(i, j).getReal() - delta >= EPSILON)
					return false;
			}
		
		return true;
	}

	private final FieldMatrix<Complex> mat;
}
