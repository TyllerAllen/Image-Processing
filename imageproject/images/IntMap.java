package imageproject.images;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

public class IntMap {
	int data[];
	int w;
	int h;

	public IntMap(GreyImage image) {

		w = image.getWidth();
		h = image.getHeight();
		data = new int[w * h];

		image.getPixels(data);

	}

	public IntMap makeBlackAndWhite(int thresh) {
		IntMap res = new IntMap(w, h);
		for (int fx = 0; fx < w; fx++) {
			for (int fy = 0; fy < h; fy++) {
				int t = get(fx, fy);
				if (t > thresh) {
					t = 255;

				} else {
					t = 0;
				}
				res.set(fx, fy, t);
			}
		}
		return res;

	}

	public IntMap applyFilter(double[][] filter, double div) {

		int fw = filter[0].length;
		int fh = filter.length;

		int newW = w + fw - 1;
		int newH = h + fh - 1;

		IntMap res = new IntMap(newW, newH);

		for (int x = 0; x < newW; x++) {
			for (int y = 0; y < newH; y++) {

				double t = 0;
				for (int fx = 0; fx < fw; fx++) {
					for (int fy = 0; fy < fh; fy++) {
						t += get(x + fx - fw + 1, y + fy - fh + 1) * filter[fy][fx] / div;

					}
				}
				res.set(x, y, (int) t);

			}
		}
		return res;
	}

	private IntMap(int w, int h) {
		this.w = w;
		this.h = h;
		data = new int[w * h];
	}
	
	private IntMap(int w, int h, boolean full) {
		this(w,h);
		if(full) {
			for (int i = 0; i < w * h; i++) {
				data[i] = 255;
			}
		}
	}

	public IntMap applyFilter(double[][] filter) {

		return applyFilter(filter, 1);

	}

	public IntMap(BufferedImage image) {
		this(new GreyImage(image));
	}

	int get(int x, int y) {

		if (x < 0) {
			// x = 0;
			return 255;
		} else if (x >= w) {
//			x = w - 1;
			return 255;
		}

		if (y < 0) {
//			y = 0;
			return 255;

		} else if (y >= h) {
//			y = h - 1;
			return 255;
		}
		return data[x + y * w];

	}

	void set(int x, int y, int c) {

		if (x < 0 || x >= w || y < 0 || y >= h) {
			return;
		}
		if (c > 255) {
			c = 255;
		} else if (c < 0) {
			c = 0;
		}

		data[x + y * w] = c;

	}

	public BufferedImage createImage() {
		BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY);

		WritableRaster wr = bi.getData().createCompatibleWritableRaster(w, h);
		BufferedImage ni = new BufferedImage(bi.getColorModel(), wr, bi.getColorModel().isAlphaPremultiplied(), null);

		wr.setPixels(0, 0, w, h, data);
		return ni;
	}
	
	public IntMap scaleSymbols(int dimX, int dimY) {

		int used[][] = new int[w][h];
		int count = 0;

		HashMap<Integer, Integer> corrections = new HashMap<Integer, Integer>();

		HashMap<Integer, Region> regions = new HashMap<Integer, Region>();
		
		if (dimX <= 0)
			dimX = 1;
		if (dimY <= 0)
			dimY = 1;

		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {

				if (used[x][y] == 0) {

					int c = get(x, y);
					if (c < 255) {

						if (x > 0 && y > 0) {
							setUsed(used, corrections, x, y, used[x - 1][y - 1]);

						}

						if (x > 0) {
							setUsed(used, corrections, x, y, used[x - 1][y]);

						}
						if (y > 0) {

							setUsed(used, corrections, x, y, used[x][y - 1]);
							if (x < w - 1) {
								setUsed(used, corrections, x, y, used[x + 1][y - 1]);
							}

						}

						if (used[x][y] == 0) {
							count++;
							used[x][y] = count;

						}

						getRegion(regions, used[x][y]).update(x, y);

					} else {
						used[x][y] = -1;

					}

				}
			}
		}

		ArrayList<Integer> keys = new ArrayList<Integer>();

		keys.addAll(corrections.keySet());
		for (int i : keys) {

			limitDepth(corrections, i);

		}

		// do corrections on touching regions
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {

				int i = used[x][y];
				Integer ip = corrections.get(i);

				if (ip != null) {
					regions.remove(i);
					used[x][y] = ip;
					getRegion(regions, ip).update(x, y);

				}

			}
		}
		
		IntMap res = null;
		
		if (regions.size() > 1) {
			res = new IntMap(this.w, this.h, true);
	
			for (Integer ri : regions.keySet()) {
	
				Region r = regions.get(ri);
	
				int w = r.getWidth();
				int h = r.getHeight();
	
				int rx = r.getX();
				int ry = r.getY();
	
				IntMap im = new IntMap(w, h);
	
				for (int x = rx; x < w + rx; x++) {
					for (int y = ry; y < h + ry; y++) {
						if (used[x][y] == ri) {
							im.set(x - rx, y - ry, get(x, y));
						} else {
							im.set(x - rx, y - ry, 255);
						}
	
					}
				}
				
				float scaleFactorW = (float)dimX / w;
				float scaleFactorH = (float)dimY / h;
				
				for (int fx = 0; fx < dimX; fx++) {
					for (int fy = 0; fy < dimY; fy++) {
						int sx, sy;
						sx = (int)Math.min(w - 1, fx / scaleFactorW);
						sy = (int)Math.min(h - 1, fy / scaleFactorH);
						int t = im.get(sx,sy);
						res.set(fx + rx,  fy + ry,  t);
					}
				}
	
				if (r.getWidth() == 0 || r.getHeight() == 0) {
					System.out.println("IntMap.splitConnected( ??0)");
				}
	
			}
		} else if (regions.size() == 1) {
			res = new IntMap(dimX, dimY);
			
			float scaleFactorW = (float)dimX / w;
			float scaleFactorH = (float)dimY / h;
			
			for (int fx = 0; fx < dimX; fx++) {
				for (int fy = 0; fy < dimY; fy++) {
					int sx, sy;
					sx = (int)Math.min(w - 1, fx / scaleFactorW);
					sy = (int)Math.min(h - 1, fy / scaleFactorH);
					int t = get(sx,sy);
					res.set(fx,  fy,  t);
				}
			}
		}
		
		return res;

	}

	public ArrayList<IntMap> splitConnected() {

		ArrayList<IntMap> maps = new ArrayList<IntMap>();

		int used[][] = new int[w][h];
		int count = 0;

		HashMap<Integer, Integer> corrections = new HashMap<Integer, Integer>();

		HashMap<Integer, Region> regions = new HashMap<Integer, Region>();

		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {

				if (used[x][y] == 0) {

					int c = get(x, y);
					if (c < 255) {

						if (x > 0 && y > 0) {
							setUsed(used, corrections, x, y, used[x - 1][y - 1]);

						}

						if (x > 0) {
							setUsed(used, corrections, x, y, used[x - 1][y]);

						}
						if (y > 0) {

							setUsed(used, corrections, x, y, used[x][y - 1]);
							if (x < w - 1) {
								setUsed(used, corrections, x, y, used[x + 1][y - 1]);
							}

						}

						if (used[x][y] == 0) {
							count++;
							used[x][y] = count;

						}

						getRegion(regions, used[x][y]).update(x, y);

					} else {
						used[x][y] = -1;

					}

				}
			}
		}

		ArrayList<Integer> keys = new ArrayList<Integer>();

		keys.addAll(corrections.keySet());
		for (int i : keys) {

			limitDepth(corrections, i);

		}

		// do corrections on touching regions
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {

				int i = used[x][y];
				Integer ip = corrections.get(i);

				if (ip != null) {
					regions.remove(i);
					used[x][y] = ip;
					getRegion(regions, ip).update(x, y);

				}

			}
		}
		
		for (Integer ri : regions.keySet()) {

			Region r = regions.get(ri);

			int w = r.getWidth();
			int h = r.getHeight();

			int rx = r.getX();
			int ry = r.getY();

			IntMap im = new IntMap(w, h);

			for (int x = rx; x < w + rx; x++) {
				for (int y = ry; y < h + ry; y++) {
					if (used[x][y] == ri) {
						im.set(x - rx, y - ry, get(x, y));
					} else {
						im.set(x - rx, y - ry, 255);
					}

				}

			}


			maps.add(im);

		}
		return maps;

	}

	private void limitDepth(HashMap<Integer, Integer> corrections, int... m) {

		TreeSet<Integer> fixes = new TreeSet<Integer>();

		for (int t : m) {
			Integer i = t;

			while (i != null) {
				fixes.add(i);
				i = corrections.get(i);
			}

		}

		if (fixes.size() > 2) {

			int lowest = fixes.pollFirst();
			while (fixes.size() > 0) {
				int next = fixes.pollFirst();

				corrections.put(next, lowest);

			}

		}

	}

	private void setUsed(int[][] used, HashMap<Integer, Integer> corrections, int x, int y, int replacement) {

		if (replacement > 0) {

			int existing = used[x][y];

			if (existing == 0) {

				used[x][y] = replacement;

			} else if (replacement != existing) {

				Integer cr = corrections.get(replacement);
				Integer ce = corrections.get(existing);

				if (replacement < existing) {
					if (ce != null) {
						limitDepth(corrections, existing, replacement, ce);
					} else {
						corrections.put(existing, replacement);
					}

				} else {
					if (cr != null) {
						limitDepth(corrections, replacement, existing, cr);
					} else {
						corrections.put(replacement, existing);
					}
				}

			}
		}

	}

	Region getRegion(HashMap<Integer, Region> regions, int a) {

		Region r = regions.get(a);

		if (r == null) {
			r = new Region();
			regions.put(a, r);
		}
		return r;

	}

	public int countBlack() {
		
		int black = 0;
		
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h ; y++) {
				if (get(x,y) == 0) {
					black++;
				}
			}
		}
		
		return black;
	}

	
	public ArrayList<Double> divideInto9AndGetRatioVector() {
		
		return divideInto9AndGetVector(false);
		
	}
	
	public ArrayList<Double> divideInto9AndGetPercentVector() {
		
		return divideInto9AndGetVector(true);
	}
	
	public ArrayList<Double> divideInto9AndGetVector(boolean percent) {

		double tw = w / 3.0;
		double th = h / 3.0;

		ArrayList<Double> vector = new ArrayList<Double>();
		for (int ty = 0; ty < 3; ty++) {

			for (int tx = 0; tx < 3; tx++) {

				double left = tw * tx;
				double right = tw * (tx + 1);

				double top = th * ty;
				double bottom = th * (ty + 1);

				int li = (int) Math.floor(left);

				int ri = (int) Math.floor(right);

				int ti = (int) Math.floor(top);

				int bi = (int) Math.floor(bottom);

				double black = 0;
				double divBy = 0;

				for (int x = li; x <= ri; x++) {
					for (int y = ti; y <= bi; y++) {
						double xm = 1;
						if (li == ri) {
							xm = right - left;
						} else if (li == x) {
							xm = li + 1 - left;
						} else if (ri == x) {
							xm = right - ri;
						}

						double ym = 1;
						if (ti == bi) {
							ym = bottom - top;
						} else if (ti == y) {
							ym = ti + 1 - top;
						} else if (bi == y) {
							ym = bottom - bi;
						}

						int c = get(x, y);

						if (c == 0) {
							black += xm * ym;
						} else if (c == 255) {
							if (!percent) {
								//ratio div by white
								
								divBy += xm * ym;
							}
						}
						if (percent) {
							// percent, div by total
							divBy +=xm * ym;
						}

					}
				}
				
				vector.add(black/divBy);
				

			}

		}
		return vector;
	}

}

class Region {

	int count = 0;

	Rectangle r;

	public void update(int x, int y) {

		if (r == null) {
			r = new Rectangle(x, y, 1, 1);
		} else {
			r.add(new Rectangle(x, y, 1, 1));
		}

		count++;
	}

	public int getX() {
		return r.x;
	}

	public int getY() {
		return r.y;
	}

	public int getHeight() {
		return r.height;
	}

	public int getWidth() {
		return r.width;
	}

	@Override
	public String toString() {
		return String.format("[%d,%d,%d,%d|%d]", r.x, r.y, r.width, r.height, count);
	}

}

/*	public IntMap scaleSymbols(int newW, int newH) {
	if (newW <= 0)
		newW = 1;
	if (newH <= 0)
		newH = 1;
	
	IntMap res = new IntMap(newW, newH);
	
	float scaleFactorW = (float)newW / w;
	float scaleFactorH = (float)newH / h;
	//System.out.printf("%d / %d, %d / %d\n", newW, w, newH, h);
	//System.out.printf("%f, %f\n", scaleFactorW, scaleFactorH);
	
	for (int fx = 0; fx < newW; fx++) {
		for (int fy = 0; fy < newH; fy++) {
			int sx, sy;
			sx = (int)Math.min(w - 1, fx / scaleFactorW);
			sy = (int)Math.min(h - 1, fy / scaleFactorH);
			int t = get(sx,sy);
			res.set(fx,  fy,  t);
		}
	}
	
	return res;
}*/
