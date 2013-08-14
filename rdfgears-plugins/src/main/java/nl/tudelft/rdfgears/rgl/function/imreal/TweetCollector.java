package nl.tudelft.rdfgears.rgl.function.imreal;

import java.io.BufferedReader;
import java.io.File;

import nl.tudelft.rdfgears.engine.Config;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import nl.tudelft.rdfgears.engine.Engine;

import org.w3c.dom.*;

import twitter4j.IDs;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * A class that either retrieves tweets from file or from the Twitter stream
 * (ideally not file based but DB based). The filename is the twitter user name.
 * 
 * if includeRetweets==true, retweets are included, otherwise they are ignored
 * 
 * maxHoursAllowedOld indicates how old in hours the stored data is allowed to
 * be before it is overwritten.
 * 
 * @author Claudia
 * 
 */
public class TweetCollector 
{
	
	private static final String TWITTER_DATA_FOLDER = Config.getTwitterPath();
	private static DocumentBuilder docBuilder;
	private static Twitter twitter4j;

	
	static
	{
		try
		{
			docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			ConfigurationBuilder cb = new ConfigurationBuilder();

			cb.setDebugEnabled(true)
			  .setOAuthConsumerKey(Config.getOAuthConsumerKey())
			  .setOAuthConsumerSecret(Config.getOAuthConsumerSecret())
			  .setOAuthAccessToken(Config.getOAuthAccessToken())
			  .setOAuthAccessTokenSecret(Config.getOAuthAccessTokenSecret());

			TwitterFactory tf = new TwitterFactory(cb.build());
			twitter4j = tf.getInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static HashMap<String, String> getTweetTextWithDateAsKey(
			String twitterUsername, boolean includeRetweets,
			int maxHoursAllowedOld) {
		HashMap<String, String> tweetMap = new HashMap<String, String>();

		try {
			File twitterDataFolder = new File(TWITTER_DATA_FOLDER);
			if (!twitterDataFolder.exists())
				twitterDataFolder.mkdirs();

			File f = new File(TWITTER_DATA_FOLDER + "/" + twitterUsername);
			int hours = -1;
			if (f.exists() == true) {
				long lastModified = f.lastModified();

				long diff = System.currentTimeMillis() - lastModified;

				int seconds = (int) (diff / 1000L);
				int minutes = seconds / 60;
				hours = minutes / 60;

				// if the file is nearly empty, retrieve it again anyway
				if (f.length() < 100) {
					System.err
							.println("(Nearly) empty file found, retrieving again ....");
					hours = -1;
				}
			}

			/*
			 * if we do not have data yet (or it is too old), retrieve it and
			 * store it in a folder
			 */
			if (hours > maxHoursAllowedOld || hours < 0) {
				Engine.getLogger().debug(
						"In TweetCollector, retrieving live tweets, storing to "
								+ f.toString());
				ResponseList<Status> tweetList = twitter4j.getUserTimeline(
						twitterUsername, new Paging(1, 200));

				BufferedWriter bw = new BufferedWriter(new FileWriter(
						f.toString()));
				for (Status s : tweetList) {
					if (tweetMap.size() < 5) {
						Engine.getLogger().debug(
								"status string: " + s.toString());
					}
					tweetMap.put(s.getCreatedAt().toString(), s.getText());
					bw.write(s.getCreatedAt() + "\t" + s.getText());
					bw.newLine();
				}
				bw.close();
			} else {
				System.err
						.println("In TweetCollector, reading tweets saved in "
								+ f.toString());
				BufferedReader br = new BufferedReader(new FileReader(
						f.toString()));
				String line;
				while ((line = br.readLine()) != null) {
					int delim = line.indexOf('\t');
					if (delim > 0) {
						tweetMap.put(line.substring(0, delim),
								line.substring(delim + 1));
					} else {
						System.err
								.println("In TweetCollector, no tab delimiter found in line: "
										+ line);
					}
				}
			}
			return tweetMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tweetMap;
	}

	/*
	 * retrieve the list of friends for the given user. randomly pick a number
	 * of them and check their tweets.
	 */
	public static HashMap<String, String> getFriendsTweetTextWithDateAsKey(
			String twitterUsername, int sampleFriends, boolean includeRetweets,
			int maxHoursAllowedOld) {
		
		System.err.println("Retrieving tweets of friends of "+twitterUsername);
		
		HashMap<String, String> tweetMap = new HashMap<String, String>();

		try {
			File twitterDataFolder = new File(TWITTER_DATA_FOLDER);
			if (!twitterDataFolder.exists())
				twitterDataFolder.mkdirs();

			File f = new File(TWITTER_DATA_FOLDER + "/FRIENDS_OF_"
					+ twitterUsername);
			int hours = -1;
			
			if (f.exists() == true) {
				long lastModified = f.lastModified();

				long diff = System.currentTimeMillis() - lastModified;

				int seconds = (int) (diff / 1000L);
				int minutes = seconds / 60;
				hours = minutes / 60;

				// if the file is nearly empty, retrieve it again anyway
				if (f.length() < 100) {
					System.err
							.println("(Nearly) empty file found, retrieving again ....");
					hours = -1;
				}
			}

			/*
			 * if we do not have data yet (or it is too old), retrieve it and
			 * store it in a folder
			 */
			if (hours > maxHoursAllowedOld || hours < 0) {

				BufferedWriter bw = new BufferedWriter(new FileWriter(
						f.toString()));

				// get the first 5000 friends
				IDs friends = twitter4j.getFriendsIDs(twitterUsername, -1);
				List<Long> friendIDs = new ArrayList<Long>();
				System.err.println("Number of friends on first cursor page: "+friends.getIDs().length);
				
				for (int i = 0; i < friends.getIDs().length; i++) {
					friendIDs.add(friends.getIDs()[i]);
				}
				// randomize the list
				Collections.shuffle(friendIDs);

				for (int i = 0; i < sampleFriends && i < friendIDs.size(); i++) {

					System.err.println("Sampling from user "+friendIDs.get(i));
					
					ResponseList<Status> tweetList = twitter4j.getUserTimeline(
							friendIDs.get(i), new Paging(1, 200));

					for (Status s : tweetList) {
						tweetMap.put(s.getCreatedAt().toString(), s.getText());
						bw.write(s.getCreatedAt() + "\t" + s.getText());
						bw.newLine();
					}
				}
				bw.close();
			} else {
				System.err
						.println("In TweetCollector, reading tweets saved in "
								+ f.toString());
				BufferedReader br = new BufferedReader(new FileReader(
						f.toString()));
				String line;
				while ((line = br.readLine()) != null) {
					int delim = line.indexOf('\t');
					if (delim > 0) {
						tweetMap.put(line.substring(0, delim),
								line.substring(delim + 1));
					} else {
						System.err
								.println("In TweetCollector, no tab delimiter found in line: "
										+ line);
					}
				}
			}
			return tweetMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tweetMap;
	}
}
