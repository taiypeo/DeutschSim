package com.qwertygid.deutschsim.GUI;

import java.awt.Dimension;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;

public class GUITable extends JTable{
	private static final long serialVersionUID = -1889663811918216468L;

	public GUITable() {
		setRowSelectionAllowed(false);
		setShowGrid(false);
		setIntercellSpacing(new Dimension(0, 0));
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setTableHeader(null);
	}
}
