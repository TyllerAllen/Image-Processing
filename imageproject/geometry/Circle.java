package imageproject.geometry;

import java.awt.Point;

public class Circle {

	Point center;
	int radius;

	public Circle(Point center, int radius) {
		this.center = center;
		this.radius = radius;

	}

	
	public Point intersect (Line l) {
		
		return l.intersect(this);
		
	}
	
}
