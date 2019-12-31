package imageproject.features;

import imageproject.images.ImageObject;

public abstract class FeatureFactory <T extends Feature> implements Comparable<FeatureFactory<Feature>> {

	public static FeatureFactory<Feature> blankFactory = new FeatureFactory<Feature>() {

		@Override
		public String getName() {
			return "No Feature Selected";
		}

		@Override
		public Feature createFeature(ImageObject o) {
			return null;
		}

		@Override
		public boolean canSave() {
			return false;
		}

		@Override
		public double[] getVector(String text) {
			return new double[] {};
		}
	};

	public abstract T createFeature(ImageObject o);

	public abstract String getName();

	public abstract boolean canSave();

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public int compareTo( FeatureFactory <Feature> ff2) {
		if (this == ff2) {
			return 0;
		} else {
			return this.getName().compareTo(ff2.getName());
		}
	}

	public abstract double[] getVector(String text);

}
