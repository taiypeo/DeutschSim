package com.qwertygid.deutschsim.GUI;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

import com.qwertygid.deutschsim.Logic.Gate;
import com.qwertygid.deutschsim.Logic.Table;

public class GateTable extends JPanel implements MouseInputListener{
	private static final long serialVersionUID = 3779004937588318481L;
	
	public GateTable(final int gate_table_cell_size, final int gate_table_row_height) {
		table = new Table<Gate>();		
		this.gate_table_cell_size = gate_table_cell_size;
		this.gate_table_row_height = gate_table_row_height;
		this.gate_table_col_width = gate_table_cell_size + 1;
		
		setBackground(Color.WHITE);
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		final int canvas_width = table.get_col_count() * gate_table_col_width,
				canvas_height = table.get_row_count() * gate_table_row_height;
		
		setPreferredSize(new Dimension(canvas_width, canvas_height));
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.BLACK);
		g2d.setStroke(new BasicStroke(2));
		
		for (int y = gate_table_row_height / 2; y < canvas_height; y += gate_table_row_height) {
			g2d.drawLine(0, y, canvas_width, y);
		}
		
		if (last_mouse_point != null) {
			g2d.setStroke(new BasicStroke(1));
			
			final int x = last_mouse_point.x - last_mouse_point.x % gate_table_col_width,
					y = last_mouse_point.y - last_mouse_point.y % gate_table_row_height;
			
			Color inner_transparent = new Color(255, 0, 0, 255 / 4);
			g2d.setColor(inner_transparent);
			g2d.fillRect(x, y, gate_table_cell_size, gate_table_cell_size);
			
			g2d.setColor(Color.RED);
			g2d.drawRect(x, y, gate_table_cell_size, gate_table_cell_size);
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent ev) {
		// Check for right-click on an element
	}

	@Override
	public void mouseEntered(MouseEvent ev) {
		
	}

	@Override
	public void mouseExited(MouseEvent ev) {
		last_mouse_point = null;
		repaint();
	}

	@Override
	public void mousePressed(MouseEvent ev) {
		
	}

	@Override
	public void mouseReleased(MouseEvent ev) {
		
	}
	
	@Override
	public void mouseDragged(MouseEvent ev) {
		mouse_move_action(ev.getPoint());
	}

	@Override
	public void mouseMoved(MouseEvent ev) {
		mouse_move_action(ev.getPoint());
	}
	
	public Table<Gate> get_table() {
		return table;
	}
	
	private void mouse_move_action(final Point point) {
		last_mouse_point = point;
		repaint();
	}
	
	private Table<Gate> table;
	private final int gate_table_cell_size, gate_table_row_height, gate_table_col_width;
	private Point last_mouse_point;
}
