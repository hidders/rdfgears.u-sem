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
 * Also for SELECT Queries.
 * 
 */

import java.util.List;

import org.deri.pipes.utils.XMLUtil;
import org.integratedmodelling.zk.diagram.components.Port;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;

public class GearsSparqlNode extends GearsFunctionNode implements ConnectingInputNode, ConnectingOutputNode {
	
	final Logger logger = LoggerFactory.getLogger(GearsSparqlNode.class);
	TextBandBox queryInputBox = null;
	Textbox nameBox;

	private Vbox queryVBox;

	public GearsSparqlNode(int x, int y) {
		this(x, y, 1);
	}

	public GearsSparqlNode(int x, int y, int nrInputs) {
		super(x, y, nrInputs);
		getInputListVbox().setVariableInputs(true);
		getInputListVbox().setEditableInputs(true); 
		getInputListVbox().setMinimumNrOfInputs(0);
		this.setOperationName("sparql query");
		//wnd.setTitle("SPARQL");

		vbox.appendChild(queryVBox = new Vbox());
		queryVBox.appendChild(new Label("")); 
		Hbox queryHBox = new Hbox();
		queryVBox.appendChild(new Label("Query:"));
		queryVBox.appendChild(queryHBox);
		queryHBox.appendChild((new Space()));
		queryHBox.appendChild(queryInputBox = new TextBandBox());
		queryInputBox.setWidth("100px"); 
		queryInputBox.setAutodrop(true);
		queryInputBox.setTooltiptext("Enter your CONSTRUCT query here");

	}


	/**
	 * get standard width of this window
	 * @return
	 */
	@Override
	protected int getBaseHeight(){
		return BASE_HEIGHT + 55;
	}

	/**
	 * get standard width of this window
	 * @return
	 */
	@Override
	protected int getBaseWidth(){
		return 150;
	}

	// copied from AndConditionNode
	public void connectTo(Port port) {
		getWorkspace().connect(output, port, false);
	}
	
	public static PipeNode loadConfig(Element elm, PipeEditor wsp) {
		List<Element> inputPortList = XMLUtil.getSubElementByName(elm, "inputPort");
		
		GearsSparqlNode node = new GearsSparqlNode(
				Integer.parseInt(elm.getAttribute("x")), Integer.parseInt(elm.getAttribute("y")), 
				inputPortList.size()
			);
		
		node.setQuery(getXMLFunctionParameter(elm, "query"));
		
		node.setCreateDefaultPorts(false);
		wsp.addFigure(node); // calls node.initialize(). But I must do addFigure before I can do other stuff
		//node.getInputListVbox().deleteFirstInputPort();
		
		/* connect input sources */
		
 		for(int i=0; i<inputPortList.size(); i++){
 			Element inputPortElem = inputPortList .get(i); 
 			String pname = inputPortElem.getAttribute("name");
 			String iterate = inputPortElem.getAttribute("iterate");
 			
 			NamedInputIface namedInput = node.addInputName(pname); 
 			Port newPort = namedInput.getPort();
 			namedInput.getCheckbox().setChecked("true".equals(iterate)); /* set checkbox */ 
 			
 			Element sourceElem = XMLUtil.getFirstSubElementByName(inputPortElem, "source");
 			if (sourceElem==null){
 				(new RuntimeException("Caught: No source specified for input '"+pname+"' of processor '"+elm.getAttribute("id")+"'")).printStackTrace();
 				continue;
 			}
 		}
		return node;
	}
	
	
	
	/**
	 * Simple getters/setters
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.deri.pipes.ui.PipeNode#getTagName()
	 */
	@Override
	public String getTagName() {
		return "sparql";
	}

	public void setQuery(String query) {
		queryInputBox.setTextboxText(query);
	}

	/* (non-Javadoc)
	 * @see org.deri.pipes.ui.ConnectingInputNode#onConnected(org.integratedmodelling.zk.diagram.components.Port)
	 */
	@Override
	public void onConnected(Port port) {
		System.out.println("Port "+port.getId()+" has been connected");
		
	}

	/* (non-Javadoc)
	 * @see org.deri.pipes.ui.ConnectingInputNode#onDisconnected(org.integratedmodelling.zk.diagram.components.Port)
	 */
	@Override
	public void onDisconnected(Port port) {
		System.out.println("Port "+port.getId()+" has been disconnected");
	}
	
	
	

	/*
	 * @see org.deri.pipes.ui.PipeNode#getSrcCode(org.w3c.dom.Document, boolean)
	 * @Override
	 */ 
	public Element getSrcCode(Element workflowElement, boolean includePosition) {
		/* save the function-config parameters */
		Document doc = workflowElement.getOwnerDocument();
		
		Element functionElm = doc.createElement("function");
		functionElm.setAttribute("type", getTagName());
		
		/* set the bind variables with same name as inputports */
		String bindVarStr = "";
		for (NamedInputIface namedInput : getInputListVbox().getNamedInputList()){
			bindVarStr += namedInput.getInputName() + ";";
		}
		Element bindVarParam = doc.createElement("config");
		bindVarParam.setAttribute("param", "bindVariables");
		bindVarParam.appendChild(doc.createTextNode(bindVarStr));
		functionElm.appendChild(bindVarParam);
		
		
		/* set the query */
		Element queryParam = doc.createElement("config");
		queryParam.setAttribute("param", "query");
		queryParam.appendChild(doc.createCDATASection(queryInputBox.getValue().trim()));
		functionElm.appendChild(queryParam);
		
		return createProcessorElement(functionElm, includePosition); 
	}


	/**
	 * whether or not the VariableInputListVbox should use TextBoxes instead of Lables
	 * @return
	 */
	public boolean editableInputs(){
		return true;
	}

	/**
	 * whether or not the VariableInputListVbox should provide a button to add more inputs 
	 * @return
	 */
	public boolean variableInputs(){
		return true;
	}
	
	

}
