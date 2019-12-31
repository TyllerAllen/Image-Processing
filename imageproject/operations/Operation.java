package imageproject.operations;

import java.util.ArrayList;

import javax.swing.JComponent;

import imageproject.images.ImageObject;

public interface Operation   {

	void perform(ImageObject source, ArrayList<ImageObject> dest);

//	void draw(Graphics g, Point c);
	
	
	JComponent getPanel();
	
	


}