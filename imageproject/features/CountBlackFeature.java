package imageproject.features;

import javax.swing.JLabel;
import javax.swing.JTextField;

import imageproject.gui.FlowPanel;
import imageproject.images.ImageObject;

public class CountBlackFeature implements Feature {

	private int black;

	FlowPanel p;

	@Override
	public FlowPanel getPanel() {
		return p;
	}

	private CountBlackFeature(int black) {
		this.black = black;

		p = new FlowPanel('x');
		p.add(new JLabel("Black pixels: "));
		JTextField tf = new JTextField(10);
		tf.setEditable(false);
		tf.setText(Integer.toString(black));
		tf.setMinimumSize(tf.getPreferredSize());
		p.add(tf);

	}

	public static FeatureFactory <CountBlackFeature> factory = new FeatureFactory<CountBlackFeature>() {

		@Override
		public CountBlackFeature createFeature(ImageObject o) {

			int black = o.getMap().countBlack();

			return new CountBlackFeature(black);

		}

		@Override
		public String getName() {
			return "Count Black Feature";
		}

		@Override
		public boolean canSave() {
			return true;
		}

		@Override
		public double[] getVector(String text) {

			Integer i;

			try {
				i = Integer.parseInt(text);
			} catch (NumberFormatException e) {
				i = 0;
			}

			return new double[] { i };
		}

	};

	@Override
	public String serialize() {
		return Integer.toString(black);
	}

	@Override
	public double[] getVector() {
		return new double[] {black};
	}

}
