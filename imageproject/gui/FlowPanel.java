package imageproject.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JPanel;

public class FlowPanel extends JPanel {

	int pad = 1;

	int x = 0;
	int y = 0;
	int width = 0;
	int height = 0;

	int X = 'X';
	int Y = 'Y';

	int align;

	public final static String FIT = "fit";
	public final static String CENTER = "center";

	ArrayList<Component> fit = new ArrayList<Component>();

	ArrayList<Component> center = new ArrayList<Component>();

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public FlowPanel(char c) {

		setLayout(null);

		if (c == 'y' || c == 'Y') {

			align = Y;
			// setBorder(new LineBorder(Color.orange));

		} else {
			// setBorder(new LineBorder(Color.red));

			align = X;
		}

	}

	private Component addToPanel(Component comp) {


//		Dimension minimumLayoutSize = bl.minimumLayoutSize(this);
//		setMinimumSize(minimumLayoutSize);
//		setSize(minimumLayoutSize);

		Dimension size = comp.getMinimumSize();

		comp.setSize(size);

		comp.setLocation(x, y);

		if (align == X) {

			x += size.width + pad;
			width = x - pad;
			if (size.height > height) {
				height = size.height;
			}

			fitHeights(height);
			centerHeights(height);

			size = new Dimension(width + pad * 2, height);

		} else {

			y += size.height + pad;
			height = y - pad;
			if (size.width > width) {
				width = size.width;
			}

			fitWidths(width);
			centerWidths(width);

			size = new Dimension(width, height + pad * 2);

		}

		setSize(size);
		setMinimumSize(size);
		setPreferredSize(size);

		return super.add(comp);

	}

	private void fitWidths(int width) {
		for (Component c : fit) {
			Dimension s = c.getSize();
			c.setSize(width, s.height);
		}
	}

	private void fitHeights(int height) {
		for (Component c : fit) {
			Dimension s = c.getSize();
			c.setSize(s.width, height);
		}
	}

	private void centerWidths(int width) {
		for (Component c : center) {
			Dimension s = c.getSize();
			c.setLocation((width - s.width) / 2, c.getY());

		}
	}

	private void centerHeights(int height) {
		for (Component c : center) {
			Dimension s = c.getSize();
			c.setLocation(c.getX(), (height - s.height / 2));

		}
	}

	private void addToPanel(Component comp, String hint) {
		if (hint.toLowerCase().contentEquals(FIT)) {
			this.fit.add(comp);
		} else if (hint == CENTER) {
			this.center.add(comp);

		}
		addToPanel(comp);
	}

	@Override
	public Component add(Component comp) {
		return addToPanel(comp);
	}

	@Override
	public Component add(Component comp, int index) {
		throw new UnsupportedOperationException(
				"not implemented, no index allowed when adding components to a flowpanel");
	}

	@Override
	public void add(Component comp, Object constraints) {

		addToPanel(comp, constraints.toString());
	}

	@Override
	public void add(Component comp, Object constraints, int index) {
		throw new UnsupportedOperationException(
				"not implemented, no index allowed when adding components to a flowpanel");
	}


	

}
