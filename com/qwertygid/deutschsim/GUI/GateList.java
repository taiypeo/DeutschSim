package com.qwertygid.deutschsim.GUI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;
import javax.swing.border.LineBorder;

import com.qwertygid.deutschsim.Logic.Gate;
import com.qwertygid.deutschsim.Logic.StandardGateCreator;
import com.qwertygid.deutschsim.Miscellaneous.Tools;

public class GateList extends JList<Gate>{
	private static final long serialVersionUID = -811493642506545716L;

	public GateList(final int element_size) {	
		DefaultListModel<Gate> list_model = new DefaultListModel<Gate>();
		list_model.addElement(StandardGateCreator.create_pauli_x());
		list_model.addElement(StandardGateCreator.create_pauli_y());
		list_model.addElement(StandardGateCreator.create_pauli_z());
		list_model.addElement(StandardGateCreator.create_hadamard());
		list_model.addElement(StandardGateCreator.create_control());
		
		setModel(list_model);
		setTransferHandler(new GateListTransferHandler());
		setDragEnabled(true);
		setLayoutOrientation(JList.HORIZONTAL_WRAP);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setVisibleRowCount(0);
		setFixedCellHeight(element_size + 10);
		setFixedCellWidth(element_size + 10);
		setCellRenderer(new GateListCellRenderer(element_size));
		setFont(new Font("Tahoma", Font.PLAIN, 20));
	}
	
	private static class GateListCellRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = 6442140178911177597L;
		
		public GateListCellRenderer(final int element_size) {
			super.setHorizontalAlignment(SwingConstants.CENTER);
			
			dot_image = new ImageIcon(getClass().getResource(Tools.dot_image_path));
			this.element_size = element_size;
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
					label.setPreferredSize(new Dimension(element_size, element_size));
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
		
		private final int element_size;
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
}
