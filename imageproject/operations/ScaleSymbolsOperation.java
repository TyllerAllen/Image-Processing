package imageproject.operations;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import imageproject.gui.FlowPanel;
import imageproject.images.ImageNode;
import imageproject.images.ImageObject;

public class ScaleSymbolsOperation implements Operation {
	private int[] dim;
	
	FlowPanel p = new FlowPanel('y');

	JTextField tf[] = new JTextField[2];
	private ImageNode source;
	
	public ScaleSymbolsOperation(int d[], ImageNode source) {
		this.dim = d;
		this.source = source;
		
		p = new FlowPanel('y');

		JLabel comp = new JLabel("Dimensions");

		p.add(comp);
		
		FlowPanel row = new FlowPanel('x');

		for (int x = 0; x < 2; x++) {

			JTextField t = new JTextField(3);
			tf[x] = t;

			t.setMinimumSize(new Dimension(40, 15));

			t.setText(Integer.toString(dim[x]));

			row.add(t);
			t.getDocument().addDocumentListener(new FilterCellListener(t, x));
		}
		p.add(row);
	}
	
	public void perform(ImageObject source, ArrayList<ImageObject> dest) {


		BufferedImage t = source.getThumb();
		int edge = Math.max(t.getWidth(), t.getHeight());
		dest.add(new ImageObject(source.getMap().scaleSymbols(dim[0], dim[1]), edge));

	}
	
	public JPanel getPanel() {
		return p;
	}
	
	class FilterCellListener implements DocumentListener {

		private int x;
		private JTextField t;

		FilterCellListener(JTextField t, int x) {
			this.t = t;
			this.x = x;

		}

		public void insertUpdate(DocumentEvent e) {
			update();
		}

		public void removeUpdate(DocumentEvent e) {
			update();
		}

		public void changedUpdate(DocumentEvent e) {
			update();
		}

		private void update() {
			String text = t.getText();

			int i;
			try {
				i = Integer.parseInt(text);
			} catch (NumberFormatException e) {
				i = 0;
			}

			if (i != dim[x]) {
				dim[x] = i;
			}
			source.performOperation();

			p.getParent().repaint();

		}

	}
}