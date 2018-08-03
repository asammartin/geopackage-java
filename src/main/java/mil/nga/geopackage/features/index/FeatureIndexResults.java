package mil.nga.geopackage.features.index;

import mil.nga.geopackage.features.user.FeatureRow;

/**
 * Iterable Feature Index Results to iterate on feature rows
 *
 * @author osbornb
 * @since 3.0.3
 */
public interface FeatureIndexResults extends Iterable<FeatureRow> {

	/**
	 * Get the count of results
	 *
	 * @return count
	 */
	public long count();

	/**
	 * Close the results
	 */
	public void close();

}
