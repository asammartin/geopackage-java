package mil.nga.geopackage.user.custom;

import java.sql.ResultSet;

import mil.nga.geopackage.user.UserResultSet;

/**
 * User Custom Result Set to wrap a database ResultSet for tile queries
 * 
 * @author osbornb
 * @since 3.0.1
 */
public class UserCustomResultSet extends
		UserResultSet<UserCustomColumn, UserCustomTable, UserCustomRow> {

	/**
	 * Constructor
	 * 
	 * @param table
	 *            user custom table
	 * @param resultSet
	 *            result set
	 * @param count
	 *            result count
	 */
	public UserCustomResultSet(UserCustomTable table, ResultSet resultSet,
			int count) {
		super(table, resultSet, count);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserCustomRow getRow(int[] columnTypes, Object[] values) {
		return new UserCustomRow(getTable(), columnTypes, values);
	}

}
