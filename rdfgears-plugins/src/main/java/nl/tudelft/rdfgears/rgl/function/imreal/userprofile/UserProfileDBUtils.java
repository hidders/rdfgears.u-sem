package nl.tudelft.rdfgears.rgl.function.imreal.userprofile;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import nl.tudelft.rdfgears.rgl.function.imreal.uuid.UUIDDBUtils;

public class UserProfileDBUtils {

	// JDBC driver name and database URL
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

	/**
	 * Insert statement used to store (uuid, Dimension, Value, Provider) triples
	 */
	private static final String INSERT_USER_PROFILE_ENTRY_STATEMENT = "INSERT INTO userProfile (uuid_id, dimension, scope, dvalue, provider) VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE dvalue=?";

	/**
	 * Select statement to retrieve entries based on uuid, dimension and
	 * provider
	 */
	private static final String FIND_ENTRY_BY_DIMENTION_PROVIDER_STATEMENT = "SELECT scope, dvalue, provider FROM userProfile LEFT JOIN uuid ON userProfile.uuid_id=uuid.id WHERE uuid.uuid = ? AND dimension = ? AND provider = ? ";

	/**
	 * Stores new profile entry
	 */
	public static void storeProfileEntry(String dbURL, String username,
			String password, String uuid, String dimension, String scope, String value,
			String provider) throws Exception {

		int uuidID = UUIDDBUtils
				.findUUIDbyName(dbURL, username, password, uuid);

		if (uuidID == 0) {
			throw new Exception("No UUID found for name: " + uuid);
		}

		Connection conn = null;
		try {
			// STEP 2: Register JDBC driver
			Class.forName(JDBC_DRIVER);

			// STEP 3: Open a connection
			conn = DriverManager.getConnection(dbURL, username, password);

			// STEP 4: Execute a query
			PreparedStatement stmt = conn
					.prepareStatement(INSERT_USER_PROFILE_ENTRY_STATEMENT);

			stmt.setInt(1, uuidID);
			stmt.setString(2, dimension);
			stmt.setString(3, scope);
			stmt.setString(4, value);
			stmt.setString(5, provider);
			stmt.setString(6, value);

			stmt.executeUpdate();

		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}// end finally try
		}// end try
	}

	/**
	 * Retrieves the user profile for the provided filters
	 */
	public static UserProfile retrieveUserProfile(String dbURL,
			String username, String password, String filterUUID,
			String filterDimension, String filterProvider) throws SQLException,
			ClassNotFoundException {

		Connection conn = null;

		try {
			// STEP 2: Register JDBC driver
			Class.forName(JDBC_DRIVER);

			// STEP 3: Open a connection
			conn = DriverManager.getConnection(dbURL, username, password);

			// STEP 4: Execute a query
			PreparedStatement stmt = null;
			stmt = conn
					.prepareStatement(FIND_ENTRY_BY_DIMENTION_PROVIDER_STATEMENT);
			stmt.setString(1, filterUUID);
			stmt.setString(2, filterDimension);
			stmt.setString(3, filterProvider);

			ResultSet resultSet = stmt.executeQuery();

			List<Dimension.DimensionEntry> dimensionEntries = new ArrayList<Dimension.DimensionEntry>();

			while (resultSet.next()) { // process results one row at a time
				String scope = resultSet.getString(1);
				String value = resultSet.getString(2);
				String provider = resultSet.getString(3);

				dimensionEntries.add(new Dimension.DimensionEntry(scope, value, provider));
			}
			List<Dimension> dimensions = new ArrayList<Dimension>();
			dimensions.add(new Dimension(filterDimension, dimensionEntries));
			
			return new UserProfile(filterUUID, dimensions);

		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}// end finally try
		}// end try
	}

}
