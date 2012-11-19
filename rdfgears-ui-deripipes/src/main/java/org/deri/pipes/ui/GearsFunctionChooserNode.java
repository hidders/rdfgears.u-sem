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

import nl.tudelft.rdfgears.rgl.function.RGLFunction;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Vbox;

/**
 * 
 * 
 *  NOTE: 
 * The GearsFilterNode, GearsCategorizeNode and the GearsFunctionsNode duplicate a lot of code (function dropdown)
 * and should be refactored!!!
 * 
 * 
 * @author Eric Feliksik
 *
 */
public class GearsFunctionChooserNode extends GearsFunctionNode {
	
	private static final long serialVersionUID = 1L;
	private Vbox functionChoosing = new Vbox();
	private Listbox functionListBox = new Listbox();
	private int selectedIndex = 0;
	
	private static final int CHOOSEBOX_WIDTH = 120;
	

	/**
	 * @param x
	 * @param y
	 * @param nParams
	 */
	public GearsFunctionChooserNode(int x, int y) {
		super(x, y);
		this.setOperationName("function:");
		functionChoosing.appendChild(functionListBox);
		//wnd.setTitle("function");
		//vbox.appendChild(functionChoosing);
		vbox.insertBefore(functionChoosing, getInputListVbox());
		
		/**
		 * load the function list 
		 */
    	functionListBox.setWidth(CHOOSEBOX_WIDTH+"px");
    	functionListBox.setMold("select");
    	functionListBox.appendItem("Choose...", "<noclass: First choose entry>");
    	loadFunctionList();
    	
    	functionListBox.addEventListener("onSelect", new EventListener(){
			@Override
			public void onEvent(Event arg0) throws Exception {
				if (functionListBox.getSelectedIndex()==0){
					functionListBox.setSelectedIndex(selectedIndex); // undo the selection, as we cannot select the first one. 
				}
				selectedIndex = functionListBox.getSelectedIndex();
				String funcName = (String) functionListBox.getSelectedItem().getValue();
				
				enableRGLFunction(funcName);
			}

    	});
	}
	

	/**
	 * @param currentRGLFunction the currentRGLFunction to set
	 */
	public void enableRGLFunction(String funcName) {
		/* load function class and fetch required input names */
		RGLFunction func = FunctionLoader.get(funcName);		
		assert(func!=null) : "loadRGLFunction() should never return null. ";
		getInputListVbox().removeAllInputs();
		super.setRGLFunction(func);
		
		/* add the inputs for this functions */
		for (String inputName : getRGLFunction().getRequiredInputNames()){
			addInputName(inputName);
		}
		
		ensureFunctionIsSelected(funcName);
	}
	
	/* select the listitem for rglFunction */
	private void ensureFunctionIsSelected(String funcClassName){
		List<Listitem> items = functionListBox.getItems();
		for (Listitem listItem : items){
			String itemClassName = (String)listItem.getValue();
			
			if (itemClassName.equals(funcClassName)){
				functionListBox.selectItem(listItem);
				return;
			}
		}
	}

	private void loadFunctionList(){
		FunctionLoader.init();
		for(String fullName : FunctionLoader.getFunctionList()){
			RGLFunction rglFunc = FunctionLoader.get(fullName);
			functionListBox.appendItem(rglFunc.getShortName() + " ("+fullName+")", fullName);
		}
		
		for(String fullName : FunctionLoader.getWorkflowList() ){
			RGLFunction rglFunc = FunctionLoader.get(fullName);
			functionListBox.appendItem(rglFunc.getShortName(), fullName);
		}
	}

	
	public void initialize(){
		super.initialize();
	}
	
	/**
	 * get standard height of this window
	 */
	protected int getBaseHeight() {
		return BASE_HEIGHT + 28; // higher because of function chooser
	}

	/**
	 * get standard width of this window
	 * @return
	 */
	protected int getBaseWidth(){
		return CHOOSEBOX_WIDTH + 10 ; 
	}
	

	/* use slightly lower Y base because we have a functionbox above */
	protected int getYPosForPort(int portNr){
		return super.getYPosForPort(portNr) + 25;	
	}
	
	/* (non-Javadoc)
	 * @see org.deri.pipes.ui.AbstractGearsNode#getSrcCode(org.w3c.dom.Document, boolean)
	 */
	@Override
	public Element getSrcCode(Element workflowElement, boolean includePosition) {
		Document doc = workflowElement.getOwnerDocument();
		
			/* save the query as a function-config-parameter */
			Element functionElm = doc.createElement("function");
			functionElm.setAttribute("type", getTagName()); // is associated with gearsfunctionchoosernode in PipeNode.java
			Element configElem = doc.createElement("config");
			configElem.setAttribute("param", "implementation");
			if (getRGLFunction()==null){
				throw new RuntimeException("You must select a function in every Function node");
			}
			configElem.setTextContent(getRGLFunction().getFullName());
			functionElm.appendChild(configElem);
			return createProcessorElement(functionElm, includePosition);
	}


	public static PipeNode loadConfig(Element elm, PipeEditor wsp) {
		
		GearsFunctionChooserNode node = new GearsFunctionChooserNode(
				Integer.parseInt(elm.getAttribute("x")), Integer.parseInt(elm.getAttribute("y"))
			);
		
		wsp.addFigure(node); // calls node.initialize(). But I must do addFigure before I can do other stuff
		
		String funcName = getXMLFunctionParameter(elm, "implementation");
		
		node.enableRGLFunction(funcName);
		
		node.configureIterationMarkers(elm);
		
		return node;
	}
	/* (non-Javadoc)
	 * @see org.deri.pipes.ui.PipeNode#getTagName()
	 */
	@Override
	public String getTagName() {
		return "custom-java";
	}


}
