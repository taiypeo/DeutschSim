package com.qwertygid.deutschsim.GUI;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import com.qwertygid.deutschsim.Logic.Gate;
import com.qwertygid.deutschsim.Logic.Table;

public class GateTable extends JPanel {
	private static final long serialVersionUID = 3779004937588318481L;
	
	public GateTable(final int gate_table_cell_size) {
		table = new Table<Gate>();		
		this.gate_table_cell_size = gate_table_cell_size;
		
		setBackground(Color.WHITE);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		final int canvas_width = table.get_col_count() * gate_table_cell_size,
				canvas_height = table.get_row_count() * gate_table_cell_size;
		
		setPreferredSize(new Dimension(canvas_width, canvas_height));
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.BLACK);
		g2d.setStroke(new BasicStroke(2));
		
		for (int y = gate_table_cell_size / 2; y < canvas_height; y += gate_table_cell_size) {
			g2d.drawLine(0, y, canvas_width, y);
		}
	}
	
	public Table<Gate> get_table() {
		return table;
	}
	
	private Table<Gate> table;
	private final int gate_table_cell_size;
}
