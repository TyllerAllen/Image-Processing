package imageproject.images;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeSet;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import imageproject.features.DistanceFeature;
import imageproject.features.Feature;
import imageproject.features.FeatureFactory;
import imageproject.features.SaveFeature;
import imageproject.gui.FlowPanel;
import imageproject.operations.Operation;

public class ImageNode {

	private Operation operation;
	private Rectangle bounds;

	private ImageNode parent;
	private ArrayList<ImageNode> children = new ArrayList<ImageNode>();

	private ArrayList<ImageObject> objects = new ArrayList<ImageObject>();

	final JFrame nodeFrame;
	private TreeSet<FeatureFactory<?>> featureFactories = new TreeSet<FeatureFactory<?>>();

	DistanceMap distanceMap = null;

	private ImageNode() {
		nodeFrame = new JFrame();
		operation = null;

	}

	public ImageNode(BufferedImage bi, int x, int y) {
		this();
		ImageObject io = new ImageObject(bi);
		objects.add(io);
		redoForm();

		BufferedImage t = io.getThumb();

		setBounds(new Rectangle(x, y, t.getWidth(), t.getHeight()));

	}

	public ImageNode(ImageNode source) {
		this();
		redoForm();
		bounds = new Rectangle(source.getBounds());
		parent = source;
	}

	public void addChild(ImageNode c) {
		children.add(c);
	}

	public void setOperation(Operation o) {
		operation = o;

		performOperation();

		if (objects.size() == 1) {
			BufferedImage t = objects.get(0).getThumb();
			bounds.setSize(t.getWidth(), t.getHeight());
		}

	}

	public void move(int x, int y) {
		bounds.translate(x, y);
	}

	protected void setBounds(Rectangle rectangle) {
		bounds = rectangle;
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public ImageNode getParent() {
		return parent;
	}

	public void performOperation() {

		if (operation != null && getParent() != null) {

			objects.clear();
			for (ImageObject o : getParent().objects) {
				operation.perform(o, objects);
			}


			redoForm();
		}

		for (ImageNode c : children) {
			c.performOperation();
		}

	}

	protected void setParent(ImageNode source) {
		parent = source;
	}

	public void draw(Graphics g, boolean isSelected) {

		g.setColor(Color.white);

		g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);

		if (isSelected) {
			g.setColor(Color.blue);

		} else {
			g.setColor(Color.black);

		}

		g.drawRect(bounds.x, bounds.y, bounds.width + 1, bounds.height + 1);

		if (size() == 1) {
			g.drawImage(objects.get(0).getThumb(), bounds.x + 1, bounds.y + 1, null);
		} else {

			int x = bounds.x + 1;
			int w = 0;
			int h = 0;
			int y = 0;

			Rectangle pb = parent.getBounds();

			for (ImageObject io : objects) {

				BufferedImage t = io.getThumb();

				// w += t.getWidth();
				x = bounds.x + 1 + w;

				if (w + t.getWidth() + 1 > pb.width) {
					w = 0;
					h += y;
					y = 0;
					x = bounds.x + 1;
				}

				g.setColor(new Color(170, 170, 170));

				g.drawRect(x, h + bounds.y + 1, t.getWidth() + 1, t.getHeight() + 1);

				g.drawImage(t, x + 1, h + bounds.y + 1 + 1, null);
				w += t.getWidth();
				if (t.getHeight() > y) {
					y = t.getHeight();
				}

			}

		}

	}

	public int size() {
		return objects.size();
	}

	public void moveOperationCenter(Point center) {
		JComponent p = operation.getPanel();
		int w = p.getWidth();
		int h = p.getHeight();

		p.setLocation(center.x - w / 2, center.y - h / 2);

	}

	public void addFeature(String string, FeatureFactory<?> f) {

		featureFactories.add(f);

		redoForm();

		showImageFrame();

	}

	private void redoForm() {

		Container cp = nodeFrame.getContentPane();

		cp.removeAll();
		FlowPanel view = new FlowPanel('y');

		cp.setLayout(new BorderLayout());

		int maxWidth = 0;

		for (ImageObject o : objects) {
			int w = o.getWidth();
			if (w > maxWidth) {
				maxWidth = w;
			}
		}

		if (featureFactories.contains(DistanceFeature.factory)) {

			FlowPanel distance = new FlowPanel('x');

			distance.add(new JLabel("Feature File to measure distance from:"));

			JTextField ftf = new JTextField();

			ftf.setEditable(false);
			ftf.setMinimumSize(new Dimension(450, 20));
			distance.add(ftf);

			JButton fileButton = new JButton("select file");

			fileButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					JFileChooser jfc = new JFileChooser(".");

					int result = jfc.showOpenDialog(null);

					if (result == JFileChooser.APPROVE_OPTION) {

						ftf.setText(jfc.getSelectedFile().getAbsolutePath());

						try {
							distanceMap = new DistanceMap(jfc.getSelectedFile(), featureFactories);
							if (!distanceMap.valid ) {
								distanceMap = null;
							}
							redoForm();
						} catch (IOException e1) {
							JOptionPane.showMessageDialog(null, "error reading file", "Error",
									JOptionPane.ERROR_MESSAGE);
						}

					}

				}
			});

			distance.add(fileButton);

			cp.add(distance, BorderLayout.NORTH);

		}

		for (ImageObject o : objects) {

			FlowPanel row = new FlowPanel('x');

			row.add(o);

			if (featureFactories.size() > 0) {

				row.add(Box.createHorizontalStrut(maxWidth - o.getWidth()));

				FlowPanel features = new FlowPanel('y');

				for (FeatureFactory<?> ff : featureFactories) {

					if (ff == DistanceFeature.factory) {

					} else {

						Feature newFeature = ff.createFeature(o);

						o.setFeature(ff, newFeature);
						features.add(newFeature.getPanel());
					}
				}

				if (featureFactories.contains(DistanceFeature.factory) && distanceMap != null) {

					DistanceFeature df = DistanceFeature.factory.createFeature(o);
					
					df.init(distanceMap, o.getFeature(distanceMap.getType()));

					o.setFeature(DistanceFeature.factory, df);
					features.add(df.getPanel());
				}

				row.add(features);
			}

			JSeparator sep = new JSeparator(JSeparator.HORIZONTAL);

			sep.setMinimumSize(new Dimension(1, 1));
			view.add(sep, FlowPanel.FIT);
			view.add(row);
		}

		JScrollPane scroll = new JScrollPane(view);
		cp.add(scroll, BorderLayout.CENTER); //

		if (featureFactories.contains(SaveFeature.factory)) {

			JComboBox<FeatureFactory<?>> ffcb = new JComboBox<FeatureFactory<?>>();

			ffcb.addItem(FeatureFactory.blankFactory);

			for (FeatureFactory<?> f : featureFactories) {
				if (f.canSave()) {
					ffcb.addItem(f);
				}
			}

			FlowPanel save = new FlowPanel('x');
			save.add(ffcb);
			JButton saveButton = new JButton("Save to new feature file");
			save.add(saveButton);

			saveButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					Object ff = ffcb.getSelectedItem();

					if (ff instanceof FeatureFactory<?> && canSave((FeatureFactory<?>) ff)) {
						JFileChooser jfc = new JFileChooser(".");
						int result = jfc.showSaveDialog(null);

						if (result == JFileChooser.APPROVE_OPTION) {

							File file = jfc.getSelectedFile();

							saveFeatures(file, (FeatureFactory<?>) ff);

						}
					}
				}

			});

			JButton appendButton = new JButton("Append to existing feature file");
			save.add(appendButton);
			appendButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					Object ff = ffcb.getSelectedItem();
					if (ff instanceof FeatureFactory<?> && canSave((FeatureFactory<?>) ff)) {
						JFileChooser jfc = new JFileChooser(".");
						int result = jfc.showDialog(null, "Append");

						if (result == JFileChooser.APPROVE_OPTION) {

							File file = jfc.getSelectedFile();

							appendFeatures(file, (FeatureFactory<?>) ff);
						}
					}

				}
			});
			cp.add(save, BorderLayout.SOUTH);
		}

		nodeFrame.pack();
		int height = nodeFrame.getHeight();

		if (height > 800) {
			height = 800;
		}

		nodeFrame.setSize(nodeFrame.getWidth(), height);

		JScrollBar vScroll = scroll.getVerticalScrollBar();
		if (vScroll.isVisible()) {

			nodeFrame.setSize(nodeFrame.getWidth() + vScroll.getWidth(), height);

		}

	}

	public void showImageFrame() {
		nodeFrame.setVisible(true);
	}

	public ArrayList<ImageObject> getObjects() {
		return objects;
	}

	private boolean canSave(FeatureFactory<?> ff) {

		if (ff != FeatureFactory.blankFactory) {

			for (ImageObject o : objects) {

				if (o.getFeature(SaveFeature.factory).serialize().equals("")) {

					JOptionPane.showMessageDialog(null, "known symbol property not set for each symbol", "Error",
							JOptionPane.ERROR_MESSAGE);

					return false;
				}

			}

		} else {
			JOptionPane.showMessageDialog(null, "no feature selected (lower left dropdown box)", "Error",
					JOptionPane.ERROR_MESSAGE);

			return false;
		}

		return true;
	}

	private void appendFeatures(File file, FeatureFactory<?> ff) {

		FileReader fr = null;
		try {

			fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String firstLine = br.readLine().strip();

			if (firstLine.equals(ff.getName())) {
				fr.close();
				FileWriter fw = null;
				try {

					fw = new FileWriter(file, true);

					saveFeatures(new BufferedWriter(fw), ff);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, "error writing to file", "Error", JOptionPane.ERROR_MESSAGE);
				} finally {
					if (fw != null) {
						try {
							fw.close();
						} catch (Exception e) {
						}
					}
				}

			} else {
				JOptionPane.showMessageDialog(null,
						"specified feature file is using a different feature type than the one you're trying to save",
						"Error", JOptionPane.ERROR_MESSAGE);
			}

		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "couldn't find file", "Error", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "error reading from file", "Error", JOptionPane.ERROR_MESSAGE);
		} finally {
			if (fr != null) {
				try {
					fr.close();
				} catch (IOException e) {
				}
			}
		}

	}

	private void saveFeatures(BufferedWriter bw, FeatureFactory <?> ff) throws IOException {

		for (ImageObject o : objects) {

			String name = o.getFeature(SaveFeature.factory).serialize();
			String value = o.getFeature(ff).serialize();

			bw.write(String.format("%s=%s\n", name, value));

		}

		bw.close();

	}

	private void saveFeatures(File file, FeatureFactory <?>ff) {

		try {
			FileWriter fw = new FileWriter(file);

			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(ff.getName());
			bw.write("\n");
			saveFeatures(bw, ff);

		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "error writing to file", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}
