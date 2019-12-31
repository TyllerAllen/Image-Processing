package imageproject;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import imageproject.gui.ImageDisplay;

/**
 * image: click - drag / select, double click - view, right click - add
 * operation or change image
 * 
 * background: click - nothing, double click- load image, right click - nothing
 * 
 * operation : click - select, double click - edit operation right click -
 * change operation
 * 
 * 
 * @author Scissors
 *
 */

public class ImageProjectApp {

	public static void main(String[] args) {

		JFrame.setDefaultLookAndFeelDecorated(true);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		} catch (UnsupportedLookAndFeelException e) {
		}

		JFrame f = new JFrame("Image Project");

		f.setSize(1580, 900);

		f.add(new ImageDisplay(f));

		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);

	}

}
