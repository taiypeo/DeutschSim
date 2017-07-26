package com.qwertygid.deutschsim.GUI;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JPanel;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.linear.FieldVector;

import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import com.qwertygid.deutschsim.IO.Serializer;
import com.qwertygid.deutschsim.Logic.Circuit;
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
		JSplitPane main_split_pane = create_main_split_pane();
		
		JSplitPane child_split_pane = setup_main_split_pane_top(main_split_pane);
		setup_main_split_pane_bottom(main_split_pane);
		
		setup_child_split_pane_left(child_split_pane);
		setup_child_split_pane_right(child_split_pane);
		
		JMenuBar menu_bar = create_menu_bar();
		
		JMenu file_menu = create_menu("File", menu_bar);
		add_item_new(file_menu);
		add_item_open(file_menu);
		add_item_save(file_menu);
		add_item_save_as(file_menu);
		file_menu.addSeparator();
		add_item_load_circuit_gate(file_menu);
		file_menu.addSeparator();
		add_item_quit(file_menu);

		JMenu circuit_menu = create_menu("Circuit", menu_bar);
		add_item_simulate(circuit_menu);
		add_item_change_qubits(circuit_menu);		
		circuit_menu.addSeparator();
		add_item_create_custom_gate(circuit_menu);
		
		JMenu help_menu = create_menu("Help", menu_bar);;
		add_item_about(help_menu);
	}
	
	// Window GUI setup functions
	
	private JSplitPane create_main_split_pane() {
		JSplitPane main_split_pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		main_split_pane.setResizeWeight(main_split_pane_resize_weight);
		frame.getContentPane().add(main_split_pane);
		
		return main_split_pane;
	}
	
	private JSplitPane setup_main_split_pane_top(final JSplitPane split_pane) {
		JSplitPane child_split_pane = new JSplitPane();
		child_split_pane.setResizeWeight(child_split_pane_resize_weight);
		split_pane.setLeftComponent(child_split_pane);
		
		return child_split_pane;
	}
	
	private void setup_main_split_pane_bottom(final JSplitPane split_pane) {
		JScrollPane list_scroll_pane = new JScrollPane();
		split_pane.setRightComponent(list_scroll_pane);
		
		gate_list = new GateList(gate_table_cell_size);
		list_scroll_pane.setViewportView(gate_list);
	}
	
	private void setup_child_split_pane_left(final JSplitPane split_pane) {
		JScrollPane quantum_system_scroll_pane = new JScrollPane();
		split_pane.setLeftComponent(quantum_system_scroll_pane);
		
		JPanel quantum_system_panel = new JPanel();
		quantum_system_scroll_pane.setViewportView(quantum_system_panel);
		
		qubit_table = new QubitTable(gate_table_row_height);		
		GridBagConstraints gbc_qubit_table = new GridBagConstraints();
		gbc_qubit_table.gridx = 0;
		gbc_qubit_table.gridy = 0;
		quantum_system_panel.add(qubit_table, gbc_qubit_table);
		
		gate_table = new GateTable(gate_table_cell_size, gate_table_row_height, frame);
		GridBagConstraints gbc_gate_table = new GridBagConstraints();
		gbc_gate_table.gridx = 1;
		gbc_gate_table.gridy = 0;
		quantum_system_panel.add(gate_table, gbc_gate_table);
		
		GridBagLayout gbl_quantum_system_panel = new GridBagLayout();
		gbl_quantum_system_panel.columnWidths = new int[]{qubit_table.getWidth(), gate_table.getWidth(), 0};
		gbl_quantum_system_panel.rowHeights = new int[]{qubit_table.getHeight(), 0};
		gbl_quantum_system_panel.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_quantum_system_panel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		quantum_system_panel.setLayout(gbl_quantum_system_panel);
	}
	
	private void setup_child_split_pane_right(final JSplitPane split_pane){
		JScrollPane result_scroll_pane = new JScrollPane();
		split_pane.setRightComponent(result_scroll_pane);
		
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
	}
	
	// Menu setup functions
	
	private JMenuBar create_menu_bar() {
		JMenuBar menu_bar = new JMenuBar();
		frame.setJMenuBar(menu_bar);
		
		return menu_bar;
	}
	
	private JMenu create_menu(final String name, final JMenuBar menu_bar) {
		JMenu menu = new JMenu(name);
		menu_bar.add(menu);
		
		return menu;
	}
	
	private void add_item_new(final JMenu file_menu) {
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
	}
	
	private void add_item_open(final JMenu file_menu) {
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
					
					qubit_table.set_qubits(serializer.get_qubit_sequence());
					qubit_table.update_table();
					
					gate_table.set_table(serializer.get_circuit().get_gates_table());
					gate_table.update_size();
					
					current_file = file;
					frame.setTitle("DeutschSim - " + current_file.getName());
					
					result_text_pane.setText("");
				}
			}
		});
		item_open.setAccelerator(KeyStroke.getKeyStroke('O', menu_mask));
		file_menu.add(item_open);
	}
	
	private void add_item_save(final JMenu file_menu) {
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
					Serializer serializer = new Serializer(qubit_table.get_qubits(),
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
	}
	
	private void add_item_save_as(final JMenu file_menu) {
		JMenuItem item_save_as = new JMenuItem("Save As");
		item_save_as.setAccelerator(KeyStroke.getKeyStroke('S', menu_mask | KeyEvent.SHIFT_DOWN_MASK));
		file_menu.add(item_save_as);
	}
	
	private void add_item_load_circuit_gate(final JMenu file_menu) {
		JMenuItem item_load_circuit_gate = new JMenuItem("Load Gate");
		item_load_circuit_gate.setAccelerator(KeyStroke.getKeyStroke('L', menu_mask));
		file_menu.add(item_load_circuit_gate);
	}
	
	private void add_item_quit(final JMenu file_menu) {
		JMenuItem item_quit = new JMenuItem(new AbstractAction("Quit") {
			private static final long serialVersionUID = -4441750652720636192L;

			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();				
			}
		});
		item_quit.setAccelerator(KeyStroke.getKeyStroke('Q', menu_mask));
		file_menu.add(item_quit);
	}
	
	private void add_item_simulate(final JMenu circuit_menu) {
		JMenuItem item_simulate = new JMenuItem(new AbstractAction("Simulate") {
			private static final long serialVersionUID = 8549028014281850661L;

			@Override
			public void actionPerformed(ActionEvent arg0) {	
				try {
					Circuit circuit = new Circuit(gate_table.get_table());
					FieldVector<Complex> results = circuit.operate(qubit_table.get_qubits());
					
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
	}
	
	private void add_item_change_qubits(final JMenu circuit_menu) {
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
				
				qubit_table.set_qubits(new_qubits);
				qubit_table.update_table();
				
				// TODO maybe instead of emptying the whole table this function should instead
				// append/remove rows?
				gate_table.get_table().empty();
				
				for (int qubit = 0; qubit < qubit_table.get_qubits().length(); qubit++)
					gate_table.get_table().add_row();
				
				gate_table.get_table().add_col();
				gate_table.update_size();
								
			}
		});
		item_change_qubits.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, 0));
		circuit_menu.add(item_change_qubits);
	}
	
	private void add_item_create_custom_gate(final JMenu circuit_menu) {
		JMenuItem item_create_custom_gate = new JMenuItem("Create Custom Gate");
		item_create_custom_gate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, 0));
		circuit_menu.add(item_create_custom_gate);
	}
	
	private void add_item_about(final JMenu help_menu) {
		JMenuItem item_about = new JMenuItem(new AbstractAction("About"){
			private static final long serialVersionUID = -8311117685045905144L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				final String text = "DeutschSim by Qwertygid, 2017\n\n" +
						"https://github.com/QwertygidQ/DeutschSim";
				
				// TODO add a logo
				JOptionPane.showMessageDialog(frame, text,
						"About", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		help_menu.add(item_about);
	}
	
	private JFrame frame;
	private QubitTable qubit_table;
	private GateTable gate_table;
	private JTextPane result_text_pane;
	private JCheckBox show_all_checkbox;
	private GateList gate_list;
	
	private File current_file;
	
	private static final int gate_table_cell_size = 43,
			gate_table_row_height = gate_table_cell_size + 2;
	private static final double main_split_pane_resize_weight = 0.85,
			child_split_pane_resize_weight = 0.8;
	private static final int menu_mask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
}
