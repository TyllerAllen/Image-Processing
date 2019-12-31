package imageproject.images;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import javax.swing.JOptionPane;

import imageproject.features.FeatureFactory;

public class DistanceMap {

	boolean valid = false;
	
	
	HashMap<String, ArrayList<double[]>> map = new HashMap<String, ArrayList<double[]>>();
	private FeatureFactory<?> ff;
	
	
	

	public DistanceMap(File file, TreeSet<FeatureFactory<?>> featureFactories) throws IOException {

		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);

		String firstLine = br.readLine().strip();
		
		
		
		for (FeatureFactory<?> ffi : featureFactories) {
			
			
			if (firstLine.equals(ffi.getName())) {
				ff = ffi;
				
				break;
			}
			
		}
		
		if (ff != null) {
			
			while (br.ready()) {
				String line = br.readLine();
				String[] split = line.split("=", 2);
				
				
				if (split.length  == 2) {
					
					
					ArrayList<double[]> lists = map.get(split[0]);
					
					if (lists == null) {
						lists = new ArrayList<double[]>();
						map.put(split[0], lists);
					}
					
					double vector[] = ff.getVector(split[1]);
					
					lists.add(vector);
					
				}
				
				
				
			}
			valid = true;
		}else {
			valid = false;
			JOptionPane.showMessageDialog(null, "This image doesn't have data for that feature", "Error", JOptionPane.ERROR_MESSAGE);
		}
		
		
		br.close();
		
		
	
	
	
	}

	
	public FeatureFactory<?> getType (){
		return ff;
	}


	public HashMap<String, ArrayList<Double>> getDistances(double[] vector) {
		
		HashMap<String, ArrayList<Double>> distances = new HashMap<String, ArrayList<Double>>();
		
		
		
		for ( String s : map.keySet()) {
			
			ArrayList<Double> es = distances.get(s);
			
			if (es == null) {
				es = new ArrayList<Double>();
				distances.put(s, es);
			}
			
			
			for (double[] d : map.get(s)) {
				
				double distance =getEuclideanDistance (vector, d);
				es.add(distance);
				
				
			}
			
			
		}
		return distances;
		
	}


	private double getEuclideanDistance(double[] vector, double[] d) {
		double sum = 0;
		for (int i = 0; i < vector.length; i++) {
			sum += Math.pow(vector[i]-d[i],2);
		}
		return Math.sqrt(sum);
	}
	
	
}
