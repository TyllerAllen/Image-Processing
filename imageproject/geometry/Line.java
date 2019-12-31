package imageproject.geometry;
import java.awt.Point;

public class Line {

	Point p1;
	Point p2;

	private Double slope;
	private Double offset;
	private int left;
	private int right;
	private int bottom;
	private int top;

	
	
	
	public Line(Point p1, Point p2) {
		this.p1 = p1;
		this.p2 = p2;

		if (p1.x == p2.x) {
			this.slope = null;
			this.offset = null;
		} else {
			this.slope = 1.0 * (p1.y - p2.y) / (p1.x - p2.x);
			this.offset = p1.y - slope * p1.x;
		}

		left = Math.min(p1.x, p2.x);
		right = Math.max(p1.x, p2.x);

		bottom = Math.min(p1.y, p2.y);
		top = Math.max(p1.y, p2.y);

	}

	public Line(int x1, int y1, int x2, int y2) {

		this(new Point(x1, y1), new Point(x2, y2));

	}

	public Point intersect(Line l) {

		double x;
		double y;

		if (slope == null && l.slope == null) {
			// both vertical
			return null;

		} else if (slope == null) {

			x = p1.x;
			y = l.slope * x + l.offset;

		} else if (l.slope == null) {

			x = l.p1.x;
			y = slope * x + offset;

		} else {

			x = (l.offset - offset) / (slope - l.slope);
			y = slope * x + offset;

		}

		if (inBounds(x, y) && l.inBounds(x, y)) {

			return new Point((int) Math.round(x), (int) Math.round(y));
		}
		return null;
	}

	private boolean inBounds(double x, double y) {
		return x >= left && x <= right && y >= bottom && y <= top;
	}




	public Point intersect(Circle c) {
		if (slope == null) {
			int x= c.center.x;
			
			int y1= c.center.y-c.radius;
			int y2= c.center.y+c.radius;
			
			
			if (inBounds(x, y1)) {
				return new Point((int) Math.round(x), (int) Math.round(y1));
			}else if (inBounds(x, y2)) {
				return new Point((int) Math.round(x), (int) Math.round(y2));
			}
			
			return null;
		}
		
		
		double w1 = Math.sqrt ( c.radius*c.radius / (1 + slope*slope));
		double w2 = -w1;
		double h1 = slope*w1;
		double h2 = slope*w2;
		
		
		double x1 = c.center.x + w1;
		double x2 = c.center.x + w2;
		
		double y1 = c.center.y + h1;
		double y2 = c.center.y + h2;
		
		if (inBounds(x1, y1)) {
			return new Point((int) Math.round(x1), (int) Math.round(y1));
		}else if (inBounds(x2, y2)) {
			return new Point((int) Math.round(x2), (int) Math.round(y2));
		}
		
		
		
		
		return null;
	}

	public Point getCenter() {
		return new Point( (left+right) / 2, (top+ bottom) / 2);
	}

}
