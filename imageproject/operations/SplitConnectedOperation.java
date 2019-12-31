package imageproject.operations;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;

import imageproject.gui.FlowPanel;
import imageproject.images.ImageObject;
import imageproject.images.IntMap;

public class SplitConnectedOperation implements Operation {

	public SplitConnectedOperation() {

		JLabel comp = new JLabel("Split by connected region");

		p.add(comp, FlowPanel.CENTER);

	}

	FlowPanel p = new FlowPanel('y');

	public void perform(ImageObject source, ArrayList<ImageObject> dest) {

		BufferedImage t = source.getThumb();

		int tarea = t.getHeight() * t.getWidth();

		ArrayList<IntMap> maps = source.getMap().splitConnected();

		int maxEdge;

		if (maps.size() == 1) {
			maxEdge = Math.max(t.getWidth(), t.getHeight());

		} else {

			int size = maps.size();
			if (size == 0) {
				size = 1;
			}
			
			double ss = Math.sqrt(size);
			double sf = Math.floor(ss);
			double sc = Math.ceil(ss);
			
			if (sf*sc < size) {
				sf++;
			}
			
			if (t.getHeight() > t.getWidth()){
				double hd =  t.getHeight()/ sc;
				double ld = t.getWidth() / sf;
				maxEdge = (int)Math.min(hd,ld);
			}else {
				double hd =  t.getHeight()/ sf;
				double ld = t.getWidth() / sc;
				maxEdge = (int) Math.min(hd,ld)-1;
			}
			
			

//			double mtareas = tarea / (size);

//			maxEdge = (int) Math.floor(Math.sqrt(mtareas));

		}

		for (IntMap map : maps) {
			dest.add(new ImageObject(map, maxEdge));

		}

	}
//
//	public void draw(Graphics g, Point c) {
//		
//		
//
//		String text = "Split Connected";
//		
//		StringGraphics sg = new StringGraphics (g,c, text);
//		
//		Rectangle sb = sg.bounds;
//		
//		
//		
//		g.setColor(Color.lightGray);
//		
//		g.fillRect(sb.x, sb.y, sb.width, sb.height);
//		g.setColor(Color.black);
//		g.drawRect(sb.x, sb.y, sb.width, sb.height);
//		
//		
//		sg.drawString();
//		
//		
//		
//		
//	}

	public JPanel getPanel() {
		return p;
	}

}
