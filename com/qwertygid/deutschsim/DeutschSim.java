package com.qwertygid.deutschsim;

import javax.swing.SwingUtilities;

import com.qwertygid.deutschsim.GUI.GUI;

public class DeutschSim {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GUI();
            }
        });
    }
}
