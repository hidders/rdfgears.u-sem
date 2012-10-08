package nl.tudelft.rdfgears.rgl.function.imreal.uuid;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

import nl.tudelft.rdfgears.engine.ValueFactory;
import nl.tudelft.rdfgears.rgl.datamodel.type.BagType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RDFType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RGLType;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.function.SimplyTypedRGLFunction;
import nl.tudelft.rdfgears.rgl.function.imreal.vocabulary.IMREAL;
import nl.tudelft.rdfgears.util.row.ValueRow;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * A function that provides a list of web IDs for a given UUID
 * 
 */
public class RetrieveUUIDFunction extends SimplyTypedRGLFunction {

	// JDBC driver name and database URL
	private final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

	/**
	 * Select statement that is used to retrieve all (web id, provider) pairs
	 * corresponding to provided uuid
	 */
	private final String SELECT_STATEMENT = "SELECT webid, provider FROM UUID WHERE uuid = ?";

	/**
	 * The name of the input field providing the database url
	 */
	public static final String INPUT_DB = "db";

	/**
	 * The name of the input field providing the database username
	 */
	public static final String INPUT_USERNAME = "db_username";

	/**
	 * The name of the input field providing the database password
	 */
	public static final String INPUT_PASSWORD = "db_password";

	/**
	 * The name of the input field providing the uuid
	 */
	public static final String INPUT_UUID = "uuid";

	public RetrieveUUIDFunction() {
		this.requireInputType(INPUT_UUID, RDFType.getInstance());

		this.requireInputType(INPUT_DB, RDFType.getInstance());
		this.requireInputType(INPUT_USERNAME, RDFType.getInstance());
		this.requireInputType(INPUT_PASSWORD, RDFType.getInstance());
	}

	@Override
	public RGLType getOutputType() {
		return BagType.getInstance(RDFType.getInstance());
	}

	@Override
	public RGLValue simpleExecute(ValueRow inputRow) {
		// typechecking the input
		RGLValue rdfValue = inputRow.get(INPUT_UUID);
		if (!rdfValue.isLiteral())
			return ValueFactory.createNull("Cannot handle URI input in "
					+ getFullName());

		// extracting the twitter username from the input
		String uuid = rdfValue.asLiteral().getValueString();

		// ////////////////////////////////////////////////////////////////

		// typechecking the input
		rdfValue = inputRow.get(INPUT_DB);
		if (!rdfValue.isLiteral())
			return ValueFactory.createNull("Cannot handle URI input in "
					+ getFullName());

		// extracting the twitter username from the input
		String db_url = "jdbc:mysql://" + rdfValue.asLiteral().getValueString();

		// ////////////////////////////////////////////////////////////////

		// typechecking the input
		rdfValue = inputRow.get(INPUT_USERNAME);
		if (!rdfValue.isLiteral())
			return ValueFactory.createNull("Cannot handle URI input in "
					+ getFullName());

		// extracting the twitter username from the input
		String username = rdfValue.asLiteral().getValueString();

		// ////////////////////////////////////////////////////////////////

		// typechecking the input
		rdfValue = inputRow.get(INPUT_PASSWORD);
		if (!rdfValue.isLiteral())
			return ValueFactory.createNull("Cannot handle URI input in "
					+ getFullName());

		// extracting the twitter username from the input
		String password = rdfValue.asLiteral().getValueString();

		try {
			return constructProfile(uuid,
					retrieve(db_url, username, password, uuid));
		} catch (Exception e) {
			e.printStackTrace();
			return ValueFactory.createNull("Error in "
					+ this.getClass().getCanonicalName() + ": "
					+ e.getMessage());
		}

	}

	/**
	 * Retrieves the web IDs and providers for the provided uuid
	 */
	private List<SimpleEntry<String, String>> retrieve(String dbURL,
			String username, String password, String uuid) throws SQLException,
			ClassNotFoundException {

		Connection conn = null;
		List<SimpleEntry<String, String>> result = new ArrayList<AbstractMap.SimpleEntry<String, String>>();

		try {
			// STEP 2: Register JDBC driver
			Class.forName(JDBC_DRIVER);

			// STEP 3: Open a connection
			conn = DriverManager.getConnection(dbURL, username, password);

			// STEP 4: Execute a query
			PreparedStatement stmt = conn.prepareStatement(SELECT_STATEMENT);

			stmt.setString(1, uuid);

			ResultSet resultSet = stmt.executeQuery();

			while (resultSet.next()) { // process results one row at a time
				String webid = resultSet.getString(1);
				String provider = resultSet.getString(2);

				result.add(new SimpleEntry<String, String>(webid, provider));
			}

		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}// end finally try
		}// end try

		return result;
	}

	/**
	 * Constructs user profile in RDF format.
	 * 
	 */
	private RGLValue constructProfile(String uuid,
			List<SimpleEntry<String, String>> list) throws Exception {

		// create an empty Model
		Model model = ModelFactory.createDefaultModel();

		model.setNsPrefix("foaf", FOAF.getURI());
		model.setNsPrefix("imreal", IMREAL.getURI());

		// create the resources
		Resource user = model.createResource(IMREAL.getURI() + uuid);

		user.addProperty(RDF.type, FOAF.Person);

		for (SimpleEntry<String, String> entry : list) {

			user.addProperty(
					model.createProperty(IMREAL.getURI(), entry.getValue()
							+ "ID"), entry.getKey());
		}

		return ValueFactory.createRDFModelValue(model);
	}

}
