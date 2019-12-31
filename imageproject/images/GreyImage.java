package imageproject.images;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class GreyImage {

	BufferedImage image;

	public GreyImage(BufferedImage original) {

		image = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		Graphics g = getImage().getGraphics();
		g.drawImage(original, 0, 0, null);

	}

	
	public GreyImage ( IntMap map) {
		
		image = map.createImage();
		
		
	}
	
	public int getWidth() {
		return getImage().getWidth();
	}

	public int getHeight() {
		return getImage().getHeight();
	}

	public void getPixels(int[] data) {
		getImage().getData().getPixels(0, 0, getWidth(), getHeight(), data);

	}

	public BufferedImage getImage() {
		return image;
	}
}
