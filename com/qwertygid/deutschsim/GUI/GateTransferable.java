package com.qwertygid.deutschsim.GUI;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import com.qwertygid.deutschsim.Logic.Gate;

public class GateTransferable implements Transferable {
    public GateTransferable(final Gate gate) {
        this.gate = gate;
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        return gate;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] { GATE_DATA_FLAVOR };
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(GATE_DATA_FLAVOR);
    }

    public Gate get_gate() {
        return gate;
    }

    public static final DataFlavor GATE_DATA_FLAVOR = new DataFlavor(Gate.class, "DeutschSim/Gate");

    private final Gate gate;
}
