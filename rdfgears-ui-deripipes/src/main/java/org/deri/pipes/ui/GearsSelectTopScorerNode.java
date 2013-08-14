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

import java.util.List;

import nl.tudelft.rdfgears.rgl.datamodel.type.BooleanType;
import nl.tudelft.rdfgears.rgl.function.RGLFunction;
import nl.tudelft.rdfgears.rgl.function.SimplyTypedRGLFunction;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Vbox;


/**
 * 
 * Largely duplication of GearsFunctionChooserNode. 
 * 
 * 
 * NOTE: 
 * The GearsFilterNode, GearsCategorizeNode and the GearsFunctionsNode duplicate a lot of code (function dropdown)
 * and should be refactored!!!
 * 
 * @author Eric Feliksik
 *
 */
public class GearsSelectTopScorerNode extends AbstractGearsFilterNode {
	
	/**
	 * @param x
	 * @param y
	 * @param nParams
	 */
	public GearsSelectTopScorerNode(int x, int y) {
		super(x, y);
		wnd.setTitle("filter");
		setOperationName("top-score");

	}
	
	public void initialize(){
		super.initialize();
    	//this.getInputListVbox().addInputName("bag");
    	this.addInputName("bag");
	}
	

	/**
	 * Return true iff a function qualifies as selection function.
	 * 
	 * Criteria are: 
	 * 1. it takes exactly one argument, name doesn't matter
	 * 2. it returns a boolean. 
	 * 
	 * Unfortunately we cannot check the return value (boolean) for non-simply typed values...
	 * 
	 * This may be fixed by modifying the typechecking system. 
	 * 
	 * @param rglFunc
	 * @return
	 */
	protected boolean functionIsAppropriateForThisFunctionNode(RGLFunction rglFunc) {
		if (rglFunc.getRequiredInputNames().size()!=1)
			return false;
		if (rglFunc instanceof SimplyTypedRGLFunction){
			if (! (((SimplyTypedRGLFunction)rglFunc).getOutputType() instanceof BooleanType )){
				return false;
			}
		}
		
		// for complex typed functions and workflows, we don't know whether they are ok; just assume they are 
		
		return true;
	}

	
	@Override
	public Element getSrcCode(Element workflowElement, boolean includePosition) {
		Document doc = workflowElement.getOwnerDocument();
		
			/* save the query as a function-config-parameter */
			Element functionElm = doc.createElement("function");
			functionElm.setAttribute("type", getTagName()); // is associated with gearsfunctionchoosernode in PipeNode.java
			Element configElem = doc.createElement("config");
			configElem.setAttribute("param", "scoringFunction");
			if (getRGLFunction()==null){
				throw new RuntimeException("You must select a function in every "+getTagName()+" node");
			}
			
			configElem.setTextContent(getRGLFunction().getFullName());
			functionElm.appendChild(configElem);
			return createProcessorElement(functionElm, includePosition);
	}
	
	


	public static PipeNode loadConfig(Element elm, PipeEditor wsp) {
		
		GearsSelectTopScorerNode node = new GearsSelectTopScorerNode(
				Integer.parseInt(elm.getAttribute("x")), Integer.parseInt(elm.getAttribute("y"))
			);
		
		wsp.addFigure(node); // calls node.initialize(). But I must do addFigure before I can do other stuff
		
		String funcName = getXMLFunctionParameter(elm, "scoringFunction");		
		node.enableRGLFunction(funcName);
		node.configureIterationMarkers(elm);
		return node;
	}
	
	/* (non-Javadoc)
	 * @see org.deri.pipes.ui.PipeNode#getTagName()
	 */
	@Override
	public String getTagName() {
		return "select-top-scorer";
	}


}
