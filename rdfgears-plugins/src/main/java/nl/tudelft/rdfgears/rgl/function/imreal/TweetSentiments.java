package nl.tudelft.rdfgears.rgl.function.imreal;

import java.util.HashMap;

import nl.tudelft.rdfgears.engine.Engine;
import nl.tudelft.rdfgears.engine.ValueFactory;
import nl.tudelft.rdfgears.rgl.datamodel.type.BagType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RDFType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RGLType;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.function.SimplyTypedRGLFunction;
import nl.tudelft.rdfgears.rgl.function.imreal.vocabulary.USEM;
import nl.tudelft.rdfgears.rgl.function.imreal.vocabulary.WI;
import nl.tudelft.rdfgears.rgl.function.imreal.vocabulary.WO;
import nl.tudelft.rdfgears.util.row.ValueRow;

import org.persweb.sentiment.eval.USemSentimentAnalysis;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * A function that computes the "average" sentiment score of a user's last 200 tweets:
 * 
 * (1) score each tweet as positive, negative, neutral
 * (2) compute final score: (pos-neg)/(pos+neg+neutral)
 * 
 * The output format is U-Sem format and makes use of:
 * Ontology: http://marl.gi2mo.org/0.2/ns.html
 * 
 * Output values
 * - positive opinion count
 * - negative opinion count
 * - neutral opinion count
 * - opinion count
 * - overall valency
 * 
 */
public class TweetSentiments extends SimplyTypedRGLFunction {

	public static final String INPUT_USERNAME = "username";
	public static final int MAXHOURS = 12; /* number of hours 'old' data (i.e. tweets retrieved earlier on) are still considered a valid substitute */

	public TweetSentiments() {
		this.requireInputType(INPUT_USERNAME, RDFType.getInstance());

	}
	
	public RGLType getOutputType() {
		return BagType.getInstance(RDFType.getInstance());
	}

	
	@Override
	public RGLValue simpleExecute(ValueRow inputRow) {
		/*
		 * - typechecking guarantees it is an RDFType - simpleExecute guarantees
		 * it is non-null SanityCheck: we must still check whether it is URI or
		 * String, because typechecking doesn't distinguish this!
		 */
		RGLValue rdfValue = inputRow.get(INPUT_USERNAME);
		if (!rdfValue.isLiteral())
			return ValueFactory.createNull("Cannot handle URI input in "
					+ getFullName());

		// we are happy, value can be safely cast with .asLiteral().
		String username = rdfValue.asLiteral().getValueString();
		
		HashMap<String,String> tweets = TweetCollector.getTweetTextWithDateAsKey(username, false, MAXHOURS);
		
		int positive = 0;
		int negative = 0;
		int neutral = 0;
		for(String date : tweets.keySet())
		{
			double result = USemSentimentAnalysis.analyzeTweetSentiment(tweets.get(date));
			
			if(result>0)
				positive++;
			else if(result<0)
				negative++;
			else
				neutral++;
		}
		
		int total = positive + negative + neutral;
		
		double overall_score = (double)(positive-negative)/(double)(total);
		Engine.getLogger().debug("TweetSentiments: positive: "+positive+", neutral: "+neutral+", negative: "+negative+" => score="+overall_score);
		
		/*
		 * We must now convert the languageMap, that was the result of the
		 * external 'component', to an RGL value.
		 */

		RGLValue userProfile = null;
		try 
		{
			userProfile = constructProfile(username, positive, negative, neutral, overall_score);
		} 
		catch (Exception e) 
		{
			return ValueFactory.createNull("Error in "
					+ this.getClass().getCanonicalName() + ": "
					+ e.getMessage());
		}
		return userProfile;
	}
	
	
	/**
	 * Constructs user profile in RDF format.
	 * 
	 */
	private RGLValue constructProfile(String username,
			int positive, int negative, int neutral, double overall_score) throws Exception {

		String userURI = "http://" + username + ".myopenid.com";

		// create an empty Model
		Model model = ModelFactory.createDefaultModel();

		model.setNsPrefix("foaf", FOAF.getURI());
		model.setNsPrefix("usem", USEM.getURI());
		model.setNsPrefix("wo", WO.getURI());
		model.setNsPrefix("wi", WI.getURI());
		//model.setNsPrefix("marl", "http://purl.org/marl/ns");

		// create the resources
		Resource user = model.createResource(userURI);

		user.addProperty(RDF.type, FOAF.Person);
		user.addProperty(FOAF.name, username);

		String[] labels= {"http://purl.org/marl/ns#positiveOpinionsCount","http://purl.org/marl/ns#neutralOpinionCount","http://purl.org/marl/ns#negativeOpinionCount","http://purl.org/marl/ns#opinionCount","http://purl.org/marl/ns#aggregatesOpinion"};
		double[] values={positive, neutral, negative, (positive+negative), overall_score};
		
		for (int i=0; i<labels.length; i++)
		{
			Resource knowledgeResource = model.createResource().addProperty(RDF.type, USEM.WeightedKnowledge);

			user.addProperty(USEM.knowledge, knowledgeResource);

			knowledgeResource.addProperty(WI.topic,
					model.createResource(labels[i]))
					.addProperty(
							WO.weight,
							model.createResource()
									.addProperty(RDF.type, WO.Weight)
									.addLiteral(WO.weight_value,
											values[i])
									.addProperty(WO.scale, USEM.DefaultScale));
		}

		return ValueFactory.createRDFModelValue(model);
	}
	
}
