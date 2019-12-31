package imageproject.geometry;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

public class LineSet {

	ArrayList<Line> lines = new ArrayList<Line>();

	public LineSet(Rectangle rect) {

		
		
		// top
		lines.add( new  Line(rect.x, rect.y, rect.width+rect.x, rect.y));

		// left
		lines.add( new  Line(rect.x, rect.y, rect.x, rect.y + rect.height));

		
		// bottom
		lines.add( new  Line(rect.x, rect.y+ rect.height, rect.width+rect.x, rect.y+rect.height));

		// right
		lines.add( new  Line(rect.x+rect.width, rect.y, rect.x+rect.width, rect.y + rect.height));
		
		
		
		
		
	}

	public Point intersect(Line l) {
		
		for (Line line : lines ) {
			
			
			Point i = line.intersect(l);
			if (i != null) {
				return i;
			}
			
		}
		return null;
		
		
		
		
	}

}
