package com.nl.tudelft.rdfgearsUI.server;

import javax.servlet.ServletContext;
import java.io.*;
import java.util.Properties;

public class ConfigurationDataDriver {
	private String basePath = ".";
	private File dataDir;
	Properties rdfgearsProp = new Properties();

	public ConfigurationDataDriver(ServletContext sc, String configFile) {
		// this.basePath = bp;
		// dataDir = new File(basePath + "/data");
		System.out.println("configFile basepath:" + sc.getRealPath("/"));
		if (!readConfigFile(sc, configFile)) {
			System.out
					.println("Cannot read config file or it contain error.. fix it dude..!!");
		}
	}

	public String getBasePath() {
		return basePath;
	}

	public File getDataDir() {
		return dataDir;
	}

	public boolean readConfigFile(ServletContext sc, String filepath) {

		boolean r = true;
		try {

			InputStream is = sc.getResourceAsStream(filepath);
			
			rdfgearsProp.load(is);

			// basePath =
			// d.getElementsByTagName("BasePath").item(0).getTextContent();

			basePath = rdfgearsProp.getProperty("rdfgears.base.path");
			// test the content of the data dir on base path
			File workflowDir = new File(basePath + "/data/workflows");
			if (!workflowDir.exists()) {
				System.out.println("Workflow directory do not exist, "
						+ basePath + "/data/workflows");
				r = false;
			}

			File processorDir = new File(basePath + "/data/processors");
			if (!processorDir.exists()) {
				System.out.println("Processor directory do not exist, "
						+ basePath + "/data/processors");
				r = false;
			}

			File functionsDir = new File(basePath + "/data/functions");
			if (!functionsDir.exists()) {
				System.out.println("Functions directory do not exist, "
						+ basePath + "/data/functions");
				r = false;
			}

		} catch (Exception e) {
			System.out.println("Error while parsing config file, " + filepath);
			e.printStackTrace();
			return false;
		}

		return r;
	}

	public String getConfig(String key) {
		return rdfgearsProp.getProperty(key);
	}

}
