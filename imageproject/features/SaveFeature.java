package imageproject.features;

import javax.swing.JLabel;
import javax.swing.JTextField;

import imageproject.gui.FlowPanel;
import imageproject.images.ImageObject;

public class SaveFeature implements Feature {

	private String name;
	
	FlowPanel p ;
	private JTextField tf = new JTextField(10);

	
	
	@Override
	public FlowPanel getPanel() {
		return p;
	}
	

	private SaveFeature() {
		this.name = "";

		 p = new FlowPanel('x');
		p.add(new JLabel("Known Symbol: "));
		tf.setText(name);
		tf.setMinimumSize(tf.getPreferredSize());
		p.add(tf);

	}

	


	public static FeatureFactory <SaveFeature>factory = new FeatureFactory<SaveFeature>() {

		@Override
		public SaveFeature createFeature(ImageObject o) {


			return new SaveFeature();

		}

		@Override
		public String getName() {
			return "Save";
		}

		@Override
		public boolean canSave() {
			return false;
		}

		@Override
		public double[] getVector(String text) {
			return new double[] {};
		}

	};




	@Override
	public String serialize() {
		return tf.getText();
	}


	@Override
	public double[] getVector() {
		return new double[] {};
	}

}
