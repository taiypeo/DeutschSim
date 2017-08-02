package com.qwertygid.deutschsim.GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

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

import org.apache.commons.math3.complex.Complex;

import com.qwertygid.deutschsim.Logic.Gate;
import com.qwertygid.deutschsim.Math.Interpreter;
import com.qwertygid.deutschsim.Math.LexicalAnalyzer;
import com.qwertygid.deutschsim.Miscellaneous.Tools;

public class CustomGatePrompt extends JDialog {
	private static final long serialVersionUID = -6362792305855466347L;

	public CustomGatePrompt(final JFrame frame) {
		super(frame, true);
		
		this.frame = frame;
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		gate_name = new TextEditor("Gate name", TextEditor.Type.SINGLE_LINE);
		panel.add(gate_name);
		
		tabbed_pane = new JTabbedPane();
		
		create_rotation_panel();
		tabbed_pane.add("Rotation", rotation_panel);
		
		create_phase_shift_panel();
		tabbed_pane.add("Phase Shift", phase_shift_panel);
		
		create_matrix_panel();
		tabbed_pane.add("Matrix", matrix_panel);
		
		tabbed_pane.addChangeListener(new TabbedPaneListener(tabbed_pane));
		panel.add(tabbed_pane);
		
		trig_selection = new AngleTypeSelection("What to represent arguments of " + 
				"trigonometric functions in?");
		panel.add(trig_selection);
		
		option_pane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_OPTION,
				null, new Object[] {"Create Gate"});
		
		setTitle("Create Custom Gate");
		setResizable(false);
		setContentPane(option_pane);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		
		option_pane.addPropertyChangeListener(new OKButtonListener());
	}
	
	public Gate show_prompt() {
		setLocationRelativeTo(frame);
		setVisible(true);
		
		return result_gate;
	}
	
	private void create_rotation_panel() {
		rotation_panel = new JPanel();
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
	}
	
	private void create_phase_shift_panel() {
		phase_shift_panel = new JPanel();
		phase_shift_panel.setLayout(new BorderLayout());
		
		phase = new TextEditor("Phase", TextEditor.Type.SINGLE_LINE);
		phase_shift_panel.add(phase, BorderLayout.NORTH);
		
		phase_selection = new AngleTypeSelection("What to represent phase in?");
		phase_shift_panel.add(phase_selection, BorderLayout.SOUTH);
	}
	
	private void create_matrix_panel() {
		matrix_panel = new JPanel();
		matrix_panel.setLayout(new BorderLayout());
		
		JScrollPane scroll_pane = new JScrollPane();
		
		matrix = new TextEditor("Gate's unitary matrix representation",
				TextEditor.Type.MULTIPLE_LINE);
		scroll_pane.setViewportView(matrix);
		
		matrix_panel.add(scroll_pane, BorderLayout.CENTER);
	}
		
	private final JFrame frame;
	private final JOptionPane option_pane;
	
	private TextEditor gate_name;
	
	private JTabbedPane tabbed_pane;
	
	private JPanel rotation_panel;	
	private TextEditor x_rot, y_rot, z_rot;
	private AngleTypeSelection rot_selection;
	
	private JPanel phase_shift_panel;
	private TextEditor phase;
	private AngleTypeSelection phase_selection;
	
	private JPanel matrix_panel;
	private TextEditor matrix;
	
	private AngleTypeSelection trig_selection;
	
	private Gate result_gate;
	
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
		
		public AngleTypeSelection(final String question) {
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			
			JPanel question_panel = new JPanel();
			question_panel.add(new JLabel(question));
			add(question_panel);
			
			JPanel buttons_panel = new JPanel();
			buttons_panel.setLayout(new BoxLayout(buttons_panel, BoxLayout.X_AXIS));
			
			ButtonGroup group = new ButtonGroup();
			
			radians = new JRadioButton("Radians", true);
			group.add(radians);
			buttons_panel.add(radians);
			
			degrees = new JRadioButton("Degrees", false);
			group.add(degrees);
			buttons_panel.add(degrees);
			
			add(buttons_panel);
		}
		
		public Tools.AngleType get_selected_type() {
			return degrees.isSelected() ? Tools.AngleType.DEGREES : Tools.AngleType.RADIANS;
		}
		
		private JRadioButton radians, degrees;
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
	
	private class OKButtonListener implements PropertyChangeListener {
		@Override
		public void propertyChange(PropertyChangeEvent ev) {
			final String prop = ev.getPropertyName();
			if (isVisible()
					&& ev.getSource() == option_pane
					&& (JOptionPane.VALUE_PROPERTY.equals(prop)
					|| JOptionPane.INPUT_VALUE_PROPERTY.equals(prop))) {
				if (option_pane.getValue() == JOptionPane.UNINITIALIZED_VALUE)
					return;
				option_pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
				
				final Interpreter interpreter = new Interpreter(trig_selection.get_selected_type());
				
				final Component selected_panel = tabbed_pane.getSelectedComponent();
				if (selected_panel == rotation_panel)
					create_rotation_gate(interpreter);
				else if (selected_panel == phase_shift_panel) {
					create_phase_shift_gate(interpreter);
				} else if (selected_panel == matrix_panel) {
					
				} else
					throw new RuntimeException("Selected tab is not listed in the tabbed panel");
			}
		}
		
		private void create_rotation_gate(final Interpreter interpreter) {	
			String x_rot_text = x_rot.get_text(), y_rot_text = y_rot.get_text(),
					z_rot_text = z_rot.get_text();
			if (x_rot_text.equals("X axis rotation angle"))
				x_rot_text = "0";
			if (y_rot_text.equals("Y axis rotation angle"))
				y_rot_text = "0";
			if (z_rot_text.equals("Z axis rotation angle"))
				z_rot_text = "0";
			
			Complex x_rot_complex, y_rot_complex, z_rot_complex;			
			try {
				interpreter.set_lexical_analyzer(new LexicalAnalyzer(x_rot_text));
				x_rot_complex = interpreter.interpret();
				
				interpreter.set_lexical_analyzer(new LexicalAnalyzer(y_rot_text));
				y_rot_complex = interpreter.interpret();
				
				interpreter.set_lexical_analyzer(new LexicalAnalyzer(z_rot_text));
				z_rot_complex = interpreter.interpret();
			} catch (RuntimeException ex) {
				Tools.error(frame, "A runtime exception has been caught:\n" +
						ex.getMessage());
				return;
			}
			
			if (x_rot_complex.equals(y_rot_complex)
					&& y_rot_complex.equals(z_rot_complex)
					&& z_rot_complex.equals(Complex.ZERO)) {
				dispose();
				return;
			}
			
			if (!Tools.equal(x_rot_complex.getImaginary(), 0.0)
					|| !Tools.equal(y_rot_complex.getImaginary(), 0.0)
					|| !Tools.equal(z_rot_complex.getImaginary(), 0.0)) {
				Tools.error(frame, "Rotation angles cannot be complex numbers");
				return;
			}
			
			String name = gate_name.get_text();
			if (name.equals("Gate name"))
				name = "G";
			
			final double x_rot_angle = Tools.round_if_needed(x_rot_complex.getReal()),
					y_rot_angle = Tools.round_if_needed(y_rot_complex.getReal()),
					z_rot_angle = Tools.round_if_needed(z_rot_complex.getReal());
			
			try {
				result_gate = new Gate(name, x_rot_angle, y_rot_angle,
						z_rot_angle, rot_selection.get_selected_type());
			} catch (RuntimeException ex) {
				Tools.error(frame, "A runtime exception has been caught:\n" +
						ex.getMessage());
				return;
			}
			
			dispose();
		}
		
		private void create_phase_shift_gate(final Interpreter interpreter) {
			String phase_text = phase.get_text();
			if (phase_text.equals("Phase"))
				phase_text = "0";
			
			Complex phase_complex;
			try {
				interpreter.set_lexical_analyzer(new LexicalAnalyzer(phase_text));
				phase_complex = interpreter.interpret();
			} catch (RuntimeException ex) {
				Tools.error(frame, "A runtime exception has been caught:\n" +
						ex.getMessage());
				return;
			}
			
			if (phase_complex.equals(Complex.ZERO)) {
				dispose();
				return;
			}
			
			if (!Tools.equal(phase_complex.getImaginary(), 0.0)) {
				Tools.error(frame, "Phase cannot be a complex number");
				return;
			}
			
			String name = gate_name.get_text();
			if (name.equals("Gate name"))
				name = "G";
			
			final double phase_shift = Tools.round_if_needed(phase_complex.getReal());
			try {
				result_gate = new Gate(name, phase_shift, phase_selection.get_selected_type());
			} catch (RuntimeException ex) {
				Tools.error(frame, "A runtime exception has been caught:\n" +
						ex.getMessage());
				return;
			}
			
			dispose();
		}
	}
}
