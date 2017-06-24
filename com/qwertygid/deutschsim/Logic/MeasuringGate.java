package com.qwertygid.deutschsim.Logic;

import java.util.ArrayList;

// Class for Measurement and Dot gates

public class MeasuringGate extends Gate {
	public MeasuringGate(final String id) {		
		super(id, 1);
	}
	
	@Override
	public void operate(ArrayList<Qubit> qubits) {
		// Snaps qubit's state to the Z axis (TODO)
	}

}
