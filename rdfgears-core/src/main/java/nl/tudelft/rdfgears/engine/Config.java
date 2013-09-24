package nl.tudelft.rdfgears.engine;

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

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Level;

/**
 * This configuration system is a bit chaotic and may be adapted to use a better
 * mechanism.
 * 
 * This idea is to not make the properties map available, as we like the
 * properties to be referenced statically (i.e. not by means of a string key),
 * to make them more easy to find throughout the code.
 * 
 * @author Eric Feliksik
 * 
 */
public class Config {

	public static final String DEFAULT_DB_PATH = "./bdb";
	public static final String DEFAULT_WORKFLOW_PATH = "workflows/";
	public static final String DEFAULT_CONFIG_FILE = "../rdfgears.config";
	public static final String DEFAULT_WORKFILES_PATH = "files_generated/";
	
	
	private static final Level DEFAULT_LOG_LEVEL = Level.INFO;
	private static final int DEFAULT_SPARQL_RETRY_MAX = 3;
	private static final long DEFAULT_SPARQL_RETRY_PAUSE = 1000; // milliseconds

	private static final String STRING_TRUE = "true";
	private static final String STRING_FALSE = "false";
	public static final String DEFAULT_RGL_SERIALIZATION_FORMAT = "xml";

	private boolean disable_pipelining = false;
	private boolean disable_laziness = false;

	private static String workflowPath;

	// private File rootFolder = null;

	private static Properties configMap = new Properties();

	public int getRemoteSparqlConstructBatchSize() {
		try {
			return Integer.parseInt(configMap.getProperty(
					"remote.sparql.construct.batchsize", "throw parse error"));
		} catch (NumberFormatException e) {
			return 2000; // do not batch
		}
	}

	public int getRemoteSparqlSelectBatchSize() {
		try {
			return Integer.parseInt(configMap.getProperty(
					"remote.sparql.select.batchsize", "throw parse error"));
		} catch (NumberFormatException e) {
			return 2000; // do not batch
		}
	}

	/**
	 * reinitialize the configuration
	 */
	public Config(String fileName) {

		if (fileName != null) {
			try {
				configMap.load(getStream(fileName));
			} catch (Exception e) {
				URL dir = this.getClass().getClassLoader().getResource(".");
				if (dir != null) {
					Engine.getLogger().debug(
							"Cannot open configuration file '" + fileName
									+ "' for reading in dir " + dir.getPath()
									+ ". Trying Current Working Dir.");
				} else {
					Engine.getLogger()
							.debug("Cannot open configuration file '"
									+ fileName
									+ "' as there is no ClassLoader directory accessible. Trying Current Working Dir.");
				}

				/*
				 * try loading it from CWD, if it is not the same location as
				 * where this class is stored. Necessary for the junit test :-(
				 */
				try {
					configMap.load(new FileInputStream(fileName)); // may not
																	// work in
																	// .jar
																	// files,
																	// etc
					String dirName = System.getProperty("user.dir");
					Engine.getLogger().debug(
							"Ok, loaded config file " + dirName + "/"
									+ fileName);
				} catch (Exception e2) {
					Engine.getLogger().debug(
							"Cannot open configuration file '" + fileName
									+ "' for reading in dir "
									+ System.getProperty("user.dir"));
				}
			}
		}
		initConfig();

	}

	/**
	 * Get a stream of the file, assuming it can be found next to the current
	 * class location. Useful when dealing with JAR files, where the current
	 * working directory has nothing to do with the place where the config is
	 * stored.
	 * 
	 * @param fileName
	 * @return
	 */
	private InputStream getStream(String fileName) {
		InputStream resourceAsStream = this.getClass().getClassLoader()
				.getResourceAsStream(fileName);

		// no exception

		return resourceAsStream;
	}

	private void initConfig() {
		configurePath(configMap.getProperty("workflow.path",
				DEFAULT_WORKFLOW_PATH));

		/* pipelining enabled by default */
		disable_pipelining = isTrue(configMap.getProperty("disable.pipelining",
				STRING_FALSE));
		disable_laziness = isTrue(configMap.getProperty("disable.laziness",
				STRING_FALSE));

	}

	/**
	 * Disable lazy loading, load EVERYTHING when executing remote SPARQL query,
	 * to find SPARQL endpoint errors ASAP
	 */
	public boolean do_greedyLoadingOfRemoteQueries() {
		return isTrue(configMap.getProperty("greedy.loading.of.remote.queries",
				STRING_TRUE));
	}

	/**
	 * Do pipelining
	 */
	public boolean pipeliningDisabled() {
		return disable_pipelining;
	}

	/**
	 * Disable lazy loading, load EVERYTHING when executing remote SPARQL query,
	 * to find SPARQL endpoint errors ASAP
	 */
	public boolean lazinessDisabled() {
		return disable_laziness;
	}

	public void configurePath(String workflowPathString) {
		if (workflowPathString != null) {
			workflowPath = workflowPathString;
		} else {
			workflowPath = DEFAULT_WORKFLOW_PATH;
		}
	}

	public Level getDebugLevel() {
		String logLevelStr = configMap.getProperty("log.level");
		return Level.toLevel(logLevelStr, DEFAULT_LOG_LEVEL);
	}

	public int getSparqlRetryMax() {
		String intStr = configMap.getProperty("sparql.retry.max");
		if (intStr == null)
			return DEFAULT_SPARQL_RETRY_MAX;

		try {
			return Integer.parseInt(intStr);
		} catch (NumberFormatException e) {
			return DEFAULT_SPARQL_RETRY_MAX;
		}
	}

	public long getSparqlRetryPause() {
		String longStr = configMap.getProperty("sparql.retry.pause");
		if (longStr == null)
			return DEFAULT_SPARQL_RETRY_PAUSE;

		try {
			return Long.parseLong(longStr);
		} catch (NumberFormatException e) {
			return DEFAULT_SPARQL_RETRY_PAUSE;
		}
	}

	public void setDebugLevel(String level) {
		configMap.put("log.level", level);
	}

	public void setDiskBased() {
		configMap.put("is.disk.based", Boolean.toString(true));
	}

	/**
	 * Return true iff the given string should be considered 'true'
	 * 
	 * @param str
	 * @return
	 */
	private static boolean isTrue(String str) {
		return STRING_TRUE.equals(str.toLowerCase());
	}

	/**
	 * Return true iff the given string should be considered 'false'
	 * 
	 * @param str
	 * @return
	 */
	private static boolean isFalse(String str) {
		return STRING_FALSE.equals(str.toLowerCase());
	}

	public boolean isDiskBased() {
		return Boolean.parseBoolean(configMap.getProperty("is.disk.based"));
	}

	/* function removed as it introduces a dependency to a temp dir
	 * which is not controlled by the config file
	 * 
	 public static String getWritableDir() {
		String path = System.getProperty("java.io.tmpdir") + "/rdfgears/";
		File dir = new File(path);
		dir.mkdirs();
		return path;
	}
	*/

	public static String getFlickrApiKey() {

		return configMap.getProperty("flickr.api.key");
	}

	public static String getDatabase() {

		return configMap.getProperty("database");
	}

	public static String getDatabaseUser() {

		return configMap.getProperty("dbuser");
	}

	public static String getDatabasePwd() {

		return configMap.getProperty("dbpassword");
	}

	public static String getLexiconPath() {

		return configMap.getProperty("rdfgears.base.path") + configMap.getProperty("lexicon.path");
	}

	public static String getLanguageProfilePath() {

		return configMap.getProperty("rdfgears.base.path") + configMap.getProperty("language.profile.path");
	}
	
	public static String getBasePath() {

		return configMap.getProperty("rdfgears.base.path");
	}

	public static String getFlickrDataPath() {

		return configMap.getProperty("rdfgears.base.path") + configMap.getProperty("flickr.data.path");
	}

	public static String getHofstedePath() {

		return configMap.getProperty("rdfgears.base.path") + configMap.getProperty("hofstede.file.path");
	}

	public static String getRegionPath() {

		return configMap.getProperty("rdfgears.base.path") + configMap.getProperty("region.file.path");
	}
	
	public static String getTwitter4jPath() {

		return configMap.getProperty("rdfgears.base.path") + configMap.getProperty("twitter4j.file.path");
	}

	public static String getStoragePath() {

		return configMap.getProperty("rdfgears.base.path") + configMap.getProperty("value.storage.path");
	}

	public static String getTwitterPath() {

		return configMap.getProperty("rdfgears.base.path") + configMap.getProperty("twitter.data.path");
	}
	public static String getWorkflowPath() {
		return configMap.getProperty("rdfgears.base.path") + workflowPath;
	}
	
	public String getPathToWorkFiles() {
		return configMap.getProperty("rdfgears.base.path") + 
				configMap.getProperty("work.files.path", DEFAULT_WORKFILES_PATH);
	}

	public static String getOAuthConsumerKey() {
		
		return configMap.getProperty("twitter.OAuth.consumer.key");
	}

	public static String getOAuthConsumerSecret() {
	
		return configMap.getProperty("twitter.OAuth.consumer.secret");
	}

	public static String getOAuthAccessToken() {

		return configMap.getProperty("twitter.OAuth.access.token");
	}

	public static String getOAuthAccessTokenSecret() {
		
		return configMap.getProperty("twitter.OAuth.access.secret");
	}
	
	
}
