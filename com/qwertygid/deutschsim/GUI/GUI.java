package com.qwertygid.deutschsim.GUI;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTextPane;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.AbstractListModel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

public class GUI {

	private JFrame frame;
	private GUITable initial_state_table, gate_table;
	
	public GUI() {
		initialize();
	}

	private void initialize() {
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
		
		JSplitPane main_split_pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		main_split_pane.setResizeWeight(0.95);
		frame.getContentPane().add(main_split_pane);
		
		JSplitPane child_split_pane = new JSplitPane();
		child_split_pane.setResizeWeight(0.8);
		main_split_pane.setLeftComponent(child_split_pane);
		
		JScrollPane quantum_system_scroll_pane = new JScrollPane();
		child_split_pane.setLeftComponent(quantum_system_scroll_pane);
		
		JPanel quantum_system_panel = new JPanel();
		quantum_system_scroll_pane.setViewportView(quantum_system_panel);
		
		final int gate_table_size = 43, initial_state_table_column_width = 20;
		
		initial_state_table = new GUITable();
		initial_state_table.setModel(new DefaultTableModel(
			new Object[][] {
				{null},
				{null},
				{null},
			},
			new String[] {
				""
			}
		));
		initial_state_table.setRowHeight(gate_table_size);
		initial_state_table.setColumnPreferredWidth(initial_state_table_column_width);
		GridBagConstraints gbc_initial_state_table = new GridBagConstraints();
		gbc_initial_state_table.gridx = 0;
		gbc_initial_state_table.gridy = 0;
		quantum_system_panel.add(initial_state_table, gbc_initial_state_table);
		
		gate_table = new GUITable();
		gate_table.setModel(new DefaultTableModel(
			new Object[][] {
				{null, null, null, null},
				{null, null, null, null},
				{null, null, null, null},
			},
			new String[] {
				"", "", "", ""
			}
		));
		gate_table.setRowHeight(gate_table_size);
		gate_table.setColumnPreferredWidth(gate_table_size);
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
		
		JList list = new JList();
		list.setDragEnabled(true);
		list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list.setModel(new AbstractListModel() {
			String[] values = new String[] {"X", "Y", "Z", "H", "R2"};
			public int getSize() {
				return values.length;
			}
			public Object getElementAt(int index) {
				return values[index];
			}
		});
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setVisibleRowCount(-1);
		list_scroll_pane.setViewportView(list);
		
		JMenuBar menu_bar = new JMenuBar();
		frame.setJMenuBar(menu_bar);
		
		JMenu file_menu = new JMenu("File");
		menu_bar.add(file_menu);
		
		JMenuItem item_new = new JMenuItem("New");
		file_menu.add(item_new);
		
		JMenuItem item_load = new JMenuItem("Load");
		file_menu.add(item_load);
		
		JMenuItem item_save = new JMenuItem("Save");
		file_menu.add(item_save);
		
		JMenuItem item_save_as = new JMenuItem("Save As");
		file_menu.add(item_save_as);
		
		file_menu.addSeparator();
		
		JMenuItem item_load_circuit_gate = new JMenuItem("Load Circuit Gate");
		file_menu.add(item_load_circuit_gate);
		
		file_menu.addSeparator();
		
		JMenuItem item_quit = new JMenuItem("Quit");
		file_menu.add(item_quit);
		
		JMenu circuit_menu = new JMenu("Circuit");
		menu_bar.add(circuit_menu);
		
		JMenuItem item_simulate = new JMenuItem("Simulate");
		circuit_menu.add(item_simulate);
		
		JMenuItem item_change_qubits = new JMenuItem("Change Qubits");
		circuit_menu.add(item_change_qubits);
		
		circuit_menu.addSeparator();
		
		JMenuItem item_create_custom_gate = new JMenuItem("Create Custom Gate");
		circuit_menu.add(item_create_custom_gate);
		
		JMenu help_menu = new JMenu("Help");
		menu_bar.add(help_menu);
		
		JMenuItem item_about = new JMenuItem("About");
		help_menu.add(item_about);
		
		frame.setVisible(true);
	}

}
