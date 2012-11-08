package nl.tudelft.rdfgears.rgl.function.imreal;

import java.io.BufferedReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import nl.tudelft.rdfgears.engine.Config;
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

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * A function to detect twitter languages based on a twitter username
 * 
 */
public class TwitterLanguageDetector extends SimplyTypedRGLFunction {

	public static final String INPUT_USERNAME = "username";

	/*
	 * profiles can only be loaded once, otherwise the language library crashes.
	 * Static because multiple instances of this RGLFunctions may exist.
	 */
	public static boolean profilesLoaded = false;

	/* cache the results because we don't want to do too much requests */
	private static final Map<String, RGLValue> cachedUserProfiles = new HashMap<String, RGLValue>();
	private static final Map<String, Integer> cacheTimes = new HashMap<String, Integer>();

	public TwitterLanguageDetector() {
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

		/* see if cache it outdated */
		boolean useCachedProfile = false;
		RGLValue userProfile = cachedUserProfiles.get(username);
		if (userProfile != null) {
			userProfile = cachedUserProfiles.get(username);
			int cacheTime = cacheTimes.get(username).intValue();
			int nowTime = getCurrentTimestamp();
			useCachedProfile = (nowTime - cacheTime) < 3600;
		}

		if (!useCachedProfile) {

			HashMap<String, Integer> languageMap;
			try {
				languageMap = detectLanguage(username);
			} catch (Exception e) {
				return ValueFactory.createNull("Error in "
						+ this.getClass().getCanonicalName() + ": "
						+ e.getMessage());
			}

			/*
			 * We must now convert the languageMap, that was the result of the
			 * external 'component', to an RGL value.
			 */

			try {
				userProfile = constructProfile(username, languageMap);
			} catch (Exception e) {
				return ValueFactory.createNull("Error in "
						+ this.getClass().getCanonicalName() + ": "
						+ e.getMessage());
			}

			/* store in result & time in cache */
			cachedUserProfiles.put(username, userProfile);
			cacheTimes.put(username, getCurrentTimestamp());
		}

		return userProfile;
	}

	/**
	 * Constructs user profile in RDF format.
	 * 
	 */
	private RGLValue constructProfile(String username,
			HashMap<String, Integer> languageMap) throws Exception {

		String userURI = "http://" + username + ".myopenid.com";

		// create an empty Model
		Model model = ModelFactory.createDefaultModel();

		model.setNsPrefix("foaf", FOAF.getURI());
		model.setNsPrefix("usem", USEM.getURI());
		model.setNsPrefix("wo", WO.getURI());
		model.setNsPrefix("wi", WI.getURI());
		model.setNsPrefix("dbpedia", "http://dbpedia.org/resource/");

		// create the resources
		Resource user = model.createResource(userURI);

		user.addProperty(RDF.type, FOAF.Person);
		user.addProperty(FOAF.name, username);

		for (String lang : languageMap.keySet()) {

			Resource knowledgeResource = model.createResource().addProperty(
					RDF.type, USEM.WeightedKnowledge);

			user.addProperty(USEM.knowledge, knowledgeResource);

			knowledgeResource.addProperty(WI.topic,
					model.createResource(getDbpediaLanguage(lang)))
					.addProperty(
							WO.weight,
							model.createResource()
									.addProperty(RDF.type, WO.Weight)
									.addLiteral(WO.weight_value,
											languageMap.get(lang))
									.addProperty(WO.scale, USEM.DefaultScale));
		}

		return ValueFactory.createRDFModelValue(model);
	}

	/**
	 * Executes sparql request to dbpedia in order to get the dbpedia uri for
	 * the provided language iso.
	 */
	private String getDbpediaLanguage(String iso) throws Exception {
		String sparqlService = "http://dbpedia.org/sparql";

		String query = "PREFIX dbpprop: <http://dbpedia.org/property/> "
				+ "PREFIX dbo: <http://dbpedia.org/ontology/> "
				+ "select ?language ?isocode" + "where { "
				+ "?language dbpprop:iso ?isocode. "
				+ "?language a dbo:Language. " + "FILTER (?isocode = \"" + iso
				+ "\"@en) " + "}";

		ResultSet results = executeQuery(query, sparqlService);

		// returns the first entry in the result set
		for (; results.hasNext();) {
			QuerySolution soln = results.nextSolution();
			Resource name = soln.getResource("language");
			return name.getURI();
		}

		return null;
	}

	/**
	 * Executes Jena query
	 */
	private ResultSet executeQuery(String queryString, String service)
			throws Exception {
		Query query = QueryFactory.create(queryString);

		QueryEngineHTTP qexec = QueryExecutionFactory.createServiceRequest(
				service, query);
		ResultSet results = qexec.execSelect();
		return results;

	}

	/* get unix timestamp (seconds) */
	private int getCurrentTimestamp() {
		return (int) (System.currentTimeMillis() / 1000L);
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
		
		HashMap<String,String> tweets = TweetCollector.getTweetTextWithDateAsKey(twitterUser, true, 200);//include retweets, 200 hours 'oldness'

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
