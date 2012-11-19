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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.deri.pipes.core.Engine;
import org.deri.pipes.endpoints.PipeConfig;
import org.deri.pipes.utils.XMLUtil;
import org.integratedmodelling.zk.diagram.components.Port;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;


/**
 * Operator to call one pipe from another.
 * @author robful
 *
 */
public class GearsCallNode extends GearsFunctionNode implements ConnectingOutputNode{
	static Logger logger = LoggerFactory.getLogger(InPipeNode.class);
	//Listbox pipelist;
//	Vbox parameters;

	private String pipeName; // the name of this pipe
	
	public GearsCallNode(int x, int y, String pipeName){
		this(x,y);
		this.pipeName = pipeName;
	}
	
	private GearsCallNode(int x, int y){
		super(x,y);
    	wnd.setTitle("Workflow");
	}

	protected int getBaseWidth(){
		return BASE_WIDTH + 20;
	}
	
	
	protected void initialize(){
		super.initialize();
		setOperationName(pipeName);
		for (String inputName : getInputNameList(pipeName)){
			addInputName(inputName);
		}
	}
	
	public void setSelectedPipe(String pipeid){
		pipeName=pipeid;
	}

	public static List<String> getInputNameList(Element elm){
		List<String> list = new ArrayList<String>();
		Element workflowInputList = XMLUtil.getFirstSubElementByName(elm, "workflowInputList");
		
		for (Element inputPortElem : XMLUtil.getSubElementByName(workflowInputList, "workflowInputPort")){
			list.add(inputPortElem.getAttribute("name"));
		}
		return list;
	}
	
	/* get a list of input names for a pipeName */
	private static List<String> getInputNameList(String pipeId){
		PipeConfig config = Engine.defaultEngine().getPipeStore().getPipe(pipeId);
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document doc=null;
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			ByteArrayInputStream baIStream = new ByteArrayInputStream(config.getConfig().getBytes());
			doc = db.parse(baIStream);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (doc!=null){
			return getInputNameList(doc.getDocumentElement());
		}
		
		assert(false);
		return null;
	}
	

	public Element getSrcCode2(Document doc, boolean config) {
		Element el = doc.createElement(getTagName());
		if(config){
			setPosition(el);
		}
		el.setAttribute("pipeid", this.pipeName);
		return el;
	}


	/* (non-Javadoc)
	 * @see org.deri.pipes.ui.PipeNode#getSrcCode(org.w3c.dom.Document, boolean)
	 */
	@Override	
	public Element getSrcCode(Element workflowElement, boolean includePosition) {
		//throw new RuntimeException("This function is obsoleted by RDF Gears");
		Document doc = workflowElement.getOwnerDocument();
		
		/* save the query as a function-config-parameter */
		Element functionElm = doc.createElement("function");
		functionElm.setAttribute("type", getTagName());
		Element paramElem = doc.createElement("config");
		paramElem.setAttribute("param", "workflow-id");
		paramElem.appendChild(doc.createTextNode(this.pipeName));
		functionElm.appendChild(paramElem);
		return createProcessorElement(functionElm, includePosition); 
	}


	public static PipeNode loadConfig(Element elem, PipeEditor wsp){
		GearsCallNode node = new GearsCallNode(
				Integer.parseInt(elem.getAttribute("x")), 
				Integer.parseInt(elem.getAttribute("y")),
				getXMLFunctionParameter(elem, "workflow-id")
			);
		wsp.addFigure(node); // calls initialize
		return node;
	}	
	
	/* (non-Javadoc)
	 * @see org.deri.pipes.ui.PipeNode#getTagName()
	 */
	@Override
	public String getTagName() {
		return "workflow";
	}


	// copied from AndConditionNode
	public void connectTo(Port port) {
		getWorkspace().connect(output, port, false);
	}
	


}
