package nl.tudelft.rdfgears.rgl.function.imreal.userprofile;

import nl.tudelft.rdfgears.engine.ValueFactory;
import nl.tudelft.rdfgears.rgl.datamodel.type.BagType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RDFType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RGLType;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.function.SimplyTypedRGLFunction;
import nl.tudelft.rdfgears.rgl.function.imreal.uuid.UUIDDBUtils;
import nl.tudelft.rdfgears.rgl.function.imreal.vocabulary.USEM;
import nl.tudelft.rdfgears.rgl.function.imreal.vocabulary.WI;
import nl.tudelft.rdfgears.rgl.function.imreal.vocabulary.WO;
import nl.tudelft.rdfgears.util.row.ValueRow;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * A function that provides a user's profile entries
 * 
 */
public class GetUserProfileEntryFunction extends SimplyTypedRGLFunction {

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
	 * The name of the input field providing the topic
	 */
	public static final String INPUT_TOPIC = "topic";

	public GetUserProfileEntryFunction() {
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

		String uuid = rdfValue.asLiteral().getValueString();

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
		
		// ////////////////////////////////////////////////////////////////

		// typechecking the input
		rdfValue = inputRow.get(INPUT_TOPIC);
		if (!rdfValue.isLiteral())
			return ValueFactory.createNull("Cannot handle URI input in "
					+ getFullName());

		String topic = rdfValue.asLiteral().getValueString();

		try {
			String email = UUIDDBUtils.findEmailbyUUID(db_url, username,
					password, uuid);
			return constructProfile(email,
					UserProfileDBUtils.retrieveUserProfile(db_url, username,
							password, uuid, topic));
		} catch (Exception e) {
			e.printStackTrace();
			return ValueFactory.createNull("Error in "
					+ this.getClass().getCanonicalName() + ": "
					+ e.getMessage());
		}

	}

	/**
	 * Constructs user profile in RDF format.
	 * 
	 */
	private RGLValue constructProfile(String userURI, UserProfile userProfile)
			throws Exception {

		Model model = buildRDF(userURI, userProfile);

		return ValueFactory.createRDFModelValue(model);
	}

	private Model buildRDF(String userURI, UserProfile userProfile) {
		if (userURI == null) {
			userURI = "http://" + userProfile.getUuid() + ".myopenid.com";
		} else {
			userURI = "mailto:" + userURI;
		}

		// create an empty Model
		Model model = ModelFactory.createDefaultModel();

		model.setNsPrefix("foaf", FOAF.getURI());
		model.setNsPrefix("usem", USEM.getURI());
		model.setNsPrefix("wo", WO.getURI());
		model.setNsPrefix("wi", WI.getURI());

		// create the resources
		Resource user = model.createResource(userURI);

		user.addProperty(RDF.type, FOAF.Person);
		// user.addProperty(FOAF.name, username);

		for (Dimension.DimensionEntry dimensionEntry : userProfile
				.getDimensions().get(0).getDimensionEntries()) {

			Resource knowledgeResource = model.createResource().addProperty(
					RDF.type, USEM.WeightedKnowledge);

			user.addProperty(USEM.knowledge, knowledgeResource);

			knowledgeResource.addLiteral(WI.topic, dimensionEntry.getTopic()).addProperty(
					WO.weight,
					model.createResource().addProperty(RDF.type, WO.Weight)
							.addLiteral(WO.weight_value, dimensionEntry.getValue())
							.addProperty(WO.scale, USEM.DefaultScale));
		}
		return model;
	}

	public static void main(String[] args) throws Exception {
		Model buildRDF = new GetUserProfileEntryFunction().buildRDF(
				"test@abv.bg", UserProfileDBUtils.retrieveUserProfile(
						"jdbc:mysql://localhost/imreal", "root", "SECRET123",
						"Test", "scope"));

		buildRDF.write(System.out, "RDF/XML-ABBREV", null);
	}

}
