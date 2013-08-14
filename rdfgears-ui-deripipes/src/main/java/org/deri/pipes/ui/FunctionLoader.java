/*
 * Copyright (c) 2008-2009,
 * 
 * Digital Enterprise Research Institute, National University of Ireland, 
 * Galway, Ireland
 * http://www.deri.org/
 * http://pipes.deri.org/
 *
 * Semantic Web Pipes is distributed under New BSD License.
 * 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 *  * Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright 
 *    notice, this list of conditions and the following disclaimer in the 
 *    documentation and/or other materials provided with the distribution and 
 *    reference to the source code.
 *  * The name of Digital Enterprise Research Institute, 
 *    National University of Ireland, Galway, Ireland; 
 *    may not be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.deri.pipes.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

import nl.tudelft.rdfgears.rgl.datamodel.type.RGLType;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.function.RGLFunction;
import nl.tudelft.rdfgears.util.row.TypeRow;
import nl.tudelft.rdfgears.util.row.ValueRow;

import org.deri.pipes.core.Engine;
import org.deri.pipes.endpoints.PipeConfig;
import org.deri.pipes.utils.XMLUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * @author Eric Feliksik
 *
 */
public class FunctionLoader {

	private static Map<String, RGLFunction> map;
	private static List<String> functionList;
	private static List<String> workflowList;
	
	static void init(){ /* initialize classmap */
		/* delete map if it exists */
		map = new HashMap<String, RGLFunction>();
		initFunctionList();
		initWorkflowList();
	}

	/**
	 * 
	 */
	public static void reset() {
		init();
	}
	
	/**
	 * init, if this wasn't already done
	 */
	private static void checkInit(){
		if (map==null){
			init();
		}
	}
	
	static List<String> getFunctionList(){
		checkInit();
		return functionList;
	}
	
	static List<String> getWorkflowList(){
		checkInit();
		return workflowList;
	}
	
	static RGLFunction get(String functionId){
		if (functionId==null){
			throw new IllegalArgumentException("Cannot load functionId null, you must give a non-null argument");
		}
		checkInit();
		RGLFunction func = map.get(functionId);
		if(func==null && functionId.startsWith("workflow")){
			// may have just been put in the filesystem (is that true?). Try to reload. 
			initWorkflowList();
			func = map.get(functionId);
		}
		
		if(func==null){
			throw new RuntimeException("Cannot find RGLFunction '"+functionId+"' ");
		}
		return func;
		
	}
	
	private static void initFunctionList(){
		functionList = new ArrayList<String>();
		ServiceLoader<RGLFunction> loader = ServiceLoader.load(RGLFunction.class);
		for (RGLFunction rglFunc : loader){
			map.put(rglFunc.getFullName(), (RGLFunction) rglFunc);
			functionList.add(rglFunc.getFullName());
		}
	}

	/**
	 * Load the workflow list from the workflows on disk. 
	 */
	private static void initWorkflowList() {
		workflowList = new ArrayList<String>();
		for (PipeConfig config: Engine.defaultEngine().getPipeStore().getPipeList()){
			String workflowId = config.getId();
			/** 
			 * we could load the entire workflows with 
			 * 	Workflow wflow = rdfgears.engine.WorkflowLoader.loadWorkflow(workflowId);
			 * but this recursively loads all subworkflows and instantiates function.
			 * And if some workflow has an error it becomes a mess. Not desired. 
			 * 
			 * Instead just read the input ports from XML and create a dummy function.
			 */
			
			PipeConfig workflowConfig = Engine.defaultEngine().getPipeStore().getPipe(workflowId);
			if(workflowConfig==null){
				throw new RuntimeException("Workflow '"+workflowId+"' cannot be loaded. Does this workflow-id match the path of the workflow XML file? ");
			}
			Node workflowXML = workflowConfig.getWorkflowXML();
			
			ArrayList<String> inputNames = new ArrayList<String>();
			Element workflowInputList = XMLUtil.getFirstSubElementByName((Element) workflowXML, "workflowInputList");
			
			if (workflowInputList != null){
				for (Element workflowInputPortElem : XMLUtil.getSubElementByName(workflowInputList, "workflowInputPort")){
					inputNames.add(workflowInputPortElem.getAttribute("name"));
				}	
			}
			
			RGLFunction workflow = new DummyWorkflowRGLFunction(workflowId, inputNames);
			map.put(workflow.getFullName(), workflow);
			workflowList.add(workflow.getFullName());
		}
		
	}

}

/**
 * A dummy class to contain the required input names for a workflow. 
 * @author Eric Feliksik
 *
 */
class DummyWorkflowRGLFunction extends nl.tudelft.rdfgears.rgl.function.RGLFunction {
	String name ;
	public DummyWorkflowRGLFunction(String name, List<String> requiredInputs){
		this.name = name;
		for (String inputName : requiredInputs){
			this.requireInput(inputName);
		}
	}
	/* (non-Javadoc)
	 * @see rdfgears.rgl.workflow.function.RGLFunction#initialize(java.util.Map)
	 */
	@Override
	public void initialize(Map<String, String> config) {
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see rdfgears.rgl.workflow.function.RGLFunction#execute(rdfgears.util.row.ValueRow)
	 */
	@Override
	public RGLValue execute(ValueRow inputRow) {
		throw new RuntimeException("Dummy function cannot be typechecked");
	}

	/* (non-Javadoc)
	 * @see rdfgears.rgl.workflow.function.RGLFunction#getOutputType(rdfgears.util.row.TypeRow)
	 */
	@Override
	public RGLType getOutputType(TypeRow inputTypes) {
		throw new RuntimeException("Dummy function is not executable");
	}
	
	public String getFullName(){
		return "workflow:"+this.name;
	}
	
	public String getShortName(){
		String[] split = name.split("/");
		String briefName = split[split.length-1]; // last element; 
		if (briefName==""){
			briefName = name + " NAME_ERROR";
		}
		return briefName+" (Workflow "+this.name+")";
	}
	/* (non-Javadoc)
	 * @see rdfgears.rgl.workflow.function.RGLFunction#getRole()
	 */
	@Override
	public String getRole() {
		return "dummy-function"; // maybe we should just say 'workflow' if we use this method
	}

}
