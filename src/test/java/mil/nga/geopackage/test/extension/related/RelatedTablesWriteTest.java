package mil.nga.geopackage.test.extension.related;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import junit.framework.TestCase;
import mil.nga.geopackage.extension.related.ExtendedRelation;
import mil.nga.geopackage.extension.related.RelatedTablesExtension;
import mil.nga.geopackage.extension.related.UserMappingDao;
import mil.nga.geopackage.extension.related.UserMappingRow;
import mil.nga.geopackage.extension.related.UserMappingTable;
import mil.nga.geopackage.features.user.FeatureDao;
import mil.nga.geopackage.features.user.FeatureResultSet;
import mil.nga.geopackage.test.LoadGeoPackageTestCase;
import mil.nga.geopackage.test.TestConstants;
import mil.nga.geopackage.user.custom.UserCustomColumn;
import mil.nga.geopackage.user.custom.UserCustomResultSet;

import org.junit.Test;

/**
 * Test Related Tables Extension writing
 * 
 * @author jyutzler
 */
public class RelatedTablesWriteTest extends LoadGeoPackageTestCase {

	/**
	 * Constructor
	 */
	public RelatedTablesWriteTest() {
		super(TestConstants.IMPORT_DB_FILE_NAME);
	}

	/**
	 * Test write relationships
	 *
	 * @throws SQLException
	 * @throws IOException
	 */
	@Test
	public void testWriteRelationships() throws SQLException, IOException {

		RelatedTablesExtension rte = new RelatedTablesExtension(geoPackage);

		// 1. Has extension
		TestCase.assertFalse(rte.has());

		// 4. Get relationships
		List<ExtendedRelation> extendedRelations = rte.getRelationships();
		TestCase.assertTrue(extendedRelations.isEmpty());

		// 2. Add extension
		// 5. Add relationship between "geometry2d" and "geometry3d"
		final String baseTableName = "geometry2d";
		final String relatedTableName = "geometry3d";
		final String mappingTableName = "g2d_3d";

		List<UserCustomColumn> additionalColumns = RelatedTablesUtils
				.createAdditionalUserColumns(UserMappingTable
						.numRequiredColumns());

		UserMappingTable userMappingTable = UserMappingTable.create(
				mappingTableName, additionalColumns);
		TestCase.assertFalse(rte.has(userMappingTable.getTableName()));
		TestCase.assertEquals(UserMappingTable.numRequiredColumns()
				+ additionalColumns.size(), userMappingTable.getColumns()
				.size());
		UserCustomColumn baseIdColumn = userMappingTable.getBaseIdColumn();
		TestCase.assertNotNull(baseIdColumn);
		TestCase.assertTrue(baseIdColumn
				.isNamed(UserMappingTable.COLUMN_BASE_ID));
		TestCase.assertTrue(baseIdColumn.isNotNull());
		TestCase.assertFalse(baseIdColumn.isPrimaryKey());
		UserCustomColumn relatedIdColumn = userMappingTable
				.getRelatedIdColumn();
		TestCase.assertNotNull(relatedIdColumn);
		TestCase.assertTrue(relatedIdColumn
				.isNamed(UserMappingTable.COLUMN_RELATED_ID));
		TestCase.assertTrue(relatedIdColumn.isNotNull());
		TestCase.assertFalse(relatedIdColumn.isPrimaryKey());

		TestCase.assertFalse(rte.has(userMappingTable.getTableName()));
		ExtendedRelation extendedRelation = rte.addFeaturesRelationship(
				baseTableName, relatedTableName, userMappingTable);
		TestCase.assertTrue(rte.has());
		TestCase.assertTrue(rte.has(userMappingTable.getTableName()));
		TestCase.assertNotNull(extendedRelation);
		extendedRelations = rte.getRelationships();
		TestCase.assertEquals(1, extendedRelations.size());
		TestCase.assertTrue(geoPackage.getDatabase().tableExists(
				mappingTableName));

		// 7. Add mappings
		FeatureDao baseDao = geoPackage.getFeatureDao(baseTableName);
		FeatureDao relatedDao = geoPackage.getFeatureDao(relatedTableName);
		FeatureResultSet baseFrs = baseDao.queryForAll();
		int baseCount = baseFrs.getCount();
		long[] baseIds = new long[baseCount];
		int inx = 0;
		while (baseFrs.moveToNext()) {
			baseIds[inx++] = baseFrs.getRow().getId();
		}
		baseFrs.close();
		FeatureResultSet relatedFrs = relatedDao.queryForAll();
		int relatedCount = relatedFrs.getCount();
		long[] relatedIds = new long[relatedCount];
		inx = 0;
		while (relatedFrs.moveToNext()) {
			relatedIds[inx++] = relatedFrs.getRow().getId();
		}
		relatedFrs.close();
		UserMappingDao dao = rte.getMappingDao(mappingTableName);
		UserMappingRow userMappingRow = null;
		for (inx = 0; inx < 10; inx++) {
			userMappingRow = dao.newRow();
			userMappingRow.setBaseId(((int) Math.floor(Math.random()
					* baseCount)));
			userMappingRow.setRelatedId(((int) Math.floor(Math.random()
					* relatedCount)));
			RelatedTablesUtils.populateUserRow(userMappingTable,
					userMappingRow, UserMappingTable.requiredColumns());
			TestCase.assertTrue(dao.create(userMappingRow) > 0);
		}

		TestCase.assertEquals(10, dao.count());

		userMappingTable = dao.getTable();
		String[] columns = userMappingTable.getColumnNames();
		UserCustomResultSet resultSet = dao.queryForAll();
		int count = resultSet.getCount();
		TestCase.assertEquals(10, count);
		int manualCount = 0;
		while (resultSet.moveToNext()) {

			UserMappingRow resultRow = dao.getRow(resultSet);
			TestCase.assertFalse(resultRow.hasId());
			RelatedTablesUtils.validateUserRow(columns, resultRow);
			RelatedTablesUtils.validateDublinCoreColumns(resultRow);

			manualCount++;
		}
		TestCase.assertEquals(count, manualCount);
		resultSet.close();

		// 8. Remove mappings (note: it is plausible and allowed
		// to have duplicate entries)
		TestCase.assertTrue(dao.deleteByIds(userMappingRow) > 0);

		// 6. Remove relationship
		rte.removeRelationship(extendedRelation);
		TestCase.assertFalse(rte.has(userMappingTable.getTableName()));
		extendedRelations = rte.getRelationships();
		TestCase.assertEquals(0, extendedRelations.size());
		TestCase.assertFalse(geoPackage.getDatabase().tableExists(
				mappingTableName));

		// 3. Remove extension
		rte.removeExtension();
		TestCase.assertFalse(rte.has());
	}

}
