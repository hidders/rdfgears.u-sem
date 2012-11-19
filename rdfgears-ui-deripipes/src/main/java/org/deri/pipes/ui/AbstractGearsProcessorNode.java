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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.deri.pipes.utils.XMLUtil;
import org.integratedmodelling.zk.diagram.components.Port;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Space;
import org.zkoss.zul.Vbox;

/**
 * @author Eric Feliksik
 *
 */
public abstract class AbstractGearsProcessorNode extends AbstractGearsNode {
	private static int nodeIdCounter = 0; // a unique nodeId for every element
	
	private String procId = "node_"+nodeIdCounter++;
	protected Port output;
	
	protected static final int INPUT_NAME_WIDTH = 75;
	protected static final int BASE_HEIGHT = 45;
	protected static final int BASE_WIDTH = INPUT_NAME_WIDTH+35;
	public static int OUTPORT_HEIGHT = 40;
	
	protected static final double HBOX_HEIGHT = 19.4;
	
	public final static String INPUT_TOOLTIP = "Input variable name";
	protected boolean createDefaultPorts = true;
	
	private VariableInputListVbox inputListVbox = new VariableInputListVbox(this); // the vbox containing all named input elements
	
	protected Vbox vbox;
	protected Hbox functionHeader = new Hbox(); // first element, can be referenced by subclass
	protected Label headerLabel = new Label();

	
	/**
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public AbstractGearsProcessorNode(int x, int y, int width, int height) {
		super(x, y, width, height);
		wnd.appendChild(vbox = new Vbox());
		vbox.appendChild(functionHeader);
		functionHeader.appendChild(new Space());
		functionHeader.appendChild(headerLabel);
		headerLabel.setStyle("font-weight: bold");
		headerLabel.setValue("");

		
	}
	public void setWindowTitle(String title){
		//wnd.setTitle(title);
		
		wnd.setTitle(getProcessorId());
	}
	
	public void setOperationName(String name){
		headerLabel.setValue(name);
		headerLabel.setTooltip(name);
	}
	public String getOperationName(){
		return headerLabel.getValue();
	}

	
	public Port getOutputPort(){
		return output;
	}
	
	protected void initialize(){
		
		relayout();
	}
	
	/**
	 * get standard height of this window
	 */
	protected int getBaseHeight() {
		return BASE_HEIGHT;
	}

	/**
	 * get standard width of this window
	 * @return
	 */
	protected int getBaseWidth(){
		return BASE_WIDTH;
	}
	
	protected void setCreateDefaultPorts(boolean bool){
		this.createDefaultPorts = bool;
	}
	

	public String getPortName(Port port){
		Iterator<Port> iter = this.getPorts().iterator();
		for (NamedInputIface namedInput : getInputListVbox().getNamedInputList()){
			if (namedInput.getPort()==port){
				return namedInput.getInputName();
			}
		}
		return null;
	}
	
	
	/** 
	 * Delete the connections with ports of this Node, if the Node is detached. 
	 * This attempts to fix a bug in the ZKDiagram library, as connections sometimes re-spawn... 
	 * But this doesn't make sense, connections of detached ports should be gone for good.  
	 */
	@Override
	public void detach(){
//		System.out.println("DETACHING NODE "+this);
		for (Port p : this.getPorts()){
			
			/* iterate over array copy to prevent ConcurrentModificationException */
			
			Object[] targetPorts = this.getWorkspace().getOutgoingConnections(p.getId()).toArray();
			for (int i=0; i<targetPorts.length; i++){
				this.getWorkspace().notifyConnection(p, (Port) targetPorts[i], null, true); // delete connection
			}
			
			Object[] sourcePorts = this.getWorkspace().getIncomingConnections(p.getId()).toArray();
			for (int i=0; i<sourcePorts.length; i++){
					this.getWorkspace().notifyConnection((Port) sourcePorts[i], p, null, true); // delete connection
			}
			
			//p.detach();
		}
		
		super.detach(); // will also detach child elements (including ports)
	}
	/**
	 * @param nodeSet
	 */
	public void collectDependencies(Set<PipeNode> nodeSet) {
		for (Port thisNodePort : this.getPorts()){
			
			if (thisNodePort instanceof GearPort){
				/* only follow input ports */
				if (((GearPort) thisNodePort).getGearPortType() == GearPort.PortType.TARGET_PORT){
					Collection<Port> incomingConnections = getWorkspace().getIncomingConnections(thisNodePort.getUuid());
					if (incomingConnections.size()>1){
						(new RuntimeException(getId()+": There are "+incomingConnections.size()+" (max is 1) incoming connections for node "+this.getTagName())).printStackTrace();				
					}

					for(Port sourcePort:incomingConnections){
						if (sourcePort.getParent() instanceof AbstractGearsNode){
							((AbstractGearsProcessorNode)sourcePort.getParent()).collectDependencies(nodeSet);						
						}
						else if (sourcePort.getParent() instanceof WorkflowInputNode){
							nodeSet.add((WorkflowInputNode)sourcePort.getParent());
						}
						else {
							throw new RuntimeException(getId()+": Unexpected class "+sourcePort.getParent().getClass().getName());
						}
						 
					}
				}
				else {
					/* do nothing with named/unnamed outputs */
				}
			}
			else {
				// print stacktrace
				(new RuntimeException("Error: skipping port of type "+thisNodePort.getClass().getName())).printStackTrace();
			}
		}
		
		/* add self */
		nodeSet.add(this);
		
	}
		
		
	/**
	 * Get the <processor> XML serialization of this node
	 */
	public abstract Element getSrcCode(Element workflowElement, boolean includePosition);
	
	/**
	 * @param workflowElement - the xml node to which the src must be added
	 * @param done - a set of nodes that was already included in the document
	 * @param includePosition whether to include x,y coordinates of the node-position 
	 */
	public void addSrcCode(Element workflowElement, Set<AbstractGearsNode> done, boolean includePosition) {
		if (done.contains(this))
			return; /* already serialized */
		
		done.add(this); /* administer that this object is serialized */
		
		Element processorElem = getSrcCode(workflowElement, includePosition);
		workflowElement.getElementsByTagName("network").item(0).appendChild(processorElem);
		
		/* 
		 * finally, also let the children add their SrcCode 
		 */
		addChildrenSrcCode(workflowElement, done, includePosition);
	}

	/**
	 * 
	 */
	protected void addChildrenSrcCode(Element workflowElement, Set<AbstractGearsNode> done, boolean config) {
		
		for (Port thisNodePort : this.getPorts()){
			if ( (thisNodePort instanceof GearPort) &&
				(((GearPort) thisNodePort).getGearPortType() == GearPort.PortType.TARGET_PORT) ){ /* only follow input ports */
					Collection<Port> incomingConnections = getWorkspace().getIncomingConnections(thisNodePort.getUuid());
					if (incomingConnections.size()>1){
						(new RuntimeException(getId()+": There are "+incomingConnections.size()+
								" (max is 1) incoming connections for node "+this.getTagName())).printStackTrace();				
					}

					for(Port sourcePort:incomingConnections){
						if (sourcePort.getParent() instanceof AbstractGearsProcessorNode){
							((AbstractGearsProcessorNode)sourcePort.getParent()).addSrcCode(workflowElement, done, config);
						}
						else {
							throw new RuntimeException(getId()+": Unexpected class "+sourcePort.getParent().getClass().getName());
						}
				}
			}
		}
	}

	public static String getXMLFunctionParameter(Element processorElement, String parameterName){
		Element functionElem = XMLUtil.getSubElementByName(processorElement, "function").get(0);
		NodeList childNodes = functionElem.getChildNodes();		
		for (int i=0; i<childNodes.getLength(); i++){
			Node item = childNodes.item(i);
			if (item instanceof org.w3c.dom.Element){
				Element cfgElem = (Element) item; 
				if (parameterName.equals(cfgElem.getAttribute("param"))){
					return cfgElem.getTextContent(); 
				}	
			}
		}
		return null; // no parameter with that name! 
	}
	
	
	public void relayoutInputPorts(int from) {
		List<NamedInputIface> namedInputList = getInputListVbox().getNamedInputList();
		for(int i=from; i< namedInputList.size(); i++){
			NamedInputIface namedInput = namedInputList.get(i);
			GearPort port = namedInput.getPort();
//			System.out.println("relayoutInputPorts: set port position ("+getXPosForPort()+", "+getYPosForPort(i)+")");
			port.setPosition(getXPosForPort(), getYPosForPort(i));
//			System.out.println("Setting port visible");
		}
	}

	
	public VariableInputListVbox getInputListVbox(){
		return inputListVbox;
	}

	public Port getPortWithName(String portName) {
		return getInputListVbox().getPortWithName(portName);
	}
	
	public void setInputListVbox(VariableInputListVbox ivb){
		this.inputListVbox = ivb; 
	}
	
	
	public void relayout() {
		setDimension(getBaseWidth(), (int) (getBaseHeight() + getDynamicHeight() ));
	}
	
	/** can be overridden by subclass if it doesn't use inputListVbox  (e.g. GearCategorizeNode) */
	public double getDynamicHeight(){
		return getInputListVbox().getNamedInputList().size() * HBOX_HEIGHT;
	}

	public int getXPosForPort() {
		return 0; // by default, port is on the left
	}
	
	protected int getYPosForPort(int portNr){
		int yPos = 21; // height of window manager border
		if (getInputListVbox().hasVariableInputs())
			yPos += 31; // offset for the addinput-button
		
		if (! getOperationName().equals("")){
			yPos += 29; // space for the operation name 
			if (getInputListVbox().hasVariableInputs())
				// weird, I don't understand this offset; apparently the two elements together take less space 
				// than the sum of their individuals.
				yPos -= 17; 
			
		}
		yPos += (int) (portNr * HBOX_HEIGHT); // if portNr!=0, add offset for each existing hbox		
		
		return yPos;
	}


	/**
	 * @param namedInput
	 */
	protected void removeInput(NamedInputIface namedInput) {
		getInputListVbox().removeInput(namedInput);
	}
	
	/** 
	 * add a named input. 
	 * 
	 * @param portName
	 * @param fixedName whether this input name can be modified by the user
	 * @return
	 */
	public NamedInputIface addInputName(String portName) {
		return getInputListVbox().addInputName(portName);
	}
	
//	/**
//	 * get a list with all the named inputs for this node. 
//	 * @see org.deri.pipes.ui.AbstractGearsNode#getNamedInputList()
//	 */
//	protected List<NamedInputIface> getNamedInputList() {
//		return namedInputList;
//	}
//	
	protected NamedInputIface getNamedInput(String name){
		for (NamedInputIface namedInput : getInputListVbox().getNamedInputList()){
			if (namedInput.getInputName().equals(name)){
				return namedInput;
			}
		}
		return null;
	}
	
	protected void configureIterationMarkers(Element processorElement){
		/* configure input port iteration markers */
		for(Element inputPortElem : XMLUtil.getSubElementByName(processorElement, "inputPort")){
 			boolean doIterate = "true".equals(inputPortElem.getAttribute("iterate"));
 			if (doIterate){
 				String inputName = inputPortElem.getAttribute("name");
 				NamedInputIface namedInput = getNamedInput(inputName);
 				if (namedInput==null){
 					procId = processorElement.getAttribute("id");
 					throw new RuntimeException("Processor "+procId+" specifies input '"+inputName+"', but it is not required. ");
 				}
 				namedInput.getCheckbox().setChecked(true);
 			}
 		}
		
	}
	
	/**
	 * Create a <processor> tag for the serialization of this processor
	 * 
	 * @param functionElement
	 * @param includePosition
	 */
	public Element createProcessorElement(Element functionElement, boolean includePosition){
		Document doc = functionElement.getOwnerDocument();
		
		/*
		 * create a processor srcCode and add it to functionElem
		 */
		Element processorElem = doc.createElement("processor");
		//rdfGearsElem.setAttribute("type", this.getTagName());
		processorElem.setAttribute("id", getProcessorId());
		
		processorElem.appendChild(functionElement);
		
		if (includePosition)
			setPosition(processorElem);
		
		
		for (NamedInputIface namedInput : getInputListVbox().getNamedInputList() ){
			Element inputPortElm = doc.createElement("inputPort");
			inputPortElm.setAttribute("name", namedInput.getPort().getPortName());
			inputPortElm.setAttribute("iterate", String.valueOf(namedInput.getCheckbox().isChecked()));
			
			/* get the id of the source of this port */
			Collection<Port> incomingConnections = getWorkspace().getIncomingConnections(namedInput.getPort().getUuid());
			if (incomingConnections.size()>1){
				throw new RuntimeException("There are "+incomingConnections.size()+" incoming connections on port "+namedInput.getPort().getPortName() + " of node "+this.getTagName());				
			}
			for(Port port:incomingConnections){ /* this should really only be one port */
				Element inputSourceElm = doc.createElement("source");
				GearPort ngp = (GearPort) port;
				String procName = ngp.getProcessorId();
				if (procName!=null)
					inputSourceElm.setAttribute("processor", ngp.getProcessorId());
				else
					inputSourceElm.setAttribute("workflowInputPort", ngp.getPortName());
				inputPortElm.appendChild(inputSourceElm ); 
			}
			processorElem.appendChild(inputPortElm);
		}
		
		return processorElem;
	}
	
	
	/**
	 * Set the id for this processor, as it will be used in the XML serialization. 
	 * 
	 * Not to be confused with setId(), which is for internals of ZK.  
	 * @return
	 */
	public void setProcessorId(String id){
		procId = id;
		setWindowTitle("ignored"); // set the processor id 
	}
	
	
	
	/**
	 * Get the id for this processor, as it will be used in the XML serialization. 
	 * 
	 * Not to be confused with getId(), which is for internals of ZK.  
	 * @return
	 */
	public String getProcessorId() {
		if (this instanceof WorkflowInputNode){
			return null;
		}
		
		return procId;
	}

}
