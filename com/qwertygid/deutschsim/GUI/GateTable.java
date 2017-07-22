package com.qwertygid.deutschsim.GUI;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;

import com.qwertygid.deutschsim.Logic.Gate;
import com.qwertygid.deutschsim.Logic.Table;

public class GateTable extends JPanel{
	private static final long serialVersionUID = 3779004937588318481L;
	
	public GateTable(final int gate_table_cell_size, final int gate_table_row_height) {
		table = new Table<Gate>();		
		this.gate_table_cell_size = gate_table_cell_size;
		this.gate_table_row_height = gate_table_row_height;
		this.gate_table_col_width = gate_table_cell_size + 1;
		
		setBackground(Color.WHITE);
		
		handler = new MouseHandler(this);
		addMouseListener(handler);
		addMouseMotionListener(handler);
	}
	
	@Override
	public void paintComponent(Graphics g) {		
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.BLACK);
		g2d.setStroke(new BasicStroke(2));

		for (int y = gate_table_row_height / 2; y < get_canvas_height(); y += gate_table_row_height) {
			g2d.drawLine(0, y, get_canvas_width(), y);
		}
		
		for (int row = 0; row < table.get_row_count(); row++)
			for (int col = 0; col < table.get_col_count(); col++) {
				Gate gate = table.get_element(row, col);
				if (gate != null) {
					final int x = gate_table_col_width * col, y = gate_table_row_height * row,
							gate_height = gate_table_row_height * (gate.get_ports_number() - 1) + gate_table_cell_size;
					
					g2d.setStroke(new BasicStroke(1));
					
					g2d.setColor(Color.BLACK);
					g2d.drawRect(x, y, gate_table_cell_size, gate_height);
					
					g2d.setColor(Color.WHITE);
					g2d.fillRect(x + 1, y + 1, gate_table_cell_size - 1, gate_height - 1);
				}
			}
		
		if (handler.get_last_mouse_point() != null) {
			g2d.setStroke(new BasicStroke(1));
			
			final int x = handler.get_last_mouse_point().x - handler.get_last_mouse_point().x % gate_table_col_width,
					y = handler.get_last_mouse_point().y - handler.get_last_mouse_point().y % gate_table_row_height;
			
			Color inner_transparent = new Color(255, 0, 0, 255 / 4);
			g2d.setColor(inner_transparent);
			g2d.fillRect(x, y, gate_table_cell_size, gate_table_cell_size);
			
			g2d.setColor(Color.RED);
			g2d.drawRect(x, y, gate_table_cell_size, gate_table_cell_size);
		}
	}
	
	public void update_size() {	
		setPreferredSize(new Dimension(get_canvas_width(), get_canvas_height()));
	}
	
	public Table<Gate> get_table() {
		return table;
	}
	
	public int get_gate_table_row_height() {
		return gate_table_row_height;
	}
	
	public int get_gate_table_col_width() {
		return gate_table_col_width;
	}
	
	private int get_canvas_width() {
		return table.get_col_count() * gate_table_col_width; 
	}
	
	private int get_canvas_height() {
		return table.get_row_count() * gate_table_row_height;
	}
	
	private final Table<Gate> table;
	private final int gate_table_cell_size, gate_table_row_height, gate_table_col_width;
	
	private final MouseHandler handler;
	
	private static class MouseHandler implements MouseInputListener {
		public MouseHandler(final GateTable table) {
			this.table = table;
		}
		
		@Override
		public void mouseClicked(MouseEvent ev) {
			if (SwingUtilities.isRightMouseButton(ev)) {
				final int row = last_mouse_point.y / table.get_gate_table_row_height(),
						col = last_mouse_point.x / table.get_gate_table_col_width();
				
				if (table.get_table().get_element(row, col) != null)
					table.get_table().remove_element(row, col);
			}
		}

		@Override
		public void mouseEntered(MouseEvent ev) {
			
		}

		@Override
		public void mouseExited(MouseEvent ev) {
			last_mouse_point = null;
			table.repaint();
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
		
		public Point get_last_mouse_point() {
			return last_mouse_point;
		}
		
		private void mouse_move_action(final Point point) {
			last_mouse_point = point;
			table.repaint();
		}
		
		private Point last_mouse_point;
		private final GateTable table;
	}
}
