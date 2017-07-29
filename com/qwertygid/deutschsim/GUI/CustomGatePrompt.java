package com.qwertygid.deutschsim.GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;

public class CustomGatePrompt extends JDialog {
	private static final long serialVersionUID = -6362792305855466347L;

	public CustomGatePrompt(final JFrame frame) {
		super(frame, true);
		
		this.frame = frame;
		
		JTabbedPane tabbed_pane = new JTabbedPane();
		tabbed_pane.add("Rotation", create_rotation_panel());
		tabbed_pane.add("Phase Shift", create_phase_shift_panel());
		tabbed_pane.add("Matrix", create_matrix_panel());
		
		tabbed_pane.addChangeListener(new TabbedPaneListener(tabbed_pane));
		
		option_pane = new JOptionPane(tabbed_pane, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_OPTION,
				null, new Object[] {"Create Gate"});
		
		setTitle("Create Custom Gate");
		setResizable(false);
		setContentPane(option_pane);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
	}
	
	public void show_prompt() {
		setLocationRelativeTo(frame);
		setVisible(true);
	}
	
	private JPanel create_rotation_panel() {
		JPanel rotation_panel = new JPanel();
		rotation_panel.setLayout(new BorderLayout());
		
		JPanel text_editor_panel = new JPanel();
		text_editor_panel.setLayout(new BoxLayout(text_editor_panel, BoxLayout.Y_AXIS));
		
		x_rot = new TextEditor("X axis rotation angle", TextEditor.Type.SINGLE_LINE);
		text_editor_panel.add(x_rot);
		
		y_rot = new TextEditor("Y axis rotation angle", TextEditor.Type.SINGLE_LINE);
		text_editor_panel.add(y_rot);
		
		z_rot = new TextEditor("Z axis rotation angle", TextEditor.Type.SINGLE_LINE);
		text_editor_panel.add(z_rot);
		
		rotation_panel.add(text_editor_panel, BorderLayout.NORTH);
		
		rot_selection = new AngleTypeSelection("What to represent angles in?");
		rotation_panel.add(rot_selection, BorderLayout.SOUTH);
		
		return rotation_panel;
	}
	
	private JPanel create_phase_shift_panel() {
		JPanel phase_shift_panel = new JPanel();
		phase_shift_panel.setLayout(new BorderLayout());
		
		phase = new TextEditor("Phase", TextEditor.Type.SINGLE_LINE);
		phase_shift_panel.add(phase, BorderLayout.NORTH);
		
		phase_selection = new AngleTypeSelection("What to represent phase in?");
		phase_shift_panel.add(phase_selection, BorderLayout.SOUTH);
		
		return phase_shift_panel;
	}
	
	private JPanel create_matrix_panel() {
		JPanel matrix_panel = new JPanel();
		matrix_panel.setLayout(new BorderLayout());
		
		JScrollPane scroll_pane = new JScrollPane();
		
		matrix = new TextEditor("Gate's unitary matrix representation",
				TextEditor.Type.MULTIPLE_LINE);
		scroll_pane.setViewportView(matrix);
		
		matrix_panel.add(scroll_pane, BorderLayout.CENTER);
		
		matrix_selection = new AngleTypeSelection("What to represent arguments of " + 
				"trigonometric functions in?");
		matrix_panel.add(matrix_selection, BorderLayout.SOUTH);
		
		return matrix_panel;
	}
		
	private final JFrame frame;
	private final JOptionPane option_pane;
	
	private TextEditor x_rot, y_rot, z_rot;
	private AngleTypeSelection rot_selection;
	
	private TextEditor phase;
	private AngleTypeSelection phase_selection;
	
	private TextEditor matrix;
	private AngleTypeSelection matrix_selection;
	
	private static class TextEditor extends JPanel {
		private static final long serialVersionUID = 6799884214616868976L;
		
		public enum Type {
			SINGLE_LINE, MULTIPLE_LINE
		}
		
		public TextEditor(final String bg_text, final Type type) {
			this.bg_text = bg_text;
			
			if (type == Type.SINGLE_LINE)
				component = new JTextField();
			else
				component = new JTextPane();
			
			set_background_text();
			component.addFocusListener(new FocusListener() {
				@Override
				public void focusGained(FocusEvent ev) {
					clear_background_text();
				}

				@Override
				public void focusLost(FocusEvent ev) {
					set_background_text();
				}
			});
			
			setLayout(new BorderLayout());
			add(component, BorderLayout.CENTER);
		}
		
		public String get_text() {
			return component.getText();
		}
		
		private void clear_background_text() {
			if (component.getText().equals(bg_text) &&
					component.getForeground().equals(Color.GRAY)) {
				component.setForeground(Color.BLACK);
				component.setText("");
			}
		}
		
		private void set_background_text() {
			if (component.getText().isEmpty()) {
				component.setText(bg_text);
				component.setForeground(Color.GRAY);
			}
		}
		
		private final String bg_text;
		private final JTextComponent component;
	}
	
	private static class AngleTypeSelection extends JPanel{
		private static final long serialVersionUID = 3493671236699868365L;

		public enum Type {
			DEGREES, RADIANS
		}
		
		public AngleTypeSelection(final String question) {
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			
			JPanel question_panel = new JPanel();
			question_panel.add(new JLabel(question));
			add(question_panel);
			
			JPanel buttons_panel = new JPanel();
			buttons_panel.setLayout(new BoxLayout(buttons_panel, BoxLayout.X_AXIS));
			
			ButtonGroup group = new ButtonGroup();
			
			degrees = new JRadioButton("Degrees", true);
			group.add(degrees);
			buttons_panel.add(degrees);
			
			radians = new JRadioButton("Radians", false);
			group.add(radians);
			buttons_panel.add(radians);
			
			add(buttons_panel);
		}
		
		public Type get_selected_type() {
			return degrees.isSelected() ? Type.DEGREES : Type.RADIANS;
		}
		
		private JRadioButton degrees, radians;
	}
	
	private static class TabbedPaneListener implements ChangeListener {
		public TabbedPaneListener(final JTabbedPane tabbed_pane) {
			this.tabbed_pane = tabbed_pane;
		}
		
		@Override
		public void stateChanged(ChangeEvent ev) {			
			tabbed_pane.requestFocus();
		}
		
		private final JTabbedPane tabbed_pane;
	}
}
