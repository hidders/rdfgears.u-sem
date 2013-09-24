package nl.tudelft.rdfgears.rgl.function.imreal;

/*
 * #%L
 * RDFGears
 * %%
 * Copyright (C) 2013 WIS group at the TU Delft (http://www.wis.ewi.tudelft.nl/)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.List;

import nl.tudelft.rdfgears.engine.Config;
import nl.tudelft.rdfgears.engine.ValueFactory;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.function.imreal.userprofile.Dimension;
import nl.tudelft.rdfgears.rgl.function.imreal.userprofile.UserProfile;
import nl.tudelft.rdfgears.rgl.function.imreal.vocabulary.IMREAL;
import nl.tudelft.rdfgears.rgl.function.imreal.vocabulary.USEM;
import nl.tudelft.rdfgears.rgl.function.imreal.vocabulary.WI;
import nl.tudelft.rdfgears.rgl.function.imreal.vocabulary.WO;

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
 * A class that is responsible for creating the U-Sem format based user
 * profiles. It is a lot of hard-coding right now, should be made more generic
 * in the future.
 * 
 * A lot of code duplication as well ...
 * 
 * @author Claudia
 */

public class UserProfileGenerator {
	
	private static HashMap<String,String> languageMap = new HashMap<String,String>();
	
	private static Model createEmptyModel() {
		// create an empty Model
		Model model = ModelFactory.createDefaultModel();

		model.setNsPrefix("foaf", FOAF.getURI());
		model.setNsPrefix("imreal", IMREAL.getURI());
		model.setNsPrefix("usem", USEM.getURI());
		model.setNsPrefix("wo", WO.getURI());
		model.setNsPrefix("wi", WI.getURI());
		model.setNsPrefix("dbpedia", "http://dbpedia.org/resource/");
		model.setNsPrefix("marl", "http://purl.org/marl/ns");

		return model;
	}

	// called from ListSocialIDsFunction.java
	public static RGLValue constructSocialIDListProfile(String uuid,
			List<SimpleEntry<String, String>> list) throws Exception {

		Model model = createEmptyModel();

		// create the resources
		Resource user = model.createResource(IMREAL.getURI() + uuid);
		user.addProperty(RDF.type, FOAF.Person);
		for (SimpleEntry<String, String> entry : list)
			user.addProperty(
					model.createProperty(IMREAL.getURI() + entry.getValue()),
					entry.getKey());

		return ValueFactory.createGraphValue(model);
	}

	// called from GetUserProfileEntryFunction.java
	public static RGLValue constructUserProfileEntryProfile(String uuid,
			UserProfile userProfile) throws Exception {

		Model model = createEmptyModel();

		// create the resources
		Resource user = model.createResource(IMREAL.getURI() + uuid);
		user.addProperty(RDF.type, FOAF.Person);

		for (Dimension.DimensionEntry dimensionEntry : userProfile
				.getDimensions().get(0).getDimensionEntries()) {

			Resource knowledgeResource = model.createResource().addProperty(
					RDF.type, USEM.WeightedKnowledge);

			user.addProperty(USEM.knowledge, knowledgeResource);

			knowledgeResource.addLiteral(WI.topic, dimensionEntry.getTopic())
					.addProperty(
							WO.weight,
							model.createResource()
									.addProperty(RDF.type, WO.Weight)
									.addLiteral(WO.weight_value,
											dimensionEntry.getValue())
									.addProperty(WO.scale, USEM.DefaultScale));
		}
		return ValueFactory.createGraphValue(model);
	}

	/*
	 * public method kept generic to make alterations easier later on; the
	 * userid can either be a uuid or a service user name
	 */
	public static RGLValue generateProfile(Object obj, String userid,
			HashMap<String, Double> map) {
		try {
			if (obj.getClass() == TwitterLanguageDetector.class) {
				return constructTwitterLanguageDetectorProfile(userid, map);
			} else if (obj.getClass() == TweetSentiments.class) {
				return constructTweetsSentimentProfile(userid, map);
			} else if (obj.getClass() == CulturalAwarenessLng.class) {
				return constructCulturalAwarenessLngProfile(userid, map);
			} else {
				System.err.println("No call found for "
						+ obj.getClass().getName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Constructs user profile in RDF format for CulturalAwarenessLng. We expect
	 * exactly one value in the map
	 * 
	 */
	private static RGLValue constructCulturalAwarenessLngProfile(String userid,
			HashMap<String, Double> map) throws Exception {
		Model model = createEmptyModel();
		Resource user = model.createResource(IMREAL.getURI() + userid);
		user.addProperty(RDF.type, FOAF.Person);

		double awareness = map.get(map.keySet().iterator().next());

		Resource knowledgeResource = model.createResource().addProperty(
				RDF.type, USEM.WeightedKnowledge);

		user.addProperty(USEM.knowledge, knowledgeResource);

		knowledgeResource
				.addProperty(
						WI.topic,
						model.createResource("http://dbpedia.org/resource/Cultural_competence"))
				.addProperty(
						WO.weight,
						model.createResource().addProperty(RDF.type, WO.Weight)
								.addLiteral(WO.weight_value, awareness)
								.addProperty(WO.scale, USEM.DefaultScale));

		return ValueFactory.createGraphValue(model);
	}

	/**
	 * Constructs user profile in RDF format for TweetSentiments
	 * 
	 */
	private static RGLValue constructTweetsSentimentProfile(String userid,
			HashMap<String, Double> map) throws Exception {
		Model model = createEmptyModel();
		Resource user = model.createResource(IMREAL.getURI() + userid);
		user.addProperty(RDF.type, FOAF.Person);

		for (String label : map.keySet()) {
			Resource knowledgeResource = model.createResource().addProperty(
					RDF.type, USEM.WeightedKnowledge);

			user.addProperty(USEM.knowledge, knowledgeResource);

			knowledgeResource
					.addProperty(
							WI.topic,
							model.createResource("http://purl.org/marl/ns#"
									+ label))
					.addProperty(
							WO.weight,
							model.createResource()
									.addProperty(RDF.type, WO.Weight)
									.addLiteral(WO.weight_value, map.get(label))
									.addProperty(WO.scale, USEM.DefaultScale));
		}
		return ValueFactory.createGraphValue(model);
	}

	/**
	 * Constructs user profile in RDF format for TwitterLanguageDetector
	 * 
	 */
	private static RGLValue constructTwitterLanguageDetectorProfile(
			String userid, HashMap<String, Double> map) {
		try {

			System.err.println("constructTwitterLanguageDetectorProfile, size of map: "+map.size());
			Model model = createEmptyModel();
			Resource user = model.createResource(IMREAL.getURI() + userid);
			user.addProperty(RDF.type, FOAF.Person);

			for (String lang : map.keySet()) {

				System.err.println("Trying to match isocode language: "+lang);
				
				Resource knowledgeResource = model.createResource()
						.addProperty(RDF.type, USEM.WeightedKnowledge);

				user.addProperty(USEM.knowledge, knowledgeResource);

				String lngResource = "";
				if(languageMap.containsKey(lang)) {
					lngResource = languageMap.get(lang);
				}
				else {
					try {
						lngResource = getDbpediaLanguage(lang);
						System.err.println("dbpedia based isocode call: "+lang+" => ["+lngResource+"]");
						
						if(lngResource.length()>5)
							languageMap.put(lang, lngResource);
						else
							lngResource="";
						
					}
					catch(Exception e) {
						;
					}
				}
				if(lngResource.equals(""))
					continue;
				
				knowledgeResource.addProperty(WI.topic,
						model.createResource(lngResource))
						.addProperty(
								WO.weight,
								model.createResource()
										.addProperty(RDF.type, WO.Weight)
										.addLiteral(WO.weight_value,
												map.get(lang))
										.addProperty(WO.scale,
												USEM.DefaultScale));
				
				System.err.println("knowledge source: "+knowledgeResource.toString());
			}
			return ValueFactory.createGraphValue(model);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
    /**
     * Executes sparql request to dbpedia in order to get the dbpedia uri for
     * the provided language iso.
     */
    private static String getDbpediaLanguage(String iso) throws Exception {
            String sparqlService = "http://dbpedia.org/sparql";

            String query = "PREFIX dbpprop: <http://dbpedia.org/property/>"
                            + " PREFIX dbo: <http://dbpedia.org/ontology/>"
                            + " select ?language ?isocode" 
                            + " where {"
                            + " ?language dbpprop:iso ?isocode."
                            + " ?language a dbo:Language." 
                            + " FILTER (str(?isocode)=\"" + iso + "\""
                            + " && langMatches(lang(?isocode), \"EN\")"
 //                           + " && regex(?language, \"language\", \"i\")"
                            + " )"
                            + " }";

            System.err.println("getDBPdiaLanguage("+iso+")="+query);
            
            ResultSet results = executeQuery(query, sparqlService);
            
            /*
             * Check DBPedia for the language belonging to the isocode.
             * If there is only one result returned, use that.
             * If there are several languages returned, pick the first with "language" appended to it (if none, pick the first of the list).
             */
            String name = "";
            while(results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                Resource rec = soln.getResource("language");
                if(name.equals(""))
                	name = rec.getURI();
                else if(rec.getURI().endsWith("language") && name.endsWith("language")==false)
                	name = rec.getURI();
            }

            return name;
    }

    /**
     * Executes Jena query
     */
    private static ResultSet executeQuery(String queryString, String service)
                    throws Exception {
            Query query = QueryFactory.create(queryString);

            QueryEngineHTTP qexec = QueryExecutionFactory.createServiceRequest(
                            service, query);
            ResultSet results = qexec.execSelect();
            return results;

    }



	/*
	 * this method is called by the wrapper UserProfileFunction (which in turn
	 * allows workflows to generate U-Sem format profiles from literals)
	 */
	public static RGLValue generateProfile(String userid, String topic,
			String weight) {
		try {
			Model model = createEmptyModel();

			// create the resources
			Resource user = model.createResource(IMREAL.getURI() + userid);
			user.addProperty(RDF.type, FOAF.Person);

			Resource knowledgeResource = model.createResource().addProperty(
					RDF.type, USEM.WeightedKnowledge);

			user.addProperty(USEM.knowledge, knowledgeResource);

			knowledgeResource
					.addProperty(WI.topic, model.createResource(topic))
					.addProperty(
							WO.weight,
							model.createResource()
									.addProperty(RDF.type, WO.Weight)
									.addLiteral(WO.weight_value,
											Double.parseDouble(weight))
									.addProperty(WO.scale, USEM.DefaultScale));
			return ValueFactory.createGraphValue(model);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
