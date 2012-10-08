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
import nl.tudelft.rdfgears.rgl.datamodel.type.RDFType;
import nl.tudelft.rdfgears.rgl.function.RGLFunction;
import nl.tudelft.rdfgears.rgl.function.SimplyTypedRGLFunction;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Space;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Hbox;


/**
 * 
 * NOTE: 
 * The GearsFilterNode, GearsCategorizeNode and the GearsFunctionsNode duplicate a lot of code (function dropdown)
 * and should be refactored!!!


 * Largely duplication of GearsFunctionChooserNode. 
 * 
 * @author Eric Feliksik
 *
 */
public class GearsCategorizeNode extends GearsFunctionNode {
	
	private static final long serialVersionUID = 1L;
	private Vbox functionChoosing = new Vbox();
	private Listbox functionListBox = new Listbox();
	private int selectedIndex = 0;
	
	private static final int CHOOSEBOX_WIDTH = 120;
	
	protected VariableTextboxVbox categoryBoxList = new VariableTextboxVbox(this);
	

	/**
	 * @param x
	 * @param y
	 * @param nParams
	 */
	public GearsCategorizeNode(int x, int y) {
		super(x, y);
		functionChoosing.appendChild(functionListBox);
		wnd.setTitle("categorize");
		
		//vbox.appendChild(functionChoosing);
		vbox.insertBefore(functionChoosing, getInputListVbox());
		
		setOperationName("categorizer");
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
    	
    	assert(categoryBoxList!=null);
    	if (categoryBoxList==null)
    		throw new RuntimeException("categoryBoxList==null, AND assertions disabled");
    	
    	Hbox hbox = new Hbox();
    	hbox.appendChild(new Space());
    	hbox.appendChild(categoryBoxList);

    	vbox.appendChild(hbox);
    	
    	categoryBoxList.setInputHeaderText("add category");
	}



	public double getDynamicHeight(){
		
		assert(categoryBoxList!=null);
    	if (categoryBoxList==null)
    		throw new RuntimeException("categoryBoxList==null, AND assertions disabled");
    	
    	double categoryBoxHeight = categoryBoxList.getNumberOfTextboxes() * HBOX_HEIGHT;
    	return super.getDynamicHeight() + categoryBoxHeight + 18 ; // add some constant, this is symptomatic
	}
	
	public void initialize(){
		super.initialize();


    	this.addInputName("bag");
    	
		categoryBoxList.addTextBox(); // first textbox
	}

	/**
	 * @param currentRGLFunction the currentRGLFunction to set
	 */
	public void enableRGLFunction(String funcName) {
		/* load function class and fetch required input names */
		RGLFunction func = FunctionLoader.get(funcName);		
		assert(func!=null) : "loadRGLFunction() should never return null. ";
		setRGLFunction(func);
		
		/* unlike GearsFunctionChooserNode do not remove/add function inputs, but instead keep the single input 'bag' */
		
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
			if (isCategorizeFunction(rglFunc)){
				functionListBox.appendItem(rglFunc.getShortName() + " ("+fullName+")", fullName);
			}
			
		}
		
		for(String fullName : FunctionLoader.getWorkflowList() ){
			RGLFunction rglFunc = FunctionLoader.get(fullName);
			functionListBox.appendItem(rglFunc.getShortName(), fullName);
		}
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
	private boolean isCategorizeFunction(RGLFunction rglFunc) {
		if (rglFunc.getRequiredInputNames().size()!=1)
			return false;
		if (rglFunc instanceof SimplyTypedRGLFunction){
			if (! (((SimplyTypedRGLFunction)rglFunc).getOutputType() instanceof RDFType )){
				return false;
			}
		}
		
		// for complex typed functions and workflows, we don't know whether they are ok; just assume they are
		return true;
	}


	/**
	 * get standard height of this window
	 */
	protected int getBaseHeight() {
		return BASE_HEIGHT + 20; // higher because of function chooser
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
		return super.getYPosForPort(portNr) + 24;	
	}
	
	@Override
	public Element getSrcCode(Element workflowElement, boolean includePosition) {
		Document doc = workflowElement.getOwnerDocument();
		
			/* save the query as a function-config-parameter */
			Element functionElm = doc.createElement("function");
			functionElm.setAttribute("type", getTagName()); // is associated with gearsfunctionchoosernode in PipeNode.java
			Element categorizeFuncElem = doc.createElement("config");
			categorizeFuncElem.setAttribute("param", "categorizeFunction");
			if (getRGLFunction()==null){
				throw new RuntimeException("You must select a function in every Categorizer node");
			}
			categorizeFuncElem.setTextContent(getRGLFunction().getFullName());
			functionElm.appendChild(categorizeFuncElem);
			
			Element categoriesElem = doc.createElement("config");
			categoriesElem.setAttribute("param", "categories");
			
			String categoryListStr = "";
			for (String catStr : categoryBoxList.getStringList()){
				if (catStr.equals("") || catStr.contains(";"))
					throw new RuntimeException("A category name must not be the empty string and must not contain the ';' character. ");
				
				categoryListStr += catStr + ";";
			}
			categoriesElem.setTextContent(categoryListStr);
			functionElm.appendChild(categoriesElem);
			
			
			
			
			
			return createProcessorElement(functionElm, includePosition);
	}
	
	


	public static PipeNode loadConfig(Element elm, PipeEditor wsp) {
		
		GearsCategorizeNode node = new GearsCategorizeNode(
				Integer.parseInt(elm.getAttribute("x")), Integer.parseInt(elm.getAttribute("y"))
			);
		
		wsp.addFigure(node); // calls node.initialize(). But I must do addFigure before I can do other stuff
		
		String funcName = getXMLFunctionParameter(elm, "categorizeFunction");
		if (funcName!=null){
			node.enableRGLFunction(funcName);
		}
		
		node.categoryBoxList.removeAllBoxes();
		
		for (String cat : getXMLFunctionParameter(elm, "categories").split(";")){
			node.categoryBoxList.addTextBox(cat);
		}
			
		
		node.configureIterationMarkers(elm);
		return node;
	}
	/* (non-Javadoc)
	 * @see org.deri.pipes.ui.PipeNode#getTagName()
	 */
	@Override
	public String getTagName() {
		return "bag-categorize";
	}


}
