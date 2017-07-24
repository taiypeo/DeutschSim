package com.qwertygid.deutschsim.GUI;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.awt.Font;

import com.qwertygid.deutschsim.Logic.Gate;
import com.qwertygid.deutschsim.Logic.StandardGateCreator;
import com.qwertygid.deutschsim.Miscellaneous.Tools;

public class GUI {	
	public GUI() {
		frame = new JFrame("DeutschSim");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(640, 480);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			SwingUtilities.updateComponentTreeUI(frame);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(frame, "Failed to set up the system look & feel, using the standard Swing look & feel",
					"Error", JOptionPane.ERROR_MESSAGE);
		}
		
		setup();
		frame.setVisible(true);
	}
	
	public void setup() {		
		JSplitPane main_split_pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		main_split_pane.setResizeWeight(main_split_pane_resize_weight);
		frame.getContentPane().add(main_split_pane);
		
		JSplitPane child_split_pane = new JSplitPane();
		child_split_pane.setResizeWeight(child_split_pane_resize_weight);
		main_split_pane.setLeftComponent(child_split_pane);
		
		JScrollPane quantum_system_scroll_pane = new JScrollPane();
		child_split_pane.setLeftComponent(quantum_system_scroll_pane);
		
		JPanel quantum_system_panel = new JPanel();
		quantum_system_scroll_pane.setViewportView(quantum_system_panel);
		
		JTable initial_state_table = new JTable() {
			private static final long serialVersionUID = 1127708984190699322L;

			@Override
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		};
		initial_state_table.setModel(new DefaultTableModel(
			new Object[][] {
				{"|0>"},
				{"|0>"},
				{"|0>"},
				{"|0>"},
				{"|0>"},
				{"|0>"},
			},
			new String[] {
				""
			}
		));
		initial_state_table.setFont(new Font("Tahoma", Font.PLAIN, 12));
		initial_state_table.setRowHeight(gate_table_row_height);
		initial_state_table.setFocusable(false);
		initial_state_table.setRowSelectionAllowed(false);
		initial_state_table.setShowGrid(false);
		initial_state_table.setIntercellSpacing(new Dimension(0, 0));
		initial_state_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		initial_state_table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		initial_state_table.setTableHeader(null);
		for (int col = 0; col < initial_state_table.getColumnCount(); col++)
			initial_state_table.getColumnModel().getColumn(col).setPreferredWidth(initial_state_table_column_width);
		GridBagConstraints gbc_initial_state_table = new GridBagConstraints();
		gbc_initial_state_table.gridx = 0;
		gbc_initial_state_table.gridy = 0;
		quantum_system_panel.add(initial_state_table, gbc_initial_state_table);
		
		GateTable gate_table = new GateTable(gate_table_cell_size, gate_table_row_height);
		gate_table.setTransferHandler(new GateTableTransferHandler(frame, gate_table));
		for (int i = 0; i < 6; i++)
			gate_table.get_table().add_row();
		for (int i = 0; i < 4; i++)
			gate_table.get_table().add_col();
		gate_table.update_size();
		GridBagConstraints gbc_gate_table = new GridBagConstraints();
		gbc_gate_table.gridx = 1;
		gbc_gate_table.gridy = 0;
		quantum_system_panel.add(gate_table, gbc_gate_table);
		
		GridBagLayout gbl_quantum_system_panel = new GridBagLayout();
		gbl_quantum_system_panel.columnWidths = new int[]{initial_state_table.getWidth(), gate_table.getWidth(), 0};
		gbl_quantum_system_panel.rowHeights = new int[]{initial_state_table.getHeight(), 0};
		gbl_quantum_system_panel.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_quantum_system_panel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		quantum_system_panel.setLayout(gbl_quantum_system_panel);
		
		JScrollPane result_scroll_pane = new JScrollPane();
		child_split_pane.setRightComponent(result_scroll_pane);
		
		JPanel result_panel = new JPanel();
		result_panel.setLayout(new BoxLayout(result_panel, BoxLayout.Y_AXIS));
		result_scroll_pane.setViewportView(result_panel);
		
		JTextPane result_text_pane = new JTextPane();
		result_text_pane.setEditable(false);
		result_panel.add(result_text_pane);
		
		JPanel checkbox_panel = new JPanel();
		result_panel.add(checkbox_panel);
		
		JCheckBox show_all_checkbox = new JCheckBox("Show all");
		checkbox_panel.add(show_all_checkbox);
		
		JScrollPane scrollPane = new JScrollPane();
		result_panel.add(scrollPane);
		
		JScrollPane list_scroll_pane = new JScrollPane();
		main_split_pane.setRightComponent(list_scroll_pane);
		
		DefaultListModel<Gate> list_model = new DefaultListModel<Gate>();
		list_model.addElement(StandardGateCreator.create_pauli_x());
		list_model.addElement(StandardGateCreator.create_pauli_y());
		list_model.addElement(StandardGateCreator.create_pauli_z());
		list_model.addElement(StandardGateCreator.create_hadamard());
		list_model.addElement(StandardGateCreator.create_control());
		
		JList<Gate> list = new JList<Gate>(list_model);
		list.setTransferHandler(new GateListTransferHandler());
		list.setDragEnabled(true);
		list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setVisibleRowCount(0);
		list.setFixedCellHeight(gate_table_cell_size + 10);
		list.setFixedCellWidth(gate_table_cell_size + 10);
		list.setCellRenderer(new GateListCellRenderer());
		list.setFont(new Font("Tahoma", Font.PLAIN, 20));
		list_scroll_pane.setViewportView(list);
		
		final int menu_mask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		
		JMenuBar menu_bar = new JMenuBar();
		frame.setJMenuBar(menu_bar);
		
		JMenu file_menu = new JMenu("File");
		menu_bar.add(file_menu);
		
		JMenuItem item_new = new JMenuItem(new AbstractAction("New") {
			private static final long serialVersionUID = 3699016056959009199L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				frame.getContentPane().removeAll();
				setup();
				// TODO add restoration of JSplitPanes' panels' sizes
				frame.validate();
			}
		});
		item_new.setAccelerator(KeyStroke.getKeyStroke('N', menu_mask));
		file_menu.add(item_new);
		
		JMenuItem item_open = new JMenuItem("Open");
		item_open.setAccelerator(KeyStroke.getKeyStroke('O', menu_mask));
		file_menu.add(item_open);
		
		JMenuItem item_save = new JMenuItem("Save");
		item_save.setAccelerator(KeyStroke.getKeyStroke('S', menu_mask));
		file_menu.add(item_save);
		
		JMenuItem item_save_as = new JMenuItem("Save As");
		item_save_as.setAccelerator(KeyStroke.getKeyStroke('S', menu_mask | KeyEvent.SHIFT_DOWN_MASK));
		file_menu.add(item_save_as);
		
		file_menu.addSeparator();
		
		JMenuItem item_load_circuit_gate = new JMenuItem("Load Gate");
		item_load_circuit_gate.setAccelerator(KeyStroke.getKeyStroke('L', menu_mask));
		file_menu.add(item_load_circuit_gate);
		
		file_menu.addSeparator();
		
		JMenuItem item_quit = new JMenuItem(new AbstractAction("Quit") {
			private static final long serialVersionUID = -4441750652720636192L;

			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();				
			}
		});
		item_quit.setAccelerator(KeyStroke.getKeyStroke('Q', menu_mask));
		file_menu.add(item_quit);
		
		JMenu circuit_menu = new JMenu("Circuit");
		menu_bar.add(circuit_menu);
		
		JMenuItem item_simulate = new JMenuItem("Simulate");
		item_simulate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
		circuit_menu.add(item_simulate);
		
		JMenuItem item_change_qubits = new JMenuItem("Change Qubits");
		item_change_qubits.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, 0));
		circuit_menu.add(item_change_qubits);
		
		circuit_menu.addSeparator();
		
		JMenuItem item_create_custom_gate = new JMenuItem("Create Custom Gate");
		item_create_custom_gate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, 0));
		circuit_menu.add(item_create_custom_gate);
		
		JMenu help_menu = new JMenu("Help");
		menu_bar.add(help_menu);
		
		JMenuItem item_about = new JMenuItem("About");
		help_menu.add(item_about);
	}
	
	private JFrame frame;
	
	private static final int gate_table_cell_size = 43, gate_table_row_height = gate_table_cell_size + 2, initial_state_table_column_width = 25;
	private static final double main_split_pane_resize_weight = 0.85, child_split_pane_resize_weight = 0.8;
	
	private static class GateListCellRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = 6442140178911177597L;
		
		public GateListCellRenderer() {
			super.setHorizontalAlignment(SwingConstants.CENTER);
			
			dot_image = new ImageIcon(getClass().getResource(Tools.dot_image_path));
		}

		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean is_selected,
				boolean cell_has_focus) {
			Component component = super.getListCellRendererComponent(list, value, index, is_selected, cell_has_focus);
			
			if (component instanceof JLabel) {	
				JLabel label = (JLabel) component;
				
				if (value instanceof Gate) {
					String text = ((Gate) value).get_id();
					if (text.equals(Tools.CONTROL_ID)) {
						label.setIcon(dot_image);
						label.setText("");
					} else
						label.setText(text);
					label.setPreferredSize(new Dimension(gate_table_cell_size, gate_table_cell_size));
					label.setBorder(new LineBorder(Color.BLACK));
					
					JPanel panel = new JPanel();
					panel.setBackground(Color.WHITE);
					panel.add(component);
					
					return panel;
				}
			}
			
			return null;
		}
		
		private final ImageIcon dot_image;
	}
	
	private static class GateListTransferHandler extends TransferHandler {
		private static final long serialVersionUID = 3774691117252660958L;

		@Override
		public int getSourceActions(JComponent component) {			
			return DnDConstants.ACTION_COPY;
		}
		
		@Override
		public Transferable createTransferable(JComponent component) {
			if (component instanceof JList) {
				JList<?> list = (JList<?>) component;
				Object value = list.getSelectedValue();
				
				if (value instanceof Gate) {
					Gate gate = (Gate) value;
					return new GateTransferable(gate);
				}
			}
			
			return null;
		}
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
						table.repaint();
					}
				} catch (UnsupportedFlavorException | IOException | NullPointerException ex) {
					JOptionPane.showMessageDialog(frame, "Failed to import data into the gate table",
							"Error", JOptionPane.ERROR_MESSAGE);
				}
			}
			
			return false;
		}
		
		private final JFrame frame;
		private final GateTable table;
	}
}
