package com.qwertygid.deutschsim.IO;

import java.util.HashSet;

import com.qwertygid.deutschsim.Logic.Circuit;
import com.qwertygid.deutschsim.Logic.Gate;
import com.qwertygid.deutschsim.Logic.Table;

public class Serializer {
	public Serializer(final String qubits, final Circuit circuit) {
		if (!circuit.valid())
			throw new IllegalArgumentException("Cannot save a state with an invalid circuit");
		if (!circuit.valid_qubit_sequence(qubits))
			throw new IllegalArgumentException("Cannot save a state with an invalid qubit sequence");
		
		this.qubits = qubits;
		this.circuit = circuit;
	}
	
	public Serializer(final String filename) {
		deserialize(filename);
	}
	
	public void serialize(final String filename) {
		
	}
	
	public void deserialize(final String filename) {
		
	}
	
	public HashSet<Gate> get_used_gates() {
		HashSet<Gate> used_gates = new HashSet<Gate>();
		
		Table<Gate> gates = circuit.get_gates_table();
		
		for (int row = 0; row < gates.get_row_count(); row++)
			for (int col = 0; col < gates.get_col_count(); col++) {
				Gate current = gates.get_element(row, col);
				if (current != null)
					used_gates.add(current);
			}
		
		return used_gates;
	}
	
	public String get_qubit_sequence() {
		return qubits;
	}
	
	public Circuit get_circuit() {
		return circuit;
	}
	
	private String qubits;
	private Circuit circuit;
}
