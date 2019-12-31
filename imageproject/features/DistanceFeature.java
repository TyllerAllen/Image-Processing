package imageproject.features;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import imageproject.gui.FlowPanel;
import imageproject.images.DistanceMap;
import imageproject.images.ImageObject;

public class DistanceFeature implements Feature {
	private DistanceMap distanceMap;

	JPanel p = new JPanel();

	private DistanceFeature() {
	}


	public static FeatureFactory<DistanceFeature> factory = new FeatureFactory<DistanceFeature>() {

		@Override
		public DistanceFeature createFeature(ImageObject o) {

			return new DistanceFeature();

		}

		@Override
		public String getName() {
			return "DistanceFeature";
		}

		@Override
		public boolean canSave() {
			return false;
		}

		@Override
		public String toString() {
			return getName();
		}

		@Override
		public double[] getVector(String text) {
			return new double[] {};
		}

	};

	@Override
	public JPanel getPanel() {
		return p;
	}

	@Override
	public String serialize() {
		return "";
	}

	public void init(DistanceMap distanceMap, Feature f) {

		p = new FlowPanel('x');

		double[] vector = f.getVector();

		HashMap<String, ArrayList<Double>> distances = distanceMap.getDistances(vector);

		FlowPanel p2 = new FlowPanel('y');

		p2.add(new JLabel("symbol"));
		p2.add(new JLabel("distance"));

		p.add(p2);
		
		boolean first= true;
		
		double min = 0;
		String symbol = "";
		
		for (String k : distances.keySet()) {
			for (Double ds : distances.get(k) ) {

				FlowPanel dist = new FlowPanel('y');

				JTextField stf = new JTextField();
				stf.setText(k);
				stf.setMinimumSize(new Dimension(50,15));
				stf.setEditable(false);
				dist.add(stf);
				JTextField dtf = new JTextField();
				dtf.setText(String.format("%.2f", ds));
				
				if (first) {
					min = ds;
					first =false;
					symbol = k;
				}else {
					if (ds < min) {
						min = ds;
						symbol = k;
					}
				}
				
				dtf.setMinimumSize(new Dimension(30,15));
				dtf.setEditable(false);
				dist.add(dtf);

				p.add(dist);
				
				
				
				
			}
		}
		
		FlowPanel p3 = new FlowPanel('y');

		p3.add(new JLabel("recognized symbol"));
		p3.add(new JLabel("distance"));

		p.add(p3);
		
		
		FlowPanel result = new FlowPanel('y');

		JTextField stf = new JTextField();
		stf.setText(symbol);
		stf.setMinimumSize(new Dimension(50,15));
		stf.setEditable(false);
		result.add(stf);
		JTextField dtf = new JTextField();
		dtf.setText(String.format("%.2f", min));
		

		
		dtf.setMinimumSize(new Dimension(30,15));
		dtf.setEditable(false);
		result.add(dtf);

		p.add(result);
		
		

		this.distanceMap = distanceMap;

	}

	@Override
	public double[] getVector() {
		return new double[] {};
	}
}
