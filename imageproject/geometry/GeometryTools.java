package imageproject.geometry;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;

public class GeometryTools {

	static public Line drawArrow(Graphics g, Rectangle pb, Rectangle b) {
		return drawArrow(g, pb, b, true);
	}
	
	
	static public Line getArrow(Rectangle sourceRect, Rectangle destRect) {
		Line sourceCenterToDestCenter = getCenterDistanceLine(sourceRect, destRect);

		LineSet sourceBorder = new LineSet(sourceRect);

		LineSet destBorder = new LineSet(destRect);

		Point sourceBorderIntersect = sourceBorder.intersect(sourceCenterToDestCenter);

		Point destBorderIntersect = destBorder.intersect(sourceCenterToDestCenter);
		
		if (sourceBorderIntersect == null || destBorderIntersect == null) {
			return null;
		}

		return new Line(sourceBorderIntersect, destBorderIntersect);
		
	}


	public static Line getCenterDistanceLine(Rectangle sourceRect, Rectangle destRect) {
		Point sourceCenter = new Point((int) Math.round(sourceRect.getCenterX()), (int) Math.round(sourceRect.getCenterY()));

		Point destCenter = new Point((int) Math.round(destRect.getCenterX()), (int) Math.round(destRect.getCenterY()));

		Line sourceCenterToDestCenter = new Line(sourceCenter, destCenter);
		return sourceCenterToDestCenter;
	}
	

	static public Line drawArrow(Graphics g, Rectangle source, Rectangle dest, boolean solidTriangle) {

		Line l = getArrow(source, dest);
		
		if (l == null) {
			return null;
		}

		Point pi = l.p1;
		Point ci = l.p2;
		
		if (pi != null && ci != null) {

			g.drawLine(pi.x, pi.y, ci.x, ci.y);


			Circle c = new Circle(ci, 20);

			Point arrowTail = l.intersect(c);

			if (arrowTail != null) {

				int xd = arrowTail.x - ci.x;
				int yd = arrowTail.y - ci.y;

				if (!solidTriangle) {

					Point p1 = new Point(arrowTail.x + yd * 2 / 3, arrowTail.y - xd * 2 / 3);

					Point p2 = new Point(arrowTail.x - yd * 2 / 3, arrowTail.y + xd * 2 / 3);

					g.drawLine(p1.x, p1.y, ci.x, ci.y);
					g.drawLine(p2.x, p2.y, ci.x, ci.y);

				} else {

					Point p1 = new Point(arrowTail.x + yd / 3, arrowTail.y - xd / 3);

					Point p2 = new Point(arrowTail.x - yd / 3, arrowTail.y + xd / 3);

					Polygon p = new Polygon();

					p.addPoint(p1.x, p1.y);
					p.addPoint(p2.x, p2.y);

					p.addPoint(ci.x, ci.y);

					g.fillPolygon(p);

				}
			}

		}
		return l;

	}

}
