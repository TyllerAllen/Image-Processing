package imageproject.features;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import imageproject.gui.FlowPanel;
import imageproject.images.ImageObject;

public class RatioFeature implements Feature {

	private FlowPanel p;
	private ArrayList<Double> rv;

	private RatioFeature(ArrayList<Double> rv) {

		this.rv = new ArrayList<Double>();
		p = new FlowPanel('x');
		p.add(new JLabel("Black to white ratio vector: "));

		for (Double d : rv) {

			if (d >= 10) {
				d = 9.99;
			}
			this.rv.add(d);

			JTextField tf = new JTextField(10);
			tf.setEditable(false);

			tf.setText(String.format("%.2f", d));
			tf.setMinimumSize(new Dimension(35, 15));
			p.add(tf);
		}

	}

	public static FeatureFactory <RatioFeature>factory = new FeatureFactory<RatioFeature>() {

		@Override
		public RatioFeature createFeature(ImageObject o) {
			
			
			ArrayList<Double> rv = o.getMap().divideInto9AndGetRatioVector();
			

			return new RatioFeature(rv);

		}

		@Override
		public String getName() {
			return "Ratio Feature";
		}

		@Override
		public boolean canSave() {
			return true;
		}

		@Override
		public double[] getVector(String text) {

			text = text.replaceAll("[\\[\\]]","");
			
			
			String[] split = text.split(",");
			
			double v[] = new double[split.length];
			
			for (int i = 0; i < split.length ;i++) {
				
				Double d;
				try {
					d = Double.parseDouble(split[i]);
				} catch (NumberFormatException e) {
					d = 0.0;
				}
				
				
				
				v[i] = d;
				
			}
			
			
			return v;
		}

	};

	@Override
	public JPanel getPanel() {
		return p;
	}

	@Override
	public String serialize() {

		StringBuilder sb = new StringBuilder();

		sb.append("[");

		boolean first = true;

		for (Double d : rv) {
			if (first) {
				first = false;
			} else {
				sb.append(",");
			}
			sb.append(String.format("%.10f", d));
		}
		sb.append("]");
		return sb.toString();
	}

	@Override
	public double[] getVector() {
		double v[] = new double[rv.size()];
		for (int i =0; i < rv.size(); i++) {
			v[i] = rv.get(i);
		}
		
		return v;
	}
}
