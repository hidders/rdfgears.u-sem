package nl.tudelft.rdfgears.rgl.function.imreal;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import nl.tudelft.rdfgears.engine.Config;
import nl.tudelft.rdfgears.engine.Engine;
import nl.tudelft.rdfgears.engine.ValueFactory;
import nl.tudelft.rdfgears.rgl.datamodel.type.BagType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RDFType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RGLType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RecordType;
import nl.tudelft.rdfgears.rgl.datamodel.value.LiteralValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.impl.ModifiableRecord;
import nl.tudelft.rdfgears.rgl.datamodel.value.impl.bags.ListBackedBagValue;
import nl.tudelft.rdfgears.rgl.function.SimplyTypedRGLFunction;
import nl.tudelft.rdfgears.util.row.FieldIndexMap;
import nl.tudelft.rdfgears.util.row.FieldIndexMapFactory;
import nl.tudelft.rdfgears.util.row.TypeRow;
import nl.tudelft.rdfgears.util.row.ValueRow;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;



/**
 * A function to detect twitter languages based on a twitter username  
 *
 */
public class TwitterLanguageDetector extends SimplyTypedRGLFunction  {
	
	public static final String INPUT_USERNAME = "username";
	public static final String FIELD_LANG = "language";
	public static final String FIELD_SCORE = "score";
	
	
	/* profiles can only be loaded once, otherwise the language library crashes. 
	 * Static because multiple instances of this RGLFunctions may exist. */
	public static boolean profilesLoaded = false; 
	
	
	/* the records we will create contain two fields */
	private static final FieldIndexMap fiMap = FieldIndexMapFactory.create(FIELD_LANG, FIELD_SCORE);
	
	/* cache the results because we don't want to do too much requests */ 
	private static final Map<String, RGLValue> cachedUserProfiles = new HashMap<String, RGLValue>(); 
	private static final Map<String, Integer> cacheTimes = new HashMap<String, Integer>(); 
	
	public TwitterLanguageDetector(){
		this.requireInputType(INPUT_USERNAME, RDFType.getInstance()); 
		 
	}

	public RGLType getOutputType() {
		/* this function returns a type Bag(Record(< language: RDFValue, score: RDFValue >)) */
		TypeRow typeRow = new TypeRow();
		typeRow.put(FIELD_LANG, RDFType.getInstance());
		typeRow.put(FIELD_SCORE, RDFType.getInstance());
		return BagType.getInstance(RecordType.getInstance(typeRow));
	}
	
	@Override
	public RGLValue simpleExecute(ValueRow inputRow) {
		/* - typechecking guarantees it is an RDFType
		 * - simpleExecute guarantees it is non-null
		 * SanityCheck: we must still check whether it is URI or String, 
		 * because typechecking doesn't distinguish this!  
		 */
		RGLValue rdfValue = inputRow.get(INPUT_USERNAME);
		if (! rdfValue.isLiteral())
			return ValueFactory.createNull("Cannot handle URI input in "+getFullName());
		
		// we are happy, value can be safely cast with .asLiteral(). 
		String username = rdfValue.asLiteral().getValueString();
		
		
		/* see if cache it outdated */ 
		boolean useCachedProfile = false;
		RGLValue userProfile = cachedUserProfiles.get(username); 
		if (userProfile != null){
			userProfile = cachedUserProfiles.get(username);
			int cacheTime = cacheTimes.get(username).intValue();
			int nowTime = getCurrentTimestamp();
			useCachedProfile = (nowTime-cacheTime) < 3600;
		}
		
		
		
		if (! useCachedProfile  ){

			HashMap<String, Integer> languageMap;
			try {
				languageMap = detectLanguage(username);
			} catch (Exception e) {
				return ValueFactory.createNull("Error in "+this.getClass().getCanonicalName()+": "+e.getMessage());
			}
			
			/* We must now convert the languageMap, that was the result of the external 
			 * 'component', to an RGL value.*/
			
			
			/* Create a list of RGL records.  
			   could use an ArrayList<RGLValue> as well, but this is recommended
			   because optimizations can the be exploited */
			List<RGLValue> list = ValueFactory.createBagBackingList(); 
			
			/* now create an RGL Record like 
			 * 		[language: "en", score: "1.0"^^xsd:double ] 
			 * for every entry in the language map. */
			
			for (String language : languageMap.keySet()){
				/* instantiate a record */ 
		    	ModifiableRecord rec = ValueFactory.createModifiableRecordValue(fiMap);
		    	
		    	/* We MUST set all fiels of the fieldIndexMap fiMap.
		    	 * Otherwise RDF gears will crash.
		    	 *  
		    	 * If no value is available, do 
		    	 * 		rec.put(FIELDNAME, ValueFactory.createNull("sorry, no value")); 
		    	 */
		    	
		    	// an RDF literal with string representation of the language, without an RDF language tag
		    	LiteralValue lang_literal = ValueFactory.createLiteralPlain(language, null); 
		    	rec.put(FIELD_LANG, lang_literal);
		    	LiteralValue score_literal = ValueFactory.createLiteralDouble(languageMap.get(language).doubleValue());
		    	rec.put(FIELD_SCORE, score_literal);
		    	
		    	list.add(rec); // add record to the list 
		    }
			
			/* create a Bag that is backed by the list. This approach is simple, 
			 * but it does not support pipelining.  
			 */
			userProfile = new ListBackedBagValue(list);
			
			/* store in result & time in cache */ 
			cachedUserProfiles.put(username,  userProfile);
			cacheTimes.put(username, getCurrentTimestamp()); 
		}
		
		return userProfile; 
	}
	
	/* get unix timestamp (seconds) */ 
	private int getCurrentTimestamp() {
		return (int) (System.currentTimeMillis() / 1000L);
	}

	/**
	 * will throw Exception on failure
	 * @param twitterUser
	 * @return
	 * @throws LangDetectException 
	 * @throws IOException 
	 */
	private HashMap<String, Integer> detectLanguage(String twitterUser) throws LangDetectException, IOException{
		
		String getTweetsURL = "https://api.twitter.com/1/statuses/user_timeline.xml?include_entities=false&include_rts=true&screen_name="+twitterUser+"&count=200";
		
		/* ************* 
		 * The dir with the language profiles is assumed to be stored in the tmpdir. 
		 * As it is read-only, it may be nicer to package it in the jar instead.... 
		 * But the jar directory contents are not easily listed by the langdetect tool.  
		 */
		File profileDir = new File(Config.getWritableDir()+"/imreal-language-profiles"); /* should be cross-platform and work in webapps */
		if (!profilesLoaded){
			DetectorFactory.loadProfile(profileDir);
			profilesLoaded = true;
		}
		
		URL url = new URL(getTweetsURL);	
		Engine.getLogger().debug("Attempting to retrieve "+url.toString());
		
		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

		String inputLine;

		//TODO: xml parser
		
		HashMap<String,Integer> languageMap = new HashMap<String,Integer>();

		while ((inputLine = in.readLine()) != null)
		{
		   	if(inputLine.contains("<text>"))
		   	{
		   		String tweet = inputLine;
		   		tweet = tweet.replace("<tweet>", "");
		   		tweet = tweet.replace("</text>", "");
		   	
			   	//language of the tweet
			    Detector detect = DetectorFactory.create();
		        detect.append(inputLine);
		        String lang = detect.detect();
		        
		        if(languageMap.containsKey(lang)==true)
		        {
		        	int val = languageMap.get(lang)+1;
		        	languageMap.put(lang,val);
		        }
		        else
		        	languageMap.put(lang,1);
		   	}
		}

		in.close();
		
		return languageMap;
			
	}
	

}
