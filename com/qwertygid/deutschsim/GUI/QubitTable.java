package com.qwertygid.deutschsim.GUI;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class QubitTable extends JTable {
	private static final long serialVersionUID = 4964929671296568709L;
	
	public QubitTable(final int row_height) {
		setFont(new Font("Tahoma", Font.PLAIN, 12));
		setRowHeight(row_height);
		setFocusable(false);
		setRowSelectionAllowed(false);
		setShowGrid(false);
		setIntercellSpacing(new Dimension(0, 0));
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setTableHeader(null);
		setModel(new DefaultTableModel(new Object[][] {{"|0>"}}, new String[] {""}));
		
		update_col_width();
		
		qubits = "0";
	}
	
	public void update_table() {
		Object[][] data = new Object[qubits.length()][1];
		for (int qubit = 0; qubit < qubits.length(); qubit++)
			data[qubit][0] = "|" + qubits.charAt(qubit) + ">";
		
		setModel(new DefaultTableModel(data, new String[] {""}));
		update_col_width();
	}

	public void set_qubits(final String qubits) {
		this.qubits = qubits;
	}
	
	public String get_qubits() {
		return qubits;
	}
	
	@Override
	public boolean isCellEditable(int row, int col) {
		return false;
	}

	private void update_col_width() {
		for (int col = 0; col < getColumnCount(); col++)
			getColumnModel().getColumn(col).
				setPreferredWidth(col_width);
	}
	
	private String qubits;
	
	private static final int col_width = 25;
}
