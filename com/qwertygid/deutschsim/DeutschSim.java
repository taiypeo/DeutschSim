package com.qwertygid.deutschsim;

import java.awt.EventQueue;

import com.qwertygid.deutschsim.GUI.GUI;

public class DeutschSim {
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				new GUI();
			}	
		});
	}
}
