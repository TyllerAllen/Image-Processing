package imageproject.images;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.TreeMap;

import javax.swing.JPanel;

import imageproject.features.Feature;
import imageproject.features.FeatureFactory;
import imageproject.gui.ImageDisplay;

public class ImageObject extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1;

	static int max = 200;

	GreyImage image;

	BufferedImage thumb;

	IntMap map;

	private TreeMap<FeatureFactory, Feature> features = new TreeMap<FeatureFactory, Feature>();

	public GreyImage getImage() {
		return image;
	}

	private void init(GreyImage image, BufferedImage thumb, IntMap map) {

		this.image = image;
		this.thumb = thumb;
		this.map = map;
		

		Dimension size = new Dimension(image.getWidth(), image.getHeight());
		setMinimumSize(size);
		setSize(size);

	}
	
	
	private void init(GreyImage image, IntMap map) {
		init(image,map, max);

	}

	private void init(GreyImage image, IntMap map, int max) {
		init(image, ImageDisplay.resize(image.getImage(), max), map);

	}

	private void init(BufferedImage bi) {
		init(new GreyImage(bi));
	}

	private void init(GreyImage image) {
		init(image, new IntMap(image));
	}

	public ImageObject(BufferedImage bi) {

		init(bi);

	}

	public ImageObject(IntMap map) {
		this(map, max);
	}

	public ImageObject(IntMap map, int max) {

		init(new GreyImage(map), map, max);

	}

	public IntMap getMap() {
		return map;
	}

	public void setImage(BufferedImage bi) {

		init(bi);


	}

	public BufferedImage getThumb() {
		return thumb;
	}


	@Override
	protected void paintComponent(Graphics g) {

		g.setColor(Color.white);
		
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.black);
		
		g.drawLine(0,0,getWidth(),getHeight());
		
		g.drawImage(image.image, 0, 0, null);

	}

	public void setFeature(FeatureFactory ff, Feature newFeature) {
		features.put(ff, newFeature);
	}
	
	

	public Feature getFeature(FeatureFactory ff) {
		return features.get(ff);
	}
	

}