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

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.linear.FieldVector;

import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
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
import java.io.File;
import java.io.IOException;
import java.awt.Font;

import com.qwertygid.deutschsim.IO.Serializer;
import com.qwertygid.deutschsim.Logic.Circuit;
import com.qwertygid.deutschsim.Logic.Gate;
import com.qwertygid.deutschsim.Logic.StandardGateCreator;
import com.qwertygid.deutschsim.Miscellaneous.Tools;

public class GUI {	
	public GUI() {
		frame = new JFrame("DeutschSim - Untitled");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(640, 480);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			SwingUtilities.updateComponentTreeUI(frame);
		} catch (Exception e) {
			Tools.error(frame, "Failed to set up the system look & feel, using" +
					"the standard Swing look & feel");
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
		
		initial_state_table = new QubitTable(gate_table_row_height, initial_state_table_column_width);		
		GridBagConstraints gbc_initial_state_table = new GridBagConstraints();
		gbc_initial_state_table.gridx = 0;
		gbc_initial_state_table.gridy = 0;
		quantum_system_panel.add(initial_state_table, gbc_initial_state_table);
		
		gate_table = new GateTable(gate_table_cell_size, gate_table_row_height);
		gate_table.setTransferHandler(new GateTableTransferHandler(frame, gate_table));
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
		
		result_text_pane = new JTextPane();
		result_text_pane.setEditable(false);
		result_panel.add(result_text_pane);
		
		JPanel checkbox_panel = new JPanel();
		result_panel.add(checkbox_panel);
		
		show_all_checkbox = new JCheckBox("Show all");
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
				frame.setTitle("DeutschSim - Untitled");
				current_file = null;
			}
		});
		item_new.setAccelerator(KeyStroke.getKeyStroke('N', menu_mask));
		file_menu.add(item_new);
		
		JMenuItem item_open = new JMenuItem(new AbstractAction("Open") {
			private static final long serialVersionUID = -4441750652720636192L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser file_chooser = new JFileChooser();
				file_chooser.setCurrentDirectory(new File("."));
				
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						"DeutschSim circuits", "dcirc");
				file_chooser.setFileFilter(filter);
				
				final int return_value = file_chooser.showOpenDialog(frame);
				if (return_value == JFileChooser.APPROVE_OPTION) {
					File file = file_chooser.getSelectedFile();
					Serializer serializer = null;
					try {
						// Serializer constructor automatically calls valid() on itself
						serializer = new Serializer(file);
					} catch (RuntimeException | IOException ex) {
						Tools.error(frame, "An exception has been caught:\n" +
								ex.getMessage());
						return;
					}
					
					initial_state_table.set_qubits(serializer.get_qubit_sequence());
					initial_state_table.update_table();
					
					gate_table.set_table(serializer.get_circuit().get_gates_table());
					gate_table.update_size();
					
					current_file = file;
					frame.setTitle("DeutschSim - " + current_file.getName());
				}
			}
		});
		item_open.setAccelerator(KeyStroke.getKeyStroke('O', menu_mask));
		file_menu.add(item_open);
		
		JMenuItem item_save = new JMenuItem(new AbstractAction("Save") {
			private static final long serialVersionUID = -4441750652720636192L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (current_file == null) {
					JFileChooser file_chooser = new JFileChooser() {
						private static final long serialVersionUID = 4649847794719144813L;
						
						@Override
						public void approveSelection() {
							File file = getSelectedFile();
							if (file.exists()) {
								final int result = JOptionPane.showConfirmDialog(this,
										"The file exists, overwrite?", "File exists",
										JOptionPane.YES_NO_OPTION);
								if (result == JOptionPane.YES_OPTION)
									super.approveSelection();
								
								return;
							}
							
							super.approveSelection();
						}
					};
					file_chooser.setCurrentDirectory(new File("."));
					file_chooser.setSelectedFile(new File(".dcirc"));
					
					FileNameExtensionFilter filter = new FileNameExtensionFilter(
							"DeutschSim circuits", "dcirc");
					file_chooser.setFileFilter(filter);
					
					final int return_value = file_chooser.showSaveDialog(frame);
					if (return_value == JFileChooser.APPROVE_OPTION) {
						current_file = file_chooser.getSelectedFile();
						frame.setTitle("DeutschSim - " + current_file.getName());
					}
				}
				
				save();
			}
			
			private void save() {
				try {
					Serializer serializer = new Serializer(initial_state_table.get_qubits(),
							new Circuit(gate_table.get_table()));
					serializer.serialize(current_file);
				} catch (RuntimeException|IOException ex) {
					Tools.error(frame, "An exception has been caught:\n" +
								ex.getMessage());
				}
			}
		});
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
		
		JMenuItem item_simulate = new JMenuItem(new AbstractAction("Simulate") {
			private static final long serialVersionUID = 8549028014281850661L;

			@Override
			public void actionPerformed(ActionEvent arg0) {	
				try {
					Circuit circuit = new Circuit(gate_table.get_table());
					FieldVector<Complex> results = circuit.operate(initial_state_table.get_qubits());
					
					StringBuilder text = new StringBuilder("Simulation results:\n");
					
					final int qubits_number = Integer.toBinaryString(results.getDimension() - 1).length();
					for (int index = 0; index < results.getDimension(); index++) {
						final Complex current = results.getEntry(index);
						final double current_magnitude = current.abs();
						
						if (!show_all_checkbox.isSelected() && Tools.equal(current_magnitude, 0))
							continue;
						
						double current_percentage = Math.pow(current_magnitude, 2) * 100;
						if (Tools.equal(current_percentage, Math.round(current_percentage)))
							current_percentage = Math.round(current_percentage);
						
						StringBuilder qubits_values = new StringBuilder(Integer.toBinaryString(index));
						for (int length = qubits_values.length(); length < qubits_number; length++)
							qubits_values.insert(0, '0');
						
						text.append(current.getReal() + (current.getImaginary() < 0 ? "" : "+") +
								current.getImaginary() + "i |" + qubits_values + ">\t" +
								current_percentage + "% chance\n");
					}
					
					result_text_pane.setText(text.toString());
					
				} catch (RuntimeException ex) {
					Tools.error(frame, "A runtime exception has been caught:\n" + ex.getMessage());
					ex.printStackTrace();
				}
			}
		});
		item_simulate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0));
		circuit_menu.add(item_simulate);
		
		JMenuItem item_change_qubits = new JMenuItem(new AbstractAction("Change Qubits") {
			private static final long serialVersionUID = 8549028014281850661L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String new_qubits = (String) JOptionPane.showInputDialog(frame, "Enter a qubit sequence:",
						"Change Qubits", JOptionPane.PLAIN_MESSAGE);
				if (new_qubits == null)
					return;
				else if (!new_qubits.matches("[01]+")) {
					Tools.error(frame, "The provided string is not a valid qubit sequence.\n" +
							"A valid qubit sequence contains one or more '0' or '1' characters.");
					
					return;
				}
				
				initial_state_table.set_qubits(new_qubits);
				initial_state_table.update_table();
				
				// TODO maybe instead of emptying the whole table this function should instead
				// append/remove rows?
				gate_table.get_table().empty();
				
				for (int qubit = 0; qubit < initial_state_table.get_qubits().length(); qubit++)
					gate_table.get_table().add_row();
				
				gate_table.get_table().add_col();
				gate_table.update_size();
								
			}
		});
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
	private QubitTable initial_state_table;
	private GateTable gate_table;
	private JTextPane result_text_pane;
	private JCheckBox show_all_checkbox;
	
	private File current_file;
	
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
