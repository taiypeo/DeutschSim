package com.qwertygid.deutschsim.GUI;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class GateTableCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = -69224632471142746L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
	        boolean is_selected, boolean has_focus, int row, int column) {		
		cell_empty = (value == null);
		
		JComponent component = (JComponent) super.getTableCellRendererComponent(table, value, is_selected, has_focus, row, column);
		if (!cell_empty)
			component.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		return component;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D) g;
		
		Rectangle rect = g2d.getClipBounds();
		g2d.setStroke(new BasicStroke(2));
		g2d.setColor(Color.BLACK);
		
		if (cell_empty) {
			final int x1 = rect.x, x2 = x1 + rect.width, y = rect.y + rect.height / 2;
			g2d.drawLine(x1, y, x2, y);
		}
	}
	
	boolean cell_empty;
}
