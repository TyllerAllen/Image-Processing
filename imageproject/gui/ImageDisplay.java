package imageproject.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import imageproject.features.CountBlackFeature;
import imageproject.features.DistanceFeature;
import imageproject.features.PercentFeature;
import imageproject.features.RatioFeature;
import imageproject.features.SaveFeature;
import imageproject.geometry.GeometryTools;
import imageproject.geometry.Line;
import imageproject.images.ImageNode;
import imageproject.operations.BlackAndWhiteOperation;
import imageproject.operations.FilterOperation;
import imageproject.operations.Operation;
import imageproject.operations.ScaleSymbolsOperation;
import imageproject.operations.SplitConnectedOperation;
import imageproject.operations.ThinningOperation;
import imageproject.operations.ThickeningOperation;
import imageproject.operations.ScaleSymbolsOperation;

public class ImageDisplay extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 0;

	ArrayList<ImageNode> objects = new ArrayList<ImageNode>();

	ImageNode selectedObject = null;

	private final JFrame frame;

	class StatsPopupMenu extends JPopupMenu {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1;

		JMenuItem countBlack = new JMenuItem("2. Count black pixels");
		JMenuItem ratio = new JMenuItem("5. Get ratio of black pixels to white pixels in each 9th");
		JMenuItem percent = new JMenuItem("   Get percent of black pixels in each 9th");
		
		JMenuItem save = new JMenuItem("6. Save features to file");
		JMenuItem distance = new JMenuItem("7. Measure distance to recognized symbols");

		private ImageNode source;

		public StatsPopupMenu() {

			this.add(countBlack);
			this.add(ratio);
			this.add(percent);
			this.add(save);
			this.add(distance);

			countBlack.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					source.addFeature("count black", CountBlackFeature.factory);
				}
			});

			ratio.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					source.addFeature("ratio", RatioFeature.factory);
				}
			});

			percent.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					source.addFeature("percent", PercentFeature.factory);
				}
			});
			
			save.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					
					
					source.addFeature("save", SaveFeature.factory);
					
					
				}
			});

			distance.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					source.addFeature("distance", DistanceFeature.factory);

				}
			});

		}

		public void init(ImageNode source) {
			this.source = source;

		}

	}

	class OperationPopupMenu extends JPopupMenu {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1;
		JMenuItem custom3x3 = new JMenuItem("1. Apply 3x3 filter");
		JMenuItem blackAndWhite = new JMenuItem("2. Change to black and white");
		JMenuItem splitConnected = new JMenuItem("2. Split by connected regions");

		JMenuItem filter3x1 = new JMenuItem("3. Apply 3x1 (1/3) filter");
		JMenuItem filter1x3 = new JMenuItem("3. Apply 1x3 (1/3) filter");
		JMenuItem filter3x3 = new JMenuItem("3. Apply 3x3 (1/9) filter");
		JMenuItem scaleSymbols = new JMenuItem("4. Scale symbols");

		JMenuItem thinning = new JMenuItem("8. Apply Thinning");
		JMenuItem thickening = new JMenuItem("8. Apply Thickening");
		private ImageNode pressed2;

		private void setNewOperation(Operation o) {
			pressed2.setOperation(o);

			objects.add(pressed2);

			ImageDisplay.this.add(o.getPanel());
			ImageDisplay.this.validate();

			pressed2.getParent().addChild(pressed2);

			repaint();
		}

		public OperationPopupMenu() {
			this.add(custom3x3);
			this.add(blackAndWhite);
			this.add(splitConnected);

			this.add(filter3x1);
			this.add(filter1x3);
			this.add(filter3x3);

			this.add(scaleSymbols);
			this.add(thinning);
			this.add(thickening);

			custom3x3.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {

					setNewOperation(new FilterOperation(new double[][] { { 1, 1, 1 }, { 0, 0, 0 }, { -1, -1, -1 } }, 1,
							pressed2));

				}

			});

			blackAndWhite.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {

					setNewOperation(new BlackAndWhiteOperation(127, pressed2));

				}
			});

			filter3x1.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {

					setNewOperation(new FilterOperation(new double[][] { { 1, 1, 1 } }, 3, pressed2));

				}
			});

			filter1x3.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {

					setNewOperation(new FilterOperation(new double[][] { { 1 }, { 1 }, { 1 } }, 3, pressed2));

				}
			});

			filter3x3.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {

					setNewOperation(
							new FilterOperation(new double[][] { { 1, 1, 1 }, { 1, 1, 1 }, { 1, 1, 1 } }, 9, pressed2));

				}
			});

			splitConnected.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					setNewOperation(new SplitConnectedOperation());
				}
			});

			this.addPopupMenuListener(new PopupMenuListener() {

				public void popupMenuWillBecomeVisible(PopupMenuEvent e) {

				}

				public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {

					operationSource = null;
					pressed = null;
					ImageDisplay.this.repaint();
				}

				public void popupMenuCanceled(PopupMenuEvent e) {

				}
			});

			this.scaleSymbols.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					setNewOperation(new ScaleSymbolsOperation(new int[] {15,15}, pressed2));
				}
			});
			
				
		
			
			this.thinning.addActionListener( new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					setNewOperation(new ThinningOperation(50,pressed2));
				}
			});
			
			this.thickening.addActionListener( new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					setNewOperation(new ThickeningOperation(5, pressed2));
				}
			});

		}

		public void init(ImageNode pressed, ImageNode operationSource) {
			pressed2 = pressed;

		}

	}

	StatsPopupMenu statPopup = new StatsPopupMenu();
	OperationPopupMenu opPopup = new OperationPopupMenu();

	ImageNode operationSource = null;
	ImageNode pressed = null;
	boolean moved = false;

	class ImageDisplayMouseListener implements MouseListener, MouseMotionListener {

		Point pressedXY;
		JFileChooser fc = new JFileChooser(new File("."));

		public void mouseMoved(MouseEvent e) {

			if (pressed != null && !opPopup.isVisible()) {

				pressed.move(e.getX() - pressedXY.x, e.getY() - pressedXY.y);
				pressedXY = e.getPoint();

				moved = true;

				doLayout();

				repaint();
			}

		}

		public void mouseDragged(MouseEvent e) {
			mouseMoved(e);
		}

		public void mouseReleased(MouseEvent e) {

			if (operationSource != null) {

				if (!moved) {
					statPopup.init(operationSource);
					statPopup.show(ImageDisplay.this, e.getX(), e.getY());
				} else if (operationSource.getBounds().intersects(pressed.getBounds())) {

					if (operationSource.getBounds().intersects(e.getX(), e.getY(), 1, 1)) {
						statPopup.init(operationSource);
						statPopup.show(ImageDisplay.this, e.getX(), e.getY());

					}

				} else {
					// objects.add(pressed);

					opPopup.init(pressed, operationSource);
					opPopup.show(ImageDisplay.this, e.getX(), e.getY());

				}

			}

			if (opPopup.isVisible()) {

			} else {
				operationSource = null;
				pressed = null;
				ImageDisplay.this.repaint();

			}

		}

		public void mousePressed(MouseEvent e) {

			for (ImageNode io : objects) {

				if (io.getBounds().contains(e.getPoint())) {
					pressed = io;
					pressedXY = e.getPoint();
					break;

				}

			}

			if (pressed != null) {

				if (e.getButton() == MouseEvent.BUTTON3) {
					ImageNode io2 = new ImageNode(pressed);

					operationSource = pressed;
					pressed = io2;
					moved = false;

				}
			}

		}

		public void mouseExited(MouseEvent e) {

		}

		public void mouseEntered(MouseEvent e) {

		}

		public void mouseClicked(MouseEvent e) {

			ImageNode clicked = null;

			for (ImageNode io : objects) {

				if (io.getBounds().contains(e.getPoint())) {
					clicked = io;
					break;
				}

			}

			if (clicked == null) {

				if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {

					int result = fc.showOpenDialog(frame);
					if (result == JFileChooser.APPROVE_OPTION) {
						File file = fc.getSelectedFile();
						if (file != null) {

							BufferedImage bi = loadImage(file);

							if (bi != null && bi.getWidth() > 0 && bi.getHeight() > 0) {

								objects.add(new ImageNode(bi, e.getX(), e.getY()));
								repaint(10);

							}

						}

					}
				} else if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1) {
					selectedObject = null;
					repaint(10);
				}
			} else {

				ImageNode clickedObject = (ImageNode) clicked;

				if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1) {

					selectedObject = (ImageNode) clicked;
					repaint(10);
				} else if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {

					clickedObject.showImageFrame();

				}
			}

		}

	}


	public ImageDisplay(JFrame frame) {

		this.frame = frame;
		ImageDisplayMouseListener listener = new ImageDisplayMouseListener();

		addMouseListener(listener);
		addMouseMotionListener(listener);
		setLayout(new ImageDisplayLayout(objects));


	}


	private static BufferedImage loadImage(String file) {
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File(file));

		} catch (IOException e) {
			e.printStackTrace();
			// TODO: let user know that image failed to load
		}
		return img;
	}

	private static BufferedImage loadImage(File file) {
		BufferedImage img = null;
		try {
			img = ImageIO.read(file);

		} catch (IOException e) {
			img = null;
		}
		return img;
	}

	static public BufferedImage resize(BufferedImage img, int max) {

		int h = img.getHeight();
		int w = img.getWidth();

		double scale = 1;
		if (h > w) {
			scale = 1.0 * max / h;

		} else {
			scale = 1.0 * max / w;

		}
		h = (int) Math.round(h * scale);
		w = (int) Math.round(w * scale);

		if (h < 1) {
			h = 1;
		}
		if (w < 1) {
			w = 1;
		}

		Image i = img.getScaledInstance(w, h, Image.SCALE_AREA_AVERAGING);

		BufferedImage bi = new BufferedImage(w, h, img.getType());
		Graphics2D g = bi.createGraphics();

		g.drawImage(i, 0, 0, null);

		return bi;

	}

	@Override
	protected void paintComponent(Graphics g) {

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		if (objects.isEmpty()) {

			String clickToLoad = "Double click to load Image";

			g.setColor(Color.black);

			Font newFont = getFont().deriveFont(70f);
			g.setFont(newFont);

			Rectangle2D stringBounds = g.getFontMetrics().getStringBounds(clickToLoad, g);

			// g.drawRect((int)(getWidth()-stringBounds.getWidth())/2,
			// (int)(getHeight()-stringBounds.getHeight())/2, (int)stringBounds.getWidth(),
			// (int)stringBounds.getHeight());
			// g.fillOval((int)(getWidth()-stringBounds.getWidth())/2,
			// (int)(getHeight()-stringBounds.getHeight())/2,10, 10);
			g.drawString("Double click to load Image", (int) (getWidth() - stringBounds.getWidth()) / 2,
					(int) (getHeight() - stringBounds.getHeight()) / 2 + (int) stringBounds.getCenterY()
							+ (int) stringBounds.getHeight());

		} else {

			for (ImageNode io : objects) {

				io.draw(g, io == selectedObject);


				ImageNode p = io.getParent();

				if (p != null) {

					Rectangle pb = p.getBounds();

					GeometryTools.drawArrow(g, pb, io.getBounds());

				}

			}

			if (operationSource != null && !operationSource.getBounds().intersects(pressed.getBounds())) {

				Rectangle b = pressed.getBounds();

				g.setColor(Color.lightGray);

				g.fillRect(b.x, b.y, b.width, b.height);

				g.setColor(Color.green);

				g.drawRect(b.x, b.y, b.width + 1, b.height + 1);

				Rectangle ob = operationSource.getBounds();

				GeometryTools.drawArrow(g, ob, b);

			}

			/*
			 * IntMap im = new IntMap(img);
			 * 
			 * g.drawImage(img, 0, 0, null);
			 * 
			 * int w = img.getWidth(); int h = img.getHeight();
			 * 
			 * double gauss[][] = { { 1, 2, 1 }, { 2, 4, 2 }, { 1, 2, 1 } }; double edgh[][]
			 * = { { 1, 2, 1 }, { 0, 0, 0 }, { -1, -2, -1 } }; double edgv[][] = { { 1, 0,
			 * -1 }, { 2, 0, -2 }, { 1, 0, -1 } };
			 * 
			 * double edgv5[][] = { { 2, 1, 0, -1, -2 }, { 2, 1, 0, -1, -2 }, { 4, 2, 0, -2,
			 * -4 }, { 2, 1, 0, -1, -2 }, { 2, 1, 0, -1, -2 } };
			 * 
			 * double lg[][] = { { -1, -1, -1 }, { -1, 8, -1 }, { -1, -1, -1 } };
			 * 
			 * double unk[][] = { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 } };
			 * 
			 * // IntMap applied= im.applyFilter(unk);
			 * 
			 * g.drawImage(im.applyFilter(lg, 1).createImage(), w, 0, null);
			 * g.drawImage(im.applyFilter(edgh).createImage(), w * 2, 0, null);
			 * g.drawImage(im.applyFilter(edgv).createImage(), w * 3, 0, null);
			 * 
			 * g.drawImage(im.applyFilter(edgh).applyFilter(edgh).createImage(), w * 2, h,
			 * null); g.drawImage(im.applyFilter(edgv).applyFilter(edgv).createImage(), w *
			 * 3, h, null);
			 * 
			 * g.drawImage(im.applyFilter(gauss, 16).createImage(), 0, h, null);
			 * 
			 * g.drawImage(im.applyFilter(edgv5).createImage(), w, h, null);
			 */

		}
	}

}
