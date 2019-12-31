package imageproject.features;

import javax.swing.JPanel;

public interface Feature {


	JPanel getPanel();
	
	String serialize();

	double[] getVector();
	
	

}
