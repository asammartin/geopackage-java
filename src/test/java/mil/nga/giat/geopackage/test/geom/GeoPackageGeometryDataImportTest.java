package mil.nga.giat.geopackage.test.geom;

import java.io.IOException;
import java.sql.SQLException;

import mil.nga.giat.geopackage.test.ImportGeoPackageTestCase;

import org.junit.Test;

/**
 * Test GeoPackage Geometry Data from an imported database
 * 
 * @author osbornb
 */
public class GeoPackageGeometryDataImportTest extends ImportGeoPackageTestCase {

	/**
	 * Constructor
	 */
	public GeoPackageGeometryDataImportTest() {

	}

	/**
	 * Test reading and writing (and comparing) geometry bytes
	 * 
	 * @throws SQLException
	 * @throws IOException
	 */
	@Test
	public void testReadWriteBytes() throws SQLException, IOException {

		GeoPackageGeometryDataUtils.testReadWriteBytes(geoPackage);

	}

}
