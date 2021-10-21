package org.eclipse.basyx.regression.sql;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.basyx.regression.sqlproxy.SQLConfig;
import org.eclipse.basyx.tools.sql.driver.SQLDriver;
import org.eclipse.basyx.tools.sql.query.DynamicSQLQuery;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class DynamicSQLQueryTest {
	public static final String SCHEMA_NAME = "basyxtest";
	public static final String TABLE_NAME = "queries";
	public static final String FULL_TABLE_NAME = SCHEMA_NAME + "." + TABLE_NAME;

	private static final String COLNAME_INT = "TestInteger";
	private static final String COLNAME_STRING = "TestString";
	private static final String COLNAME_BYTES = "TestBytes";

	private static SQLDriver driver;

	@BeforeClass
	public static void setUp() throws SQLException {
		driver = new SQLDriver("//localhost/basyx-map?", SQLConfig.SQLUSER, SQLConfig.SQLPW, "jdbc:postgresql:", "org.postgresql.Driver");

		createQueryTestSchema();
		createTestTable();
	}

	@AfterClass
	public static void tearDown() {
		dropTestTable();
		dropQueryTestSchema();
	}

	@Test
	public void testSimpleQuery() throws SQLException {
		Map<String, Object> expectedData = getExpectedData();
		writeTestData(expectedData);
		List<Map<String, Object>> queriedDataList = queryTestData();
		assertEquals(1, queriedDataList.size());
		Map<String, Object> queriedDataEntry = queriedDataList.get(0);
		assertEquals(expectedData.get(COLNAME_INT), queriedDataEntry.get(COLNAME_INT));
		assertEquals(expectedData.get(COLNAME_STRING), queriedDataEntry.get(COLNAME_STRING));
		assertArrayEquals((byte[]) expectedData.get(COLNAME_BYTES), (byte[]) queriedDataEntry.get(COLNAME_BYTES));
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> queryTestData() {
		String sqlQueryString = "SELECT * FROM " + FULL_TABLE_NAME + ";";
		String colTypeList = getExpectedColumnTypes().entrySet().stream()
				.map(e -> e.getKey() + ":" + e.getValue())
				.collect(Collectors.joining(","));
		String sqlResultFilter = "listOfMaps(" + colTypeList + ")";
		DynamicSQLQuery query = new DynamicSQLQuery(driver, sqlQueryString, sqlResultFilter);
		return (List<Map<String, Object>>) query.get();
	}

	private static Map<String, String> getExpectedColumnTypes() {
		Map<String, String> colFilterTypes = new HashMap<>();
		colFilterTypes.put(COLNAME_INT, "Integer");
		colFilterTypes.put(COLNAME_STRING, "String");
		colFilterTypes.put(COLNAME_BYTES, "Byte[]");
		return colFilterTypes;
	}

	private static Map<String, String> getColumnSQLTypes() {
		Map<String, String> colSQLTypes = new HashMap<>();
		colSQLTypes.put(COLNAME_INT, "INT");
		colSQLTypes.put(COLNAME_STRING, "VARCHAR(255)");
		colSQLTypes.put(COLNAME_BYTES, "BYTEA");
		return colSQLTypes;
	}

	private static String getSQLColumnTypes() {
		return getColumnSQLTypes().entrySet().stream()
				.map(e -> e.getKey() + " " + e.getValue())
				.collect(Collectors.joining(",", "(", ")"));
	}

	private static Map<String, Object> getExpectedData() {
		Map<String, Object> testData = new HashMap<>();
		testData.put(COLNAME_INT, 35);
		testData.put(COLNAME_STRING, "HelloWorld");
		testData.put(COLNAME_BYTES, new byte[] { -1, 24, 0, -123, 127, 6 });
		return testData;
	}

	private void writeTestData(Map<String, Object> testData) throws SQLException {
		String updateString = generateUpdateTestDataSQLString(getColumnSQLTypes());
		System.out.println(updateString);
		driver.openConnection();
		PreparedStatement statement = driver.getConnection().prepareStatement(updateString);
		statement.setInt(1, (int) testData.get(COLNAME_INT));
		statement.setString(3, (String) testData.get(COLNAME_STRING));
		byte[] byteData = (byte[]) testData.get(COLNAME_BYTES);
		statement.setBinaryStream(2, new ByteArrayInputStream(byteData), byteData.length);
		statement.execute();
		driver.closeConnection();
	}

	private String generateUpdateTestDataSQLString(Map<String, String> colSQLTypes) {
		String colList = colSQLTypes.keySet().stream().collect(Collectors.joining(",", "(", ")"));
		String typeList = colSQLTypes.entrySet().stream()
				.map(e -> "?::" + e.getValue())
				.collect(Collectors.joining(", ", "(", ")"));
		return "INSERT INTO " + FULL_TABLE_NAME + " " + colList + " VALUES " + typeList + ";";

	}

	private static void createQueryTestSchema() {
		String sqlCommandString = "CREATE SCHEMA IF NOT EXISTS " + SCHEMA_NAME + ";";
		driver.sqlUpdate(sqlCommandString);
	}

	private static void createTestTable() {
		String sqlCommandString = "CREATE TABLE IF NOT EXISTS " + FULL_TABLE_NAME + " " + getSQLColumnTypes() + ";";
		driver.sqlUpdate(sqlCommandString);
	}

	private static void dropTestTable() {
		String sqlCommandString = "DROP TABLE IF EXISTS " + FULL_TABLE_NAME + ";";
		driver.sqlUpdate(sqlCommandString);
	}

	private static void dropQueryTestSchema() {
		String sqlCommandString = "DROP SCHEMA IF EXISTS " + SCHEMA_NAME + " RESTRICT;";
		driver.sqlUpdate(sqlCommandString);
	}
}

