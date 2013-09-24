package com.nl.tudelft.rdfgearsUI.server;

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
