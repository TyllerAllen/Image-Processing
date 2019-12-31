package imageproject.operations;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import imageproject.gui.FlowPanel;
import imageproject.images.ImageNode;
import imageproject.images.ImageObject;

public class ThinningOperation implements Operation {

	int w;
	int h;
	int iterations;
	private int[][] rules = { { 0, 1, 2, 0, 2, 0, 0, 1 }, { 0, 0, 1, 0, 1, 0, 1, 2 }, { 0, 0, 0, 1, 0, 2, 2, 1 },
			{ 0, 0, 0, 0, 1, 1, 2, 2 }, { 0, 1, 2, 0, 2, 1, 2, 2 }, { 0, 0, 1, 1, 2, 2, 2, 2 },
			{ 0, 0, 0, 0, 2, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 2, 0 }, { 1, 2, 2, 0, 2, 0, 1, 2 },
			{ 2, 2, 2, 1, 2, 0, 0, 1 }, { 1, 0, 0, 2, 1, 2, 2, 2 }, { 2, 1, 0, 2, 0, 2, 2, 1 },
			{ 2, 2, 2, 2, 1, 1, 0, 0 }, { 2, 2, 1, 2, 0, 2, 1, 0 }, { 0, 0, 0, 2, 0, 0, 0, 0 },
			{ 0, 2, 0, 0, 0, 0, 0, 0 }, { 2, 1, 0, 2, 0, 1, 0, 0 }, { 1, 0, 0, 2, 0, 2, 1, 0 },
			{ 2, 2, 1, 1, 0, 0, 0, 0 }, { 1, 2, 2, 0, 1, 0, 0, 0 } };
	private int[][] around = { { -1, -1 }, { 0, -1 }, { 1, -1 }, { -1, 0 }, { 1, 0 }, { -1, 1 }, { 0, 1 }, { 1, 1 } };

	FlowPanel p = new FlowPanel('y');
	JSlider slider;
	JTextField tf = new JTextField(3);
	private ImageNode source;
	BufferedImage thinned;
	BufferedImage original;
	boolean dontUpdateText = false;
	
	public ThinningOperation(int iterations, ImageNode source) {
		this.source = source;
		this.iterations= iterations;
		this.original= source.getParent().getObjects().get(0).getThumb();
		slider = new JSlider(1, 200, iterations);
		p.add(new JLabel("Number of Thinning iterations"));
		tf.setText(String.format("%d", iterations));
		tf.setMinimumSize(new Dimension(30,15));
		
		p.add(tf, FlowPanel.CENTER);

		tf.getDocument().addDocumentListener(new DocumentListener() {

			public void removeUpdate(DocumentEvent e) {
				extracted();

			}

			private void extracted() {
				String text = tf.getText();
				try {
					int parseInt = Integer.parseInt(text);
					if (parseInt != ThinningOperation.this.iterations) {

						dontUpdateText = true;
						slider.setValue(parseInt);
						dontUpdateText = false;

					}

				} catch (NumberFormatException e1) {
				}
			}

			public void insertUpdate(DocumentEvent e) {
				extracted();
			}

			public void changedUpdate(DocumentEvent e) {
				extracted();
			}
		});
		
		
		
		
		
		

		p.add(slider,FlowPanel.FIT);
		slider.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {

				int nv = slider.getValue();

				if (nv != ThinningOperation.this.iterations) {

					ThinningOperation.this.iterations = slider.getValue();
					tf.setText(String.format("%d", ThinningOperation.this.iterations));
					thinning(slider.getValue());
					//ThinningOperation.this.perform(ThinningOperation.this.source.,source.getObjects());
					//ThinningOperation.this.source.performOperation();
					p.getParent().repaint();
					source.getObjects().clear();
					source.getObjects().add(new ImageObject(thinned));
				}

			}
		});

		
	}

	public void perform(ImageObject source, ArrayList<ImageObject> dest) {
		this.original=source.getThumb();
		thinning(this.iterations);
		//dest.clear();
		dest.add(new ImageObject(thinned));
	}

	public JComponent getPanel() {
		return p;
	}

	private boolean checkRules(int[] surrounding) {
		for (int j = 0; j < 8; j++) {
			if (surrounding[j] == -1) {
				surrounding[j] = 0;
			} else {
				surrounding[j] = 2;
			}
		}
		boolean match = false;
		for (int i = 0; i < 20; i++) {
			match = true;
			for (int j = 0; j < 8; j++) {
				if (rules[i][j] != 1) {
					if (rules[i][j] != surrounding[j]) {
						match = false;
					}
				}
			}
			if (match == true) {
				break;
			}
		}
		return match;
	}

	private void thinning(int in) {
		this.iterations=in;
		thinned = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		BufferedImage thinnedOriginal = deepCopy(original);
		Color blackColor = new Color(0, 0, 0); // Color white
		int black = blackColor.getRGB();
		Color whiteColor = new Color(255, 255, 255); // Color white
		int white = whiteColor.getRGB();
		int[] surrounding = new int[8];
		int pixel = 0;
		boolean change = true;
		for (int z = 0; z < iterations; z++) {
			change=false;
			for (int y = 0; y < original.getHeight(); y++) {
				for (int x = 0; x < original.getWidth(); x++) {
					pixel = thinnedOriginal.getRGB(x, y);

					if (pixel != -1) {
						// build array of surrounding pixels
						surrounding = new int[8];
						for (int i = 0; i < 8; i++) {
							try {
								surrounding[i] = thinnedOriginal.getRGB(x + around[i][0], y + around[i][1]);
							} catch (Exception e) {

							}
						}

						if (checkRules(surrounding)) {
							thinned.setRGB(x, y, white);
							change=true;
						} else {
							thinned.setRGB(x, y, black);
						}
					}else {
					thinned.setRGB(x, y, pixel);
					}
				}
			}
			thinnedOriginal= deepCopy(thinned);
		}
		
		
		source = new ImageNode(thinned, thinned.getWidth(), thinned.getHeight());
	}
	
	static BufferedImage deepCopy(BufferedImage bi) {
		 ColorModel cm = bi.getColorModel();
		 boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		 WritableRaster raster = bi.copyData(null);
		 return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
		}

}
