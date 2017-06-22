package com.qwertygid.deutschsim.Logic;

import java.util.ArrayList;

public class Simulator {
	public Simulator() {
		qubits = new ArrayList<Qubit>();
		gates = new ArrayList<ArrayList<Gate>>();
	}
	
	public Simulator(final ArrayList<Qubit> qubits, final ArrayList<ArrayList<Gate>> gates) {
		this.qubits = qubits;
		this.gates = gates;
		
		if (!valid())
			throw new IllegalArgumentException(
					"Invalid arguments passed to Simulator constructor");
	}
	
	public String simulate() {
		return "";
	}
	
	public void add_gate(Gate gate, final int row, final int col) {
		if (is_in_gates_table(row, col))
			gates.get(row).set(col, gate);
		else
			throw new IllegalArgumentException(
					"Position passed to add_gate() is out of bounds");
	}
	
	public void remove_gate(final int row, final int col) {
		if (is_in_gates_table(row, col))
			if (gates.get(row).get(col) != null)
				gates.get(row).set(col, null);
			else
				throw new IllegalArgumentException("Cannot delete a nonexistant gate");
		else
			throw new IllegalArgumentException(
					"Position passed to remove_gate() is out of bounds");
	}
	
	public boolean valid() {
		if (qubits.size() != gates.size())
			return false;
		
		int last_row_length = 0;
		
		for (int row = 0; row < gates.size(); row++)
		{
			// Checks if all rows are of the same length
			if (row == 0)
				last_row_length = gates.get(row).size();
			else if (gates.get(row).size() != last_row_length)
				return false;
			
			for (int col = 0; col < gates.get(row).size(); col++)
			{
				if (gates.get(row).get(col) != null)
				{
					// Checks if elements go out of bounds
					if (gates.get(row).size() - col < gates.get(row).get(col).get_ports_number())
						return false;
					
					// Checks if elements overlap
					for (int i = 1; i < gates.get(row).get(col).get_ports_number(); i++)
						if (gates.get(row + i).get(col) != null)
							return false;
				}
			}
		}
		
		return true;
	}
	
	public Qubit get_qubit(final int index) {
		if (index >= 0 && index < qubits.size())
			return qubits.get(index);
		else
			throw new IllegalArgumentException(
					"Provided qubit index is out of bounds");
	}
	
	public void add_row() {
		qubits.add(new Qubit());
		
		int size = (gates.size() == 0 ? 0 : gates.get(0).size());
		
		ArrayList<Gate> row = new ArrayList<Gate>();
		for (int i = 0; i < size; i++)
			row.add(null);
		gates.add(new ArrayList<Gate>(row));
	}

	public void add_column() {
		if (gates.size() == 0)
			add_row();
		
		for (ArrayList<Gate> row : gates)
			row.add(null);
	}
	
	public void remove_last_row() {
		if (is_table_empty())
			throw new IllegalArgumentException(
					"Cannot remove a row from an empty table");
		else {
			qubits.remove(qubits.size() - 1);
			gates.remove(gates.size() - 1);
		}
	}
	
	public void remove_last_column() {
		if (is_table_empty())
			throw new IllegalArgumentException(
					"Cannot remove a column from an empty table");
		else {
			for (ArrayList<Gate> row : gates)
				row.remove(row.size() - 1);
		}
	}

	
	private boolean is_in_gates_table(final int row, final int col) {
		if (gates.size() == 0)
			return false;
		
		return (row >= 0) && (row < gates.size()) && (col >= 0) &&
				(col < gates.get(0).size());
	}
	
	private boolean is_table_empty() {
		return (qubits.size() == 0) || (gates.size() == 0) ||
				(gates.get(0).size() == 0);
	}
	
	private ArrayList<Qubit> qubits;
	private ArrayList<ArrayList<Gate>> gates;
}
