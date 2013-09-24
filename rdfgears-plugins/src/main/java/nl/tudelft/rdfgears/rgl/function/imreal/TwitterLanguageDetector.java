
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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import nl.tudelft.rdfgears.engine.Config;
import nl.tudelft.rdfgears.engine.Engine;
import nl.tudelft.rdfgears.engine.ValueFactory;
import nl.tudelft.rdfgears.rgl.datamodel.type.GraphType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RDFType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RGLType;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.function.SimplyTypedRGLFunction;
import nl.tudelft.rdfgears.util.row.ValueRow;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;

/**
 * A function to detect twitter languages based on a Twitter username.
 * 
 * If as input the UUID is provided as input well, the RDF output gives the UUID handle, otherwise it outputs the Twitter handle.
 * 
 * @author Claudia
 */
public class TwitterLanguageDetector extends SimplyTypedRGLFunction {

	public static final String INPUT_USERNAME = "username";
	public static final String INPUT_UUID = "uuid";
	public static final String INPUT_USEFRIENDSOFUSER = "friendsOfUser";
	
	public static final int MAXHOURS = 48; /* number of hours 'old' data (i.e. tweets retrieved earlier on) are still considered a valid substitute */

	/*
	 * profiles can only be loaded once, otherwise the language library crashes.
	 * Static because multiple instances of this RGLFunctions may exist.
	 */
	public static boolean profilesLoaded = false;

	public TwitterLanguageDetector() {
		this.requireInputType(INPUT_USERNAME, RDFType.getInstance());
		this.requireInputType(INPUT_UUID, RDFType.getInstance());
		this.requireInputType(INPUT_USEFRIENDSOFUSER, RDFType.getInstance());
	}

	public RGLType getOutputType() {
		return GraphType.getInstance();
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
		String username = rdfValue.asLiteral().getValueString().trim();

		RGLValue rdfValue2 = inputRow.get(INPUT_UUID);
		if (!rdfValue2.isLiteral())
			return ValueFactory.createNull("Cannot handle URI input in "
					+ getFullName());
		String uuid = rdfValue2.asLiteral().getValueString();
		
		RGLValue rdfValue3 = inputRow.get(INPUT_USEFRIENDSOFUSER);
		if (!rdfValue3.isLiteral())
			return ValueFactory.createNull("Cannot handle URI input in "
					+ getFullName());
		String useFriends = rdfValue3.asLiteral().getValueString();		
		
		String usernameSplit[] = username.split("\\s+");
		
		HashMap<String, Double> languageMap = new HashMap<String,Double>();
		for(String un : usernameSplit) {
			try 
			{
				HashMap<String,Double> map = detectLanguage(un, useFriends);
				for(String s : map.keySet()) {
					double d = map.get(s);
					if(languageMap.containsKey(s)) {
						d += languageMap.get(s);
					}
					languageMap.put(s, d);
				}
			} catch (Exception e) 
			{
				return ValueFactory.createNull("Error in "
						+ this.getClass().getCanonicalName() + ": "
						+ e.getMessage());
			}
		}
		
		/*
		 * We must now convert the languageMap, that was the result of the
		 * external 'component', to an RGL value.
		 */

		RGLValue userProfile = null;
		try 
		{
			userProfile = UserProfileGenerator.generateProfile(this, (uuid.equals("")==true) ? username : uuid, languageMap);
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
	 * will throw Exception on failure
	 * 
	 * @param twitterUser
	 * @return
	 * @throws LangDetectException
	 * @throws IOException
	 */
	protected HashMap<String, Double> detectLanguage(String twitterUser, String useFriends)
			throws LangDetectException, IOException {
		
		HashMap<String,String> tweets = null;
		
		if(useFriends.equals("true"))
			tweets = TweetCollector.getFriendsTweetTextWithDateAsKey(twitterUser,25,true, MAXHOURS);
		else
			tweets = TweetCollector.getTweetTextWithDateAsKey(twitterUser, true, MAXHOURS);

		/* *************
		 * The dir with the language profiles is read from the conf file.
		 */
		File profileDir = new File(Config.getLanguageProfilePath());
	
		System.err.println("TwitterLanguageDetector: profiles read from "+profileDir);
												 
		if (!profilesLoaded) {
			DetectorFactory.loadProfile(profileDir);
			profilesLoaded = true;
		}

		HashMap<String, Double> languageMap = new HashMap<String, Double>();

		for(String key : tweets.keySet())
		{
			String tweetText = tweets.get(key);

			// language of the tweet
			Detector detect = DetectorFactory.create();
			detect.append(tweetText);
			String lang = null;
			try
			{
				lang = detect.detect();
			}
			catch(Exception e){System.err.println(e.getMessage());}
			
			if (lang!=null && languageMap.containsKey(lang) == true) 
			{
				double val = languageMap.get(lang) + 1;
				languageMap.put(lang, val);
			} 
			else
				languageMap.put(lang, 1.0);
		}
		
		System.err.println("number of detected languages: "+languageMap.size());
		for(String s : languageMap.keySet()) {
			System.err.println("language detection: "+s+" => "+languageMap.get(s));
		}
		
		return languageMap;
	}

}
