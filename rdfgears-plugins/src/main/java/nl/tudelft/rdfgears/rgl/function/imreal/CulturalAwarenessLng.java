package nl.tudelft.rdfgears.rgl.function.imreal;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import nl.tudelft.rdfgears.engine.Config;
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

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * Based on the number of languages detected in tweets, a cultural awareness value is returned
 * 
 * 0 if only 1 language is detected
 * 1 if 2 languages are detected
 * 2 if 3+ languages are detected
 * 
 * (since the language detection library has "stray" values, we consider a language as discovered if 5 tweets occured in it)
 * 
 */
public class CulturalAwarenessLng extends SimplyTypedRGLFunction {

	public static final String INPUT_USERNAME = "username";
	public static final int MAXHOURS = 2*24; /* number of hours 'old' data (i.e. tweets retrieved earlier on) are still considered a valid substitute */

	/*
	 * profiles can only be loaded once, otherwise the language library crashes.
	 * Static because multiple instances of this RGLFunctions may exist.
	 */
	public static boolean profilesLoaded = false;

	public CulturalAwarenessLng() {
		this.requireInputType(INPUT_USERNAME, RDFType.getInstance());

	}

	public RGLType getOutputType() {
		return BagType.getInstance(RDFType.getInstance());
	}

	@Override
	public RGLValue simpleExecute(ValueRow inputRow) 
	{
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

		HashMap<String, Integer> languageMap;
		try 
		{
			languageMap = detectLanguage(username);
		} catch (Exception e) 
		{
			return ValueFactory.createNull("Error in "
					+ this.getClass().getCanonicalName() + ": "
					+ e.getMessage());
		}

		int numLanguages = 0;
		for(String language : languageMap.keySet())
		{
			if( languageMap.get(language) >= 5)
				numLanguages++;
		}

		RGLValue userProfile = null;
		try 
		{
			userProfile = constructProfile(username, numLanguages);
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
		int numLanguages) throws Exception 
		{
		String userURI = "http://" + username + ".myopenid.com";
	
		// create an empty Model
		Model model = ModelFactory.createDefaultModel();
	
		model.setNsPrefix("foaf", FOAF.getURI());
		model.setNsPrefix("usem", USEM.getURI());
		model.setNsPrefix("wo", WO.getURI());
		model.setNsPrefix("wi", WI.getURI());
		//model.setNsPrefix("dbpedia", "http://dbpedia.org/resource/");
	
		// create the resources
		Resource user = model.createResource(userURI);
	
		user.addProperty(RDF.type, FOAF.Person);
		user.addProperty(FOAF.name, username);
	
		double awarenessScore = 1.0;
		if(numLanguages==2)
			awarenessScore = 2.0;
		if(numLanguages>=3)
			awarenessScore = 3.0;
	
			Resource knowledgeResource = model.createResource().addProperty(
					RDF.type, USEM.WeightedKnowledge);
	
			user.addProperty(USEM.knowledge, knowledgeResource);
	
			knowledgeResource.addProperty(WI.topic,
					model.createResource("http://dbpedia.org/resource/Cultural_competence"))
					.addProperty(
							WO.weight,
							model.createResource()
									.addProperty(RDF.type, WO.Weight)
									.addLiteral(WO.weight_value,
											awarenessScore)
									.addProperty(WO.scale, USEM.DefaultScale));
		
	
		return ValueFactory.createRDFModelValue(model);
	}


	/**
	 * will throw Exception on failure
	 * 
	 * @param twitterUser
	 * @return
	 * @throws LangDetectException
	 * @throws IOException
	 */
	private HashMap<String, Integer> detectLanguage(String twitterUser)
			throws LangDetectException, IOException {
		
		HashMap<String,String> tweets = TweetCollector.getTweetTextWithDateAsKey(twitterUser, true, MAXHOURS);

		/* *************
		 * The dir with the language profiles is assumed to be stored in the
		 * tmpdir. As it is read-only, it may be nicer to package it in the jar
		 * instead.... But the jar directory contents are not easily listed by
		 * the langdetect tool.
		 */
		File profileDir = new File(Config.getWritableDir()
				+ "/imreal-language-profiles"); /*
												 * should be cross-platform and
												 * work in webapps
												 */
		if (!profilesLoaded) {
			DetectorFactory.loadProfile(profileDir);
			profilesLoaded = true;
		}

		HashMap<String, Integer> languageMap = new HashMap<String, Integer>();

		for(String key : tweets.keySet())
		{
			String tweetText = tweets.get(key);

			// language of the tweet
			Detector detect = DetectorFactory.create();
			detect.append(tweetText);
			String lang = detect.detect();
			if (languageMap.containsKey(lang) == true) 
			{
				int val = languageMap.get(lang) + 1;
				languageMap.put(lang, val);
			} 
			else
				languageMap.put(lang, 1);
		}
		return languageMap;
	}

}
