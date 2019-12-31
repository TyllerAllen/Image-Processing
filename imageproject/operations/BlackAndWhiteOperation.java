package imageproject.operations;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

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

public class BlackAndWhiteOperation implements Operation {

	int threshold;

	FlowPanel p = new FlowPanel('y');

	JTextField tf = new JTextField(3);

	private ImageNode source;
	JSlider slider;

	boolean dontUpdateText = false;

	public BlackAndWhiteOperation(int threshold, ImageNode source) {
		this.threshold = threshold;
		this.source = source;
		slider = new JSlider(-1, 255, threshold);


		p.add(new JLabel("Make black and white"));
		
		
		tf.setText(String.format("%d", threshold));
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
					if (parseInt != BlackAndWhiteOperation.this.threshold) {

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

				if (nv != BlackAndWhiteOperation.this.threshold) {

					BlackAndWhiteOperation.this.threshold = slider.getValue();

					if (!dontUpdateText) {
						tf.setText(String.format("%d", BlackAndWhiteOperation.this.threshold));

					}

					BlackAndWhiteOperation.this.source.performOperation();
					p.getParent().repaint();
				}

			}
		});


	}


	public void perform(ImageObject source, ArrayList<ImageObject> dest) {


		BufferedImage t = source.getThumb();
		int edge = Math.max(t.getWidth(), t.getHeight());
		dest.add(new ImageObject(source.getMap().makeBlackAndWhite(threshold), edge));

	}

	public JPanel getPanel() {
		return p;
	}

}
