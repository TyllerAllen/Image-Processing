package imageproject.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.util.ArrayList;

import imageproject.geometry.GeometryTools;
import imageproject.geometry.Line;
import imageproject.images.ImageNode;

public class ImageDisplayLayout implements LayoutManager {

	private ArrayList<ImageNode> objects;

	public ImageDisplayLayout(ArrayList<ImageNode> objects) {
		this.objects = objects;
	}

	public void addLayoutComponent(String name, Component comp) {
	}

	public void removeLayoutComponent(Component comp) {
	}

	public Dimension preferredLayoutSize(Container parent) {
		return null;
	}

	public Dimension minimumLayoutSize(Container parent) {
		return null;
	}

	public void layoutContainer(Container parent) {

		for (ImageNode io : objects) {

			ImageNode p = io.getParent();

			if (p != null) {

				Line line = GeometryTools.getCenterDistanceLine(io.getBounds(), p.getBounds());

				io.moveOperationCenter(line.getCenter());
			}

		}

	}

}
