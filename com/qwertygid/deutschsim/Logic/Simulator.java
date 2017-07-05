package com.qwertygid.deutschsim.Logic;

import java.util.ArrayList;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.linear.FieldVector;

public class Simulator {
	public Simulator() {
		qubits = new ArrayList<Boolean>();
		gates = new Table<Gate>();
	}
	
	public Simulator(final ArrayList<Boolean> qubits, final ArrayList<ArrayList<Gate>> gates) {
		this.qubits = qubits;
		this.gates = new Table<Gate>(gates);
		
		if (!valid())
			throw new IllegalArgumentException(
					"Invalid arguments passed to Simulator constructor");
	}
	
	public FieldVector<Complex> simulate() {
		return null;
	}
	
	public boolean valid() {
		if (qubits.size() != gates.get_row_count())
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
					if (gates.get_data().get(row).size() - col < gates.get_element(row, col).get_ports_number())
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
	
	private ArrayList<Boolean> qubits; // big-endian
	private Table<Gate> gates;
}
