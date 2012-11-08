package nl.tudelft.rdfgears.rgl.function.imreal;

import java.io.BufferedReader;
import java.io.File;

import java.io.*;
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
 * A class that either retrieves tweets from file or from the Twitter stream
 * (ideally not file based but DB based). The filename is the twitter user name.
 * 
 * if includeRetweets==true, retweets are included, otherwise they are ignored
 * 
 * maxHoursAllowedOld indicates how old in hours the stored data is allowed to
 * be before it is overwritten.
 * 
 */
public class TweetCollector {

	private static final String TWITTER_DATA_FOLDER = "../temp/rdfgears/twitterData"; /*
																	 * path to
																	 * the
																	 * folder
																	 * where the
																	 * twitter
																	 * data is
																	 * stored
																	 */

	public static HashMap<String, String> getTweetTextWithDateAsKey(
			String twitterUsername, boolean includeRetweets,
			int maxHoursAllowedOld) {
		try {
			
			//creates the folder if it does not exist
			new File(TWITTER_DATA_FOLDER).mkdir();
			
			File f = new File(TWITTER_DATA_FOLDER + "/" + twitterUsername);
			int hours = -1;
			if (f.exists() == true) {
				long lastModified = f.lastModified();

				long diff = System.currentTimeMillis() - lastModified;

				int seconds = (int) (diff / 1000L);
				int minutes = seconds / 60;
				hours = minutes / 60;
			}

			/*
			 * if we do not have data yet (or it is too old), retrieve it and
			 * store it in a folder
			 */
			if (hours > maxHoursAllowedOld || hours < 0) {
				String getTweetsURL = "https://api.twitter.com/1/statuses/user_timeline.xml?include_entities=false&include_rts=true&screen_name="
						+ twitterUsername + "&count=200";

				BufferedWriter bw = new BufferedWriter(new FileWriter(
						TWITTER_DATA_FOLDER + "/" + twitterUsername));
				URL url = new URL(getTweetsURL);
				Engine.getLogger().debug(
						"In TweetCollector, retrieving tweets for "
								+ url.toString());

				BufferedReader in = new BufferedReader(new InputStreamReader(
						url.openStream()));

				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					bw.write(inputLine);
					bw.newLine();
				}
				in.close();
				bw.close();
			}

			return getTweetTextWithDateAsKeyFromFile(f, includeRetweets);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new HashMap<String, String>();
	}

	/*
	 * method returns up to the last 200 tweets, ignoring RTs key: created_at
	 * value: tweet text
	 */
	private static HashMap<String, String> getTweetTextWithDateAsKeyFromFile(
			File f, boolean includeRetweets) {

		String CREATED_AT = "<created_at>";
		String CREATED_AT2 = "</created_at>";

		String TWEET_TEXT = "<text>";
		String TWEET_TEXT2 = "</text>";

		HashMap<String, String> tweetMap = new HashMap<String, String>();

		try {
			BufferedReader in = new BufferedReader(new FileReader(f));

			String inputLine;

			// TODO: xml parser

			String currentDate = null;

			while ((inputLine = in.readLine()) != null) {
				if (inputLine.contains(CREATED_AT)) {
					currentDate = inputLine
							.substring(inputLine.indexOf(CREATED_AT)
									+ CREATED_AT.length(),
									inputLine.indexOf(CREATED_AT2));

					String[] tokens = currentDate.split("\\s+");
					int year = 0;
					int month = 0;
					int day = 0;
					for (int i = 0; i < tokens.length; i++) {
						if (month == 0 && tokens[i].length() == 3)// month or
																	// day
						{
							int monthInt = MONTH.getMonthNumber(tokens[i]);
							if (monthInt > 0) {
								month = monthInt;

								// next token is the day
								day = Integer.parseInt(tokens[i + 1]);
							}
						} else if (tokens[i].length() == 4)// year?
						{
							try {
								year = Integer.parseInt(tokens[i]);
								if (year < 2006 || year > 2013)
									year = 0;
							} catch (Exception e) {
								year = 0;
							}// not-a-number exception
						}
					}
					currentDate = year + "-" + month + "-" + day;
				} else if (inputLine.contains(TWEET_TEXT)) {
					String tweet = inputLine
							.substring(inputLine.indexOf(TWEET_TEXT)
									+ TWEET_TEXT.length(),
									inputLine.indexOf(TWEET_TEXT2));
					if (tweet.startsWith("RT") == false
							|| includeRetweets == true) {
						if (tweetMap.containsKey(currentDate)) {
							String value = tweetMap.get(currentDate) + " "
									+ tweet;// TODO: very inefficient
							tweetMap.put(currentDate, value);
						} else
							tweetMap.put(currentDate, tweet);
					}
				}
			}

			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return tweetMap;
	}

}
