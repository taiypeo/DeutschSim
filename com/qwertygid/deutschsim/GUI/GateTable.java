package com.qwertygid.deutschsim.GUI;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.event.MouseInputListener;

import com.qwertygid.deutschsim.Logic.Circuit;
import com.qwertygid.deutschsim.Logic.Gate;
import com.qwertygid.deutschsim.Logic.Table;
import com.qwertygid.deutschsim.Miscellaneous.Tools;

public class GateTable extends JPanel{
	private static final long serialVersionUID = 3779004937588318481L;
	
	public GateTable(final int gate_table_cell_size, final int gate_table_row_height,
			final JFrame frame) {
		table = new Table<Gate>();		
		this.gate_table_cell_size = gate_table_cell_size;
		this.gate_table_row_height = gate_table_row_height;
		this.gate_table_col_width = gate_table_cell_size + 2;
		dot_image = new ImageIcon(getClass().getResource(Tools.dot_image_path));
		
		setBackground(Color.WHITE);
		
		handler = new MouseHandler(this);
		addMouseListener(handler);
		addMouseMotionListener(handler);
		
		setTransferHandler(new GateTableTransferHandler(frame, this));
		
		table.add_col();
		update_size();
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
					
					g2d.setColor(Color.WHITE);
					g2d.fillRect(x + 1, y + 1, gate_table_cell_size - 1, gate_height - 1);
					
					g2d.setColor(Color.BLACK);
					g2d.drawRect(x, y, gate_table_cell_size, gate_height);
					
					g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					
					if (gate.get_id().equals(Tools.CONTROL_ID))
						dot_image.paintIcon(this, g2d, x, y);
					else {
						g2d.setFont(Tools.gate_font);
						
						FontRenderContext frc = g2d.getFontRenderContext();
						final int text_width = (int) Tools.gate_font.getStringBounds(gate.get_id(), frc).
								getWidth();
						
						LineMetrics lm = Tools.gate_font.getLineMetrics(gate.get_id(), frc);
						final int text_height = (int) (lm.getAscent() + lm.getDescent());
						
						final int text_x = x + (gate_table_cell_size - text_width) / 2,
								text_y = (int) (y + (gate_height + text_height) / 2 - lm.getDescent());
						
						// TODO add text cut-off
						g2d.drawString(gate.get_id(), text_x, text_y);
					}
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
		revalidate();
	}
	
	public void set_table(final Table<Gate> table) {
		if (!table.valid())
			throw new RuntimeException("Provided table is not valid");
		
		this.table = table;
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
	
	private Table<Gate> table;
	private final int gate_table_cell_size, gate_table_row_height, gate_table_col_width;
	
	private final ImageIcon dot_image;
	
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
				
				if (table.get_table().get_element(row, col) != null) {
					table.get_table().remove_element(row, col);
					
					if (table.get_table().get_col_count() > 1 &&
							col == table.get_table().get_col_count() - 2 &&
							table.get_table().is_col_empty(col)) {
						
						// Delete unnecessary empty columns
						for (int current_col = col; current_col >= 0; current_col--)
							if (table.get_table().is_col_empty(current_col))
								table.get_table().remove_last_col();
							else
								break;
						
						table.update_size();
					}
				}
				
				table.repaint();
			}
		}

		@Override
		public void mouseEntered(MouseEvent ev) {
			last_mouse_point = ev.getPoint();
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
	
	private static class GateTableTransferHandler extends TransferHandler {
		private static final long serialVersionUID = 1978163502085092717L;
		
		public GateTableTransferHandler(final JFrame frame, final GateTable table) {
			this.frame = frame;
			this.table = table;
		}
		
		@Override
		public boolean canImport(TransferSupport support) {
			return support.isDataFlavorSupported(GateTransferable.GATE_DATA_FLAVOR);
		}
		
		@Override
		public boolean importData(TransferSupport support) {
			if (canImport(support)) {
				try {
					Transferable transferable = support.getTransferable();
					Object value = transferable.getTransferData(GateTransferable.GATE_DATA_FLAVOR);
					
					if (value instanceof Gate) {
						Gate gate = (Gate) value;
						Point drop_location = support.getDropLocation().getDropPoint();
						final int row = drop_location.y / table.get_gate_table_row_height(),
								col = drop_location.x / table.get_gate_table_col_width();
						
						table.get_table().insert_element(gate, row, col);
						try {
							// Circuit constructor automatically calls valid() on itself
							// and throws an exception if valid() returns false
							new Circuit(table.get_table());
						} catch(RuntimeException ex) {
							table.get_table().remove_element(row, col);
							return false;
						}
						
						if (col == table.get_table().get_col_count() - 1) {
							table.get_table().add_col();
							table.update_size();
						}
						
						table.repaint();
					}
				} catch (UnsupportedFlavorException | IOException | NullPointerException ex) {
					Tools.error(frame, "Failed to import data into the gate table");
				}
			}
			
			return false;
		}
		
		private final JFrame frame;
		private final GateTable table;
	}
}
