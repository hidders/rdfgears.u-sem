/*
 * Copyright (c) 20010-2011,
 * 
 * Eric Feliksik, feliksik@gmail.com 
 *
 * GUI based on Semantic Web Pipes. 
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

/**
 * @author Eric Feliksik, feliksik@gmail.com
 * 
 */
import nl.tudelft.rdfgears.rgl.function.RGLFunction;

import org.integratedmodelling.zk.diagram.components.Port;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class GearsFunctionNode extends AbstractGearsProcessorNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	final Logger logger = LoggerFactory.getLogger(GearsSparqlNode.class);
	
	private RGLFunction rglFunction = null; 

	public GearsFunctionNode(int x, int y, int nParams) {
		super(x, y, BASE_WIDTH, (int) (BASE_HEIGHT + nParams *  HBOX_HEIGHT));
		setToobar();
		setWindowTitle("NO-TITLE");
		vbox.appendChild(getInputListVbox());
		
	}
	public GearsFunctionNode(int x, int y){
		this(x,y,0);
	}

	/** 
	 * If a function is configured and the function specifies required input names, add those inputs
	 * as non-modifiable inputs. 
	 */
	public void addRequiredInputs(){
		if (getRGLFunction()!=null){
			for (String inputName : getRGLFunction().getRequiredInputNames()){
				addInputName(inputName);
			}
		}
		
	}
	
	
	protected void initialize() {
		super.initialize();
		/* construct output port */
		output = new GearPort(getWorkspace(),GearPort.PortType.SOURCE_PORT);
		output.setPosition("none");
		output.setPortType("custom");
		addPort(output, getBaseWidth(), OUTPORT_HEIGHT);
		
		relayout();
	}


	// copied from AndConditionNode , investigate what it does
	public void connectTo(Port port) {
		getWorkspace().connect(output, port, false);
	}
	

	/**
	 * @return the currentRGLFunction
	 */
	public RGLFunction getRGLFunction() {
		return rglFunction;
	}

	/**
	 * @param currentRGLFunction the currentRGLFunction to set
	 */
	public void setRGLFunction(RGLFunction rglFunction) {
		this.rglFunction = rglFunction;
	}

	

}
