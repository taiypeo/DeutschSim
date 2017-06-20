package com.qwertygid.deutschsim;

import java.util.ArrayList;

import org.apache.commons.math3.complex.Complex;

import com.qwertygid.deutschsim.Logic.*;

public class DeutschSim {

	public static void main(String[] args) {
		MatrixGate x_rot = new MatrixGate("gate1", 180, 0, 0, true);
		MatrixGate y_rot = new MatrixGate("gate2", 0, 180, 0, true);
		Complex num = new Complex(0.7071067811865475);
		Qubit q = new Qubit(num, num);
		ArrayList<Qubit> l = new ArrayList<Qubit>();
		l.add(q);
		x_rot.operate(l);
		y_rot.operate(l);
		System.out.println(q.get_superposition().getEntry(0) + "    " + q.get_superposition().getEntry(1));
		System.out.println(q.valid());
	}

}
