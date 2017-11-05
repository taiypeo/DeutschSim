package com.qwertygid.deutschsim.Logic;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexField;
import org.apache.commons.math3.linear.Array2DRowFieldMatrix;
import org.apache.commons.math3.linear.ArrayFieldVector;
import org.apache.commons.math3.linear.FieldMatrix;
import org.apache.commons.math3.linear.FieldVector;
import org.apache.commons.math3.linear.MatrixUtils;

import com.qwertygid.deutschsim.Miscellaneous.Tools;

public class Circuit {
    public Circuit() {
        gates = new Table<Gate>();
    }

    public Circuit(final ArrayList<ArrayList<Gate>> gates) {
        this.gates = new Table<Gate>(gates);

        if (!valid())
            throw new IllegalArgumentException("Invalid arguments passed to Circuit constructor");
    }

    public Circuit(final Table<Gate> gates) {
        this.gates = gates;

        if (!valid())
            throw new IllegalArgumentException("Invalid arguments passed to Circuit constructor");
    }

    public boolean valid() {
        if (!gates.valid())
            return false;

        for (int row = 0; row < gates.get_row_count(); row++)
            for (int col = 0; col < gates.get_col_count(); col++) {
                Gate current_gate = gates.get_element(row, col);

                if (current_gate != null) {
                    if (!current_gate.valid())
                        return false;

                    // Checks if elements go out of bounds
                    if (gates.get_row_count() - row < current_gate.get_ports_number())
                        return false;

                    // Checks if elements overlap
                    for (int i = 1; i < current_gate.get_ports_number(); i++)
                        if (gates.get_element(row + i, col) != null)
                            return false;
                }
            }

        return true;
    }

    public FieldMatrix<Complex> evaluate_circuit_matrix() {
        if (!valid())
            throw new RuntimeException("Cannot evaluate a circuit matrix - the circuit is invalid");
        if (gates.is_empty())
            throw new RuntimeException("Cannot evaluate a circuit matrix - the gate table is empty");

        FieldMatrix<Complex> matrix = null;
        ArrayList<FieldMatrix<Complex>> matrices = get_matrices();
        for (FieldMatrix<Complex> mat : matrices)
            if (matrix == null)
                matrix = mat;
            else
                matrix = mat.multiply(matrix);

        return matrix;
    }

    public FieldVector<Complex> operate(final String qubits) {
        if (!valid_qubit_sequence(qubits))
            throw new IllegalArgumentException("Provided qubit sequence is invalid");

        FieldVector<Complex> state = get_start_state(qubits);

        ArrayList<FieldMatrix<Complex>> matrices = get_matrices();
        for (FieldMatrix<Complex> matrix : matrices) {
            state = matrix.operate(state);
        }

        return state;
    }

    public boolean valid_qubit_sequence(final String qubits) {
        return qubits.matches("[01]+") && (qubits.length() == gates.get_row_count());
    }

    public Table<Gate> get_gates_table() {
        return gates;
    }

    private FieldVector<Complex> get_start_state(final String qubits) {
        FieldVector<Complex> state = new ArrayFieldVector<Complex>(ComplexField.getInstance(),
                (int) Math.pow(2, qubits.length()));
        state.setEntry(Integer.parseInt(qubits, 2), new Complex(1));
        return state;
    }

    private ArrayList<FieldMatrix<Complex>> get_matrices() {
        ArrayList<FieldMatrix<Complex>> matrices = new ArrayList<FieldMatrix<Complex>>();

        for (int col = 0; col < gates.get_col_count(); col++) {
            ArrayList<Integer> controls = new ArrayList<Integer>();
            for (int row = 0; row < gates.get_row_count(); row++)
                if (gates.get_element(row, col) != null && gates.get_element(row, col).get_id() == Tools.CONTROL_ID)
                    controls.add(row);

            for (int row = 0; row < gates.get_row_count(); row++) {
                Gate current = gates.get_element(row, col);
                if (current == null || current.get_id() == Tools.CONTROL_ID)
                    continue;

                ArrayList<Integer> gate_inputs = new ArrayList<Integer>(controls);
                for (int delta = 0; delta < current.get_ports_number(); delta++)
                    gate_inputs.add(row + delta);

                FieldMatrix<Complex> mat = expand_gate(
                        new Gate("Controlled", make_controlled(current.get_matrix(), controls.size())), gate_inputs,
                        gates.get_row_count());
                if (mat != null)
                    matrices.add(mat);
            }
        }

        return matrices;
    }

    private static FieldMatrix<Complex> make_controlled(final FieldMatrix<Complex> matrix, final int control_count) {
        FieldMatrix<Complex> result = matrix;
        Complex[][] raw_matrix = null;

        for (int it = 0; it < control_count; it++) {
            final int dimension = 2 * result.getRowDimension();
            raw_matrix = MatrixUtils.createFieldIdentityMatrix(ComplexField.getInstance(), dimension).getData();
            for (int row = 0; row < result.getRowDimension(); row++) {
                for (int col = 0; col < result.getColumnDimension(); col++)
                    raw_matrix[row + result.getRowDimension()][col + result.getColumnDimension()] = result.getEntry(row,
                            col);
            }

            if (raw_matrix != null)
                result = new Array2DRowFieldMatrix<Complex>(raw_matrix);
        }

        return result;
    }

    private static FieldMatrix<Complex> expand_gate(final Gate gate, ArrayList<Integer> operated_qubits, // Algorithm taken from "Automatic Quantum Computer Programming"
            final int total_qubits) {                                                                    // by Lee Spector
        if (gate == null)                                                                                // Davy Wybiral's code was used as a reference
            return null;

        for (int qubit = 0; qubit < operated_qubits.size(); qubit++)
            operated_qubits.set(qubit, total_qubits - 1 - operated_qubits.get(qubit));

        Collections.reverse(operated_qubits);

        ArrayList<Integer> other_qubits = new ArrayList<Integer>();
        for (int qubit = 0; qubit < total_qubits; qubit++)
            if (!operated_qubits.contains(qubit))
                other_qubits.add(qubit);

        final int dimension = (int) Math.pow(2, total_qubits);

        Complex[][] raw_matrix = new Complex[dimension][dimension];

        for (int row = 0; row < dimension; row++) {
            for (int col = 0; col < dimension; col++) {
                boolean binary_equal = true;
                for (int other : other_qubits)
                    if (((row >> other) & 1) != ((col >> other) & 1)) {
                        binary_equal = false;
                        break;
                    }

                if (binary_equal) {
                    int i_star = 0, j_star = 0;

                    int index = 0;
                    for (int operated : operated_qubits) {
                        i_star |= ((row >> operated) & 1) << index;
                        j_star |= ((col >> operated) & 1) << index;

                        index++;
                    }

                    raw_matrix[row][col] = gate.get_matrix().getEntry(i_star, j_star);
                } else
                    raw_matrix[row][col] = Complex.ZERO;
            }
        }

        return new Array2DRowFieldMatrix<Complex>(raw_matrix);
    }

    private Table<Gate> gates;
}
