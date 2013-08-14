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

import org.integratedmodelling.zk.diagram.components.Port;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;

/**
 * @author Eric Feliksik
 *
 */
public class GearsSparqlEndpointNode extends GearsFunctionNode implements ConnectingInputNode,ConnectingOutputNode {
	final Logger logger = LoggerFactory.getLogger(SPARQLEndpointNode.class);
	Textbox endpoint;
	Textbox defaulturi;
	TextBandBox queryBox;
	Port endpointPort,defaulturiPort=null;
	
	protected Listbox listbox;
	
	public GearsSparqlEndpointNode(int x,int y){
		//super(x,y,260,100);
		super(x,y);
		//wnd.setTitle("Endpoint");
		//setToobar();
        Vbox vbox=new Vbox();
        
        /* endpoint label */
        Label lbl = new Label("SPARQL endpoint:");
        lbl.setStyle("font-weight: bold");
        vbox.appendChild(lbl);
        
        /* endpoint textbox */
		Hbox hbox= new Hbox();
		hbox.appendChild(new Space());
		hbox.appendChild(endpoint=createBox(140,16));
		vbox.appendChild(hbox);
		
		
		/*
	    hbox= new Hbox();
		hbox.appendChild(new Label("Default-graph-URI:"));
		hbox.appendChild(defaulturi=createBox(120,16));
		vbox.appendChild(hbox);
		*/
		
		 /* endpoint label */
        Label queryLabel = new Label("query:");
        //queryLabel.setStyle("font-weight: bold");
        vbox.appendChild(queryLabel);
        
	    hbox= new Hbox();
		hbox.appendChild(new Space());
		
		/*
		 * Create query input box 
		 */
		String defaultText = "# Note that you must explicitly mention what graph(s) you are querying, e.g. \n";
		defaultText += "PREFIX dbprop: <http://dbpedia.org/property/>\n";
		defaultText += "SELECT ?person, ?date WHERE {\n";
		defaultText += "\tGRAPH <http://dbpedia.org> {\n";
		defaultText += "\t\t?person dbprop:dateOfBirth ?date.\n";
		defaultText += "\t\t?person dbprop:name \"Edsger Wybe Dijkstra\"@en.\n";
		defaultText += "\t}\n";
		defaultText += "}\n";
		
		hbox.appendChild(queryBox = new TextBandBox(defaultText, 10, 60));
		queryBox.setWidth("120px");
		
		vbox.appendChild(hbox);
		wnd.appendChild(vbox);
        	       
	}
	

	/**
	 * get standard height of this window
	 */
	protected int getBaseHeight() {
		return 110;
	}

	/**
	 * get standard width of this window
	 * @return
	 */
	protected int getBaseWidth(){
		return 160;
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

	
	
	public void onConnected(Port port){
		System.out.println("SPARQLEndpointNode connected a port");
	}
	
	public void onDisconnected(Port port){
		System.out.println("SPARQLEndpointNode disconnected a port");
	}
	
	public void setEndpoint(String url){
		endpoint.setValue(url);
	}
			
	public void setDefaultURI(String uri){
		defaulturi.setValue(uri);
	}

	public void setQuery(String query){
		queryBox.setTextboxText(query);
	}

	public String getQuery(){
		return queryBox.getValue().trim();
	}
	
	public String getEndpoint(){
		return endpoint.getValue().trim();
	}
	

	/*
	 * @see org.deri.pipes.ui.PipeNode#getSrcCode(org.w3c.dom.Document, boolean)
	 * @Override
	 */ 
	public Element getSrcCode(Element workflowElement, boolean includePosition) {
		Document doc = workflowElement.getOwnerDocument();
		
		Element functionElm = doc.createElement("function");
		functionElm.setAttribute("type", getTagName());

		/* save the endpoint as a function-config-parameter */
		Element endpointParam = doc.createElement("config");
		endpointParam.setAttribute("param", "endpoint");
		endpointParam.appendChild(doc.createCDATASection(getEndpoint()));
		functionElm.appendChild(endpointParam);
		
		/* save the query as a function-config-parameter */
		Element queryParam = doc.createElement("config");
		queryParam.setAttribute("param", "query");
		queryParam.appendChild(doc.createCDATASection(getQuery()));
		functionElm.appendChild(queryParam);
		
		
		return createProcessorElement(functionElm, includePosition);
	}
	

	public static PipeNode loadConfig(Element elm, PipeEditor wsp) {
		
		GearsSparqlEndpointNode node = new GearsSparqlEndpointNode(
				Integer.parseInt(elm.getAttribute("x")), Integer.parseInt(elm.getAttribute("y")));
		
		node.setQuery(getXMLFunctionParameter(elm, "query"));
		node.setEndpoint(getXMLFunctionParameter(elm, "endpoint"));
		
		wsp.addFigure(node); // calls node.initialize(). But I must do addFigure before I can do other stuff
		
		return node; 
	}
	
	
	public void debug(){
//		((PipeEditor)getWorkspace()).reloadTextDebug(getSrcCode(false)) ;
//		((PipeEditor)getWorkspace()).reloadTabularDebug(null);
		
	}

	/* (non-Javadoc)
	 * @see org.deri.pipes.ui.PipeNode#getTagName()
	 */
	@Override
	public String getTagName() {
		return "sparql-endpoint";
	}

	/* (non-Javadoc)
	 * @see org.deri.pipes.ui.PipeNode#connectTo(org.integratedmodelling.zk.diagram.components.Port)
	 */
	@Override
	public void connectTo(Port port) {
		// TODO Auto-generated method stub
		System.out.println("Endpoint connected to a port");
	}


}