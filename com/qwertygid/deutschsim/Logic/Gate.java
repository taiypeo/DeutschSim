package com.qwertygid.deutschsim.Logic;

import java.util.ArrayList;

public abstract class Gate {
	public Gate(final String id, final int IO_ports)
	{
		this.id = id;
		this.IO_ports = IO_ports;
	}
	
	public abstract void operate(ArrayList<Qubit> qubits);
	
	public String get_id() {
		return id;
	}
	
	public int get_ports_number() {
		return IO_ports;
	}
	
	private final String id;
	private final int IO_ports;
}
