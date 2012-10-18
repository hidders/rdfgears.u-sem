package nl.tudelft.rdfgears.rgl.function.imreal.uuid;

import nl.tudelft.rdfgears.engine.ValueFactory;
import nl.tudelft.rdfgears.rgl.datamodel.type.BagType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RDFType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RGLType;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.function.SimplyTypedRGLFunction;
import nl.tudelft.rdfgears.util.row.ValueRow;

/**
 * A function to register UUID into the database
 * 
 */
public class RegisterUUIDFunction extends SimplyTypedRGLFunction {

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
	 * The name of the input field providing the email address
	 */
	public static final String INPUT_EMAIL = "email";

	public RegisterUUIDFunction() {
		this.requireInputType(INPUT_EMAIL, RDFType.getInstance());

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
		RGLValue rdfValue = inputRow.get(INPUT_EMAIL);
		if (!rdfValue.isLiteral())
			return ValueFactory.createNull("Cannot handle URI input in "
					+ getFullName());

		// extracting the twitter username from the input
		String email = rdfValue.asLiteral().getValueString();

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
			String existingUUID = UUIDDBUtils.findUUIDbyEmail(db_url, username, password, email);
			
			if(existingUUID != null){
				return ValueFactory.createLiteralPlain(existingUUID, null);
			}
			
			String generatedUUID = UUIDDBUtils.storeNewUUID(db_url, username, password, email);
			
			return ValueFactory.createLiteralPlain(generatedUUID, null);
			 
		} catch (Exception e) {
			e.printStackTrace();
			return ValueFactory.createNull("Error in "
					+ this.getClass().getCanonicalName() + ": "
					+ e.getMessage());
		}
	}

}
