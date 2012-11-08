package nl.tudelft.rdfgears.rgl.function.imreal.userprofile;

import nl.tudelft.rdfgears.engine.ValueFactory;
import nl.tudelft.rdfgears.rgl.datamodel.type.BagType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RDFType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RGLType;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.function.SimplyTypedRGLFunction;
import nl.tudelft.rdfgears.util.row.ValueRow;

/**
 * A function to store user profile entries into the database
 * 
 */
public class SetUserProfileEntryFunction extends SimplyTypedRGLFunction {

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

	/**
	 * The name of the input field providing the dimension
	 */
	public static final String INPUT_DIMENSION = "dimension";

	/**
	 * The name of the input field providing the dimension
	 */
	public static final String INPUT_SCOPE = "scope";

	/**
	 * The name of the input field providing the value
	 */
	public static final String INPUT_VALUE = "value";

	/**
	 * The name of the input field providing the value provider
	 */
	public static final String INPUT_PROVIDER = "provider";

	public SetUserProfileEntryFunction() {
		this.requireInputType(INPUT_UUID, RDFType.getInstance());
		this.requireInputType(INPUT_DIMENSION, RDFType.getInstance());
		this.requireInputType(INPUT_SCOPE, RDFType.getInstance());
		this.requireInputType(INPUT_VALUE, RDFType.getInstance());
		this.requireInputType(INPUT_PROVIDER, RDFType.getInstance());

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

		String uuid = rdfValue.asLiteral().getValueString();

		// /////////////////////////////////////////////////////////////////

		// typechecking the input
		rdfValue = inputRow.get(INPUT_DIMENSION);
		if (!rdfValue.isLiteral())
			return ValueFactory.createNull("Cannot handle URI input in "
					+ getFullName());

		String dimension = rdfValue.asLiteral().getValueString();

		// ////////////////////////////////////////////////////////////////

		// typechecking the input
		rdfValue = inputRow.get(INPUT_SCOPE);
		if (!rdfValue.isLiteral())
			return ValueFactory.createNull("Cannot handle URI input in "
					+ getFullName());

		String scope = rdfValue.asLiteral().getValueString();

		// ////////////////////////////////////////////////////////////////

		// typechecking the input
		rdfValue = inputRow.get(INPUT_VALUE);
		if (!rdfValue.isLiteral())
			return ValueFactory.createNull("Cannot handle URI input in "
					+ getFullName());

		String value = rdfValue.asLiteral().getValueString();

		// ////////////////////////////////////////////////////////////////

		// typechecking the input
		rdfValue = inputRow.get(INPUT_PROVIDER);
		if (!rdfValue.isLiteral())
			return ValueFactory.createNull("Cannot handle URI input in "
					+ getFullName());

		String provider = rdfValue.asLiteral().getValueString();

		// ////////////////////////////////////////////////////////////////

		// typechecking the input
		rdfValue = inputRow.get(INPUT_DB);
		if (!rdfValue.isLiteral())
			return ValueFactory.createNull("Cannot handle URI input in "
					+ getFullName());

		String db_url = "jdbc:mysql://" + rdfValue.asLiteral().getValueString();

		// ////////////////////////////////////////////////////////////////

		// typechecking the input
		rdfValue = inputRow.get(INPUT_USERNAME);
		if (!rdfValue.isLiteral())
			return ValueFactory.createNull("Cannot handle URI input in "
					+ getFullName());

		String username = rdfValue.asLiteral().getValueString();

		// ////////////////////////////////////////////////////////////////

		// typechecking the input
		rdfValue = inputRow.get(INPUT_PASSWORD);
		if (!rdfValue.isLiteral())
			return ValueFactory.createNull("Cannot handle URI input in "
					+ getFullName());

		String password = rdfValue.asLiteral().getValueString();

		try {
			UserProfileDBUtils.storeProfileEntry(db_url, username, password,
					uuid, dimension, scope, value, provider);
		} catch (Exception e) {
			e.printStackTrace();
			return ValueFactory.createNull("Error in "
					+ this.getClass().getCanonicalName() + ": "
					+ e.getMessage());
		}

		return ValueFactory
				.createNull("The user profile entry has been stored successfully.");
	}

	public static void main(String[] args) throws Exception {
		UserProfileDBUtils.storeProfileEntry("jdbc:mysql://localhost/imreal",
				"root", "SECRET123", "Test", "dimension", "scope", "value", "provider");
	}

}