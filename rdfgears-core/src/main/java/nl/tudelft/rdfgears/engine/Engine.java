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

import java.util.Collections;
import java.util.Set;

import nl.tudelft.rdfgears.rgl.datamodel.type.RGLType;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.exception.WorkflowCheckingException;
import nl.tudelft.rdfgears.rgl.exception.WorkflowLoadingException;
import nl.tudelft.rdfgears.rgl.workflow.Workflow;
import nl.tudelft.rdfgears.util.row.TypeRow;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class Engine {
	private static Model defaultModel;
	static Logger engineLogger = Logger.getLogger("rdfgears.engine");
	
	private static Config config;
	
	private static boolean initialized = false;
	
	private static FileManager fileManager = new FileManager();
	private static ModelManager modelManager = new ModelManager();

	/**
	 * Get the file manager. Used for side effects.    
	 * 
 	 * @deprecated Undocumented, unsupported, unwise. 
 	 * 
	 * @return
	 */
	public static FileManager getFileManager(){
		return fileManager;
	}
	
	/**
	 * Get the model manager. Used for side effects.    
	 * 
 	 * @deprecated Undocumented, unsupported, unwise. 
 	 * 
	 * @return
	 */
	@Deprecated
	public static ModelManager getModelManager(){
		return modelManager;
	}
	
	/**
	 * Close any resources that need to be closed. 
	 */
	public static void close(){
		modelManager.close();
		fileManager.close();
	}


	public static void init(String configFile){
		Config cfg = new Config(configFile);
		init(cfg);
		
	}
	public static void init(Config cfg){
		if (initialized)
			return;
		
		if (cfg==null)
			cfg = new Config(Config.DEFAULT_CONFIG_FILE);
		
		config = cfg;
		engineLogger.setLevel(config.getDebugLevel());
		
		//engineLogger.log(Level.TRACE, "Initialized logger to level {}", Config.LOG_LEVEL);
		engineLogger.info("Initialized logger to level "+config.getDebugLevel());
		
		initialized = true;
		
		
	}
	
	public static final String startupPath = System.getProperties().getProperty("user.dir");
	
	// make this uniform with.or replace by Tomasz' implementation, if possible. 
	static SimpleValueStore simpleValueStore = new SimpleValueStore(); // singleton
	public static SimpleValueStore getSimpleValueStore(){
		return simpleValueStore;
	}
	
	public static synchronized Model getDefaultModel(){
		if (defaultModel==null){
			defaultModel = ModelFactory.createDefaultModel();
		}
		return defaultModel;
	}
	public static Set<String> getEmptyStringSet() {
		return Collections.emptySet();
	}
	
	/**
	 * 
	 * @deprecated Use ValueFactory.createYourValue(), it dispatches to the correct instance internally. 
	 * @return
	 */
	@Deprecated 
	public static ValueFactory getValueFactory(){
		// note that we are NOT returning null, but we are returning the static class ValueFactory!!!
		// how funky is java. 
		return null;   
	}
	
	/**
	 * Return the result type of this workflow. Throw  WorkflowCheckingException if workflow is not well-typed
	 * @param workflow
	 * @return
	 * @throws WorkflowCheckingException
	 */	
	public static RGLType typeCheck(Workflow workflow, TypeRow typerow) throws WorkflowCheckingException {
		if(typerow==null){
			typerow = new TypeRow();
		}
		try {
			return workflow.getOutputType(typerow);
		} catch (WorkflowCheckingException e) {
			
			e.setProcessorAndFunction(null, workflow);
			throw(e);
		}
	}
	
	public static RGLValue executeWorkflow(Workflow workflow){
		assert(initialized);
		
		int nr = workflow.getRequiredInputNames().size();
		if (nr>0){
			throw new RuntimeException("The workflow is not executable, as it has "+nr+" inputs. ");
		}
		engineLogger.info("Executing workflow... ");
		
		return workflow.execute(null); // needs no inputs 
	}
	
	public static RGLValue executeWorkflow(String workflowName) throws WorkflowLoadingException{
		assert(initialized);
		Workflow workflow = WorkflowLoader.loadWorkflow(workflowName);
		return executeWorkflow(workflow);
	}

	public static Logger getLogger(){
		return engineLogger;
	}

	public static Config getConfig(){
		if(!initialized)
			init((Config)null);
		return config;
	}
	
}
