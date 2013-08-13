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


/**
 * @author Eric Feliksik, feliksik@gmail.com
 * 
 * RDF Gears forked PipeNodes have slightly different behavior.  
 *
 */
public abstract class AbstractGearsNode extends PipeNode {
//	private static int nodeIdCounter = 0; // a unique nodeId for every element
//	private int nodeId;
//	protected Port output;
//	
//	protected static final int INPUT_NAME_WIDTH = 75;
//	protected static final int BASE_HEIGHT = 45;
//	protected static final int BASE_WIDTH = INPUT_NAME_WIDTH+35;
//	protected static final int HBOX_HEIGHT = 20;
//	
//	
//	public final static String INPUT_TOOLTIP = "Input variable name"; 
//	
//	protected boolean createDefaultPorts = true;
//
	
	public  AbstractGearsNode(int x, int y, int width, int height){
		super(x, y, width, height);
	}
	
//	
//	public Port getOutputPort(){
//		return output;
//	}
//	
//	protected void initialize(){
//		relayout();
//	}
//	
//	/** 
//	 * Relayout this window. That is, move the ports, reconfigure width/height, etc. 
//	 */
//	public abstract void relayout();
//
//	/**
//	 * get standard height of this window
//	 */
//	protected int getBaseHeight() {
//		return BASE_HEIGHT;
//	}
//
//	/**
//	 * get standard width of this window
//	 * @return
//	 */
//	protected int getBaseWidth(){
//		return BASE_WIDTH;
//	}
//	
//	protected void setCreateDefaultPorts(boolean bool){
//		this.createDefaultPorts = bool;
//	}
//	public abstract String getPortName(Port port);
//	
//	
//	/** 
//	 * Delete the connections with ports of this Node, if the Node is detached. 
//	 * This attempts to fix a bug in the ZKDiagram library, as connections sometimes re-spawn... 
//	 * But this doesn't make sense, connections of detached ports should be gone for good.  
//	 */
//	@Override
//	public void detach(){
//		System.out.println("DETACHING NODE "+this);
//		for (Port p : this.getPorts()){
//			
//			/* iterate over array copy to prevent ConcurrentModificationException */
//			
//			Object[] targetPorts = this.getWorkspace().getOutgoingConnections(p.getId()).toArray();
//			for (int i=0; i<targetPorts.length; i++){
//				this.getWorkspace().notifyConnection(p, (Port) targetPorts[i], null, true); // delete connection
//			}
//			
//			Object[] sourcePorts = this.getWorkspace().getIncomingConnections(p.getId()).toArray();
//			for (int i=0; i<sourcePorts.length; i++){
//					this.getWorkspace().notifyConnection((Port) sourcePorts[i], p, null, true); // delete connection
//			}
//			
//			//p.detach();
//		}
//		
//		super.detach(); // will also detach child elements (including ports)
//	}
//	/**
//	 * @param nodeSet
//	 */
//	public void collectDependencies(Set<PipeNode> nodeSet) {
//		for (Port thisNodePort : this.getPorts()){
//			
//			if (thisNodePort instanceof NamedGraphPort){
//				/* only follow input ports */
//				if (((NamedGraphPort) thisNodePort).getGearPortType() == NamedGraphPort.GearPortType.NAMED_INPUT){
//					Collection<Port> incomingConnections = getWorkspace().getIncomingConnections(thisNodePort.getUuid());
//					if (incomingConnections.size()>1){
//						(new RuntimeException(this.getId()+": There are "+incomingConnections.size()+" (max is 1) incoming connections for node "+this.getTagName())).printStackTrace();				
//					}
//
//					for(Port sourcePort:incomingConnections){
//						if (sourcePort.getParent() instanceof AbstractGearsNode){
//							((AbstractGearsNode)sourcePort.getParent()).collectDependencies(nodeSet);						
//						}
//						else if (sourcePort.getParent() instanceof WorkflowInputNode){
//							nodeSet.add((WorkflowInputNode)sourcePort.getParent());
//						}
//						else {
//							throw new RuntimeException(this.getId()+": Unexpected class "+sourcePort.getParent().getClass().getName());
//						}
//						 
//					}
//				}
//				else {
//					/* do nothing with named/unnamed outputs */
//				}
//			}
//			else {
//				// print stacktrace
//				(new RuntimeException("Error: skipping port of type "+thisNodePort.getClass().getName())).printStackTrace();
//			}
//		}
//		
//		/* add self */
//		nodeSet.add(this);
//		
//	}
//		
//		
//	/**
//	 * Get the <processor> XML serialization of this node
//	 */
//	public abstract Element getSrcCode(Document doc, boolean includePosition);
//	
//	/**
//	 * @param doc - the xml document to which the src must be added
//	 * @param done - a set of nodes that was already included in the document
//	 * @param includePosition whether to include x,y coordinates of the node-position 
//	 */
//	public void addSrcCode(Document doc, Set<AbstractGearsNode> done, boolean includePosition) {
//		if (done.contains(this))
//			return; /* already serialized */
//		
//		done.add(this); /* administer that this object is serialized */
//		
//		Element processorElem = getSrcCode(doc, includePosition);
//		doc.getDocumentElement().getElementsByTagName("network").item(0).appendChild(processorElem);
//		
//		/* 
//		 * finally, also let the children add their SrcCode 
//		 */
//		addChildrenSrcCode(doc, done, includePosition);
//	}
//
//	/**
//	 * 
//	 */
//	protected void addChildrenSrcCode(Document doc, Set<AbstractGearsNode> done, boolean config) {
//		
//		for (Port thisNodePort : this.getPorts()){
//			if ( (thisNodePort instanceof NamedGraphPort) &&
//				(((NamedGraphPort) thisNodePort).getGearPortType() == NamedGraphPort.GearPortType.NAMED_INPUT) ){ /* only follow input ports */
//					Collection<Port> incomingConnections = getWorkspace().getIncomingConnections(thisNodePort.getUuid());
//					if (incomingConnections.size()>1){
//						(new RuntimeException(this.getId()+": There are "+incomingConnections.size()+
//								" (max is 1) incoming connections for node "+this.getTagName())).printStackTrace();				
//					}
//
//					for(Port sourcePort:incomingConnections){
//						if (sourcePort.getParent() instanceof AbstractGearsNode){
//							((AbstractGearsNode)sourcePort.getParent()).addSrcCode(doc, done, config);
//						}
//						else {
//							throw new RuntimeException(this.getId()+": Unexpected class "+sourcePort.getParent().getClass().getName());
//						}
//						 
//				}
//			}
//		}
//	}
//
//	public static String getXMLFunctionParameter(Element processorElement, String parameterName){
//		Element functionElem = XMLUtil.getSubElementByName(processorElement, "function").get(0);
//		NodeList childNodes = functionElem.getChildNodes();		
//		for (int i=0; i<childNodes.getLength(); i++){
//			Node item = childNodes.item(i);
//			if (item instanceof org.w3c.dom.Element){
//				Element cfgElem = (Element) item; 
//				if (parameterName.equals(cfgElem.getAttribute("param"))){
//					return cfgElem.getTextContent(); 
//				}	
//			}
//		}
//		return null; // no parameter with that name! 
//	}
//	

	
}
