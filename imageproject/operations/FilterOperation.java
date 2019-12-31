package imageproject.operations;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import imageproject.gui.FlowPanel;
import imageproject.images.ImageNode;
import imageproject.images.ImageObject;

public class FilterOperation implements Operation {

	int w;
	int h;

	private double[][] filter;
	private int divisor;

	FlowPanel p;

	JTextField tf[][];
	private ImageNode source;
	private JTextField jf;

	public FilterOperation(double f[][], int div, ImageNode source) {
		this.filter = f;
		this.divisor = div;
		this.source = source;

		w = f[0].length;
		h = f.length;

		tf = new JTextField[w][h];

		p = new FlowPanel('y');

		JLabel comp = new JLabel("Filter");

		p.add(comp);
		for (int y = 0; y < h; y++) {

			FlowPanel row = new FlowPanel('x');

			for (int x = 0; x < w; x++) {

				JTextField t = new JTextField(3);
				tf[x][y] = t;

				t.setMinimumSize(new Dimension(30, 15));

				t.setText(Double.toString(filter[y][x]));

				row.add(t);
				t.getDocument().addDocumentListener(new FilterCellListener(t, x, y));
			}
			p.add(row);

		}
		
		FlowPanel row = new FlowPanel('x');
		
		row.add(new JLabel("Divide by:"));
		jf = new JTextField(3);
		jf.setText(Integer.toString(divisor));

		jf.setMinimumSize(new Dimension(30, 15));
		
		jf.getDocument().addDocumentListener(new DocumentListener() {
			
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
				String text = jf.getText();

				int i;
				try {
					i = Integer.parseInt(text);
				} catch (NumberFormatException e) {
					i = 0;
				}

				if (i != divisor) {
					divisor = i;
				}
				FilterOperation.this.source.performOperation();

				p.getParent().repaint();

			}
		});
		
		row.add(jf);
		p.add(row);
		
		
	}

	public void perform(ImageObject source, ArrayList<ImageObject> dest) {

		BufferedImage t = source.getThumb();
		int edge = Math.max(t.getWidth(), t.getHeight());

		dest.add(new ImageObject(source.getMap().applyFilter(filter, divisor), edge));
	}

	public JComponent getPanel() {
		return p;
	}

	class FilterCellListener implements DocumentListener {

		private int x;
		private int y;
		private JTextField t;

		FilterCellListener(JTextField t, int x, int y) {
			this.t = t;
			this.x = x;
			this.y = y;

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

			double i;
			try {
				i = Double.parseDouble(text);
			} catch (NumberFormatException e) {
				i = 0;
			}

			if (i != filter[y][x]) {
				filter[y][x] = i;
			}
			source.performOperation();

			p.getParent().repaint();

		}

	}

}
