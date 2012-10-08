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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.xerces.dom.DocumentImpl;
import org.deri.pipes.core.Engine;
import org.deri.pipes.ui.events.DebugListener;
import org.deri.pipes.ui.events.DeleteListener;
import org.deri.pipes.utils.IDTool;
import org.deri.pipes.utils.XMLUtil;
import org.integratedmodelling.zk.diagram.components.CustomPort;
import org.integratedmodelling.zk.diagram.components.Port;
import org.integratedmodelling.zk.diagram.components.PortType;
import org.integratedmodelling.zk.diagram.components.Shape;
import org.integratedmodelling.zk.diagram.components.Workspace;
import org.integratedmodelling.zk.diagram.components.ZKNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;



/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */
public abstract class PipeNode extends ZKNode{  
	final Logger logger = LoggerFactory.getLogger(PipeNode.class);
	
	private static final long serialVersionUID = -1520720934219234911L;
	protected Window wnd=null;

	public PipeNode(int x,int y,int width,int height){
		super(x,y,width,height);
		this.canDelete=false;
		wnd =new Window();
		appendChild(wnd);
		
		this.setFillColor("#FAFAFA"); // light grey window background color
		//this.setStyle("background: #FF0000;");
		//this.setColor("#0000FF");
		//wnd.setStyle("background: #00FF00;");
		
	}

	protected abstract void initialize();
	
	public abstract Node getSrcCode(Element workflowElement, boolean includePosition);
	
//	
//	public Node getSrcCode(Document doc, boolean includePosition){
//		throw new RuntimeException("This function is obsoleted by RDF Gears");
//	}
	
	public abstract void connectTo(Port port);

	public CustomPort createPort(PortType pType,String position){
		CustomPort port=new CustomPort(((PipeEditor)getWorkspace()).getPTManager(),pType);
		port.setPosition(position);
		port.setPortType("custom");
		addPort(port,0,0);
		getWidth();
		return port;
	}

	public CustomPort createPort(PortType pType,int x,int y){
		CustomPort port=new CustomPort(((PipeEditor)getWorkspace()).getPTManager(),pType);
		port.setPosition("none");
		port.setPortType("custom");
		addPort(port,x,y);
		return port;
	}

	public CustomPort createPort(byte pType,String position){
		return createPort(PipePortType.getPType(pType),position);
	}

	public CustomPort createPort(byte pType,int x,int y){
		return createPort(PipePortType.getPType(pType),x,y);
	}

	protected Textbox createBox(int w,int h){
		Textbox box=new Textbox();
		box.setHeight(h+"px");
		box.setWidth(w+"px");
		return box;
	}

	protected Node getConnectedCode(Element workflowElement,Textbox txtBox,Port port,boolean config){
		Document doc = workflowElement.getOwnerDocument();
		
		Collection<Port> incomingConnections = getWorkspace().getIncomingConnections(port.getUuid());
		for(Port p:incomingConnections){
			if(p.getParent() instanceof ConnectingOutputNode){			
				if(config || ((port instanceof SourceOrStringPort)&&!(p.getParent() instanceof ParameterNode))){
					Node node = ((PipeNode)p.getParent()).getSrcCode(workflowElement , config);
					if((!config)&&node.getNodeType()==Document.ELEMENT_NODE){
						if(!"source".equals(((Element)node).getTagName())){
							Element source = doc.createElement("source");
							source.appendChild(node);
							node = source;
						}
					}
					return node;
				}
				else {
					String srcCode = ((PipeNode)p.getParent()).getSrcCode(config);
					return asTextOrCDataNode(doc, srcCode);
				}
			}
		}
		return asTextOrCDataNode(doc,txtBox.getValue().trim());
	}

	private Node asTextOrCDataNode(Document doc, String srcCode) {
		if(srcCode.indexOf('<')>=0|| srcCode.indexOf('\n')>=0){
			return doc.createCDATASection(srcCode);
		}else{
			return doc.createTextNode(srcCode);
		}
	}

	protected String getConnectedCode(Textbox txtBox,Port port){
		Collection<Port> incomingConnections = getWorkspace().getIncomingConnections(port.getUuid());
		for(Port p:incomingConnections)
			if(p.getParent() instanceof ConnectingOutputNode)			
				return (((PipeNode)p.getParent()).getSrcCode(false));
		return (txtBox.getValue().trim());
	}

	protected Node getConnectedCode(Element workflowElement  ,String tagName, Port port,boolean config){
		Document doc = workflowElement.getOwnerDocument();
		Element elm=doc.createElement(tagName);    	
		Collection<Port> incomingConnections = getWorkspace().getIncomingConnections(port.getUuid());
		for(Port p:incomingConnections){
			elm.appendChild(((PipeNode)p.getParent()).getSrcCode(workflowElement,config));
			break;
		}
		return elm;	
	}

	public String generateID(){
		return IDTool.generateRandomID("");
	}

	public void insertInSrcCode(Element parentElm,Port incommingPort,String tagName,boolean config){
		
		Collection<Port> incomingConnections = getWorkspace().getIncomingConnections(incommingPort.getUuid());
		for(Port port:incomingConnections){
			Element outElm=parentElm.getOwnerDocument().createElement(tagName);
			
			XMLUtil.getFirstSubElementByName(parentElm.getOwnerDocument().getDocumentElement(), "workflow");
			
			Element node=(Element)((PipeNode)port.getParent()).getSrcCode(parentElm,config);
			if(node.getParentNode()!=null){
				String refID=generateID();
				node.setAttribute("ID", refID); //TODO: check first does the id attribute already exist?
				outElm.setAttribute("REFID", refID);
			}
			else
				outElm.appendChild(node);
			parentElm.appendChild(outElm);
		}
	}


	@Override
	public void detach() {
		Workspace ws = getWorkspace();
		if(ws == null){
			return;
		}
		Collection<Port> ports = new ArrayList<Port>();
		ports.addAll(getPorts());
		for(Port port : ports){
			List<Port> connectedIn = new ArrayList<Port>();
			connectedIn.addAll(ws.getIncomingConnections(port.getId()));
			for(Port p : connectedIn){
				ws.notifyConnection(port, p, "", true);
				ws.notifyConnection(p, port, "", true);
			}
			ws.getIncomingConnections(port.getId()).clear();
			List<Port> connectedOut = new ArrayList<Port>();
			connectedOut.addAll(ws.getOutgoingConnections(port.getId()));
			for(Port p : connectedOut){
				ws.notifyConnection(port, p, "", true);
				ws.notifyConnection(p, port, "", true);
			}
			ws.getOutgoingConnections(port.getId()).clear();
		}
		super.detach();
	}

	public void setPosition(Element elm){
		elm.setAttribute("x", ""+getX());
		elm.setAttribute("y", ""+getY());
	}

	protected void loadConnectedConfig(Element elm,Port port,Textbox txtbox){	  
		Element linkedElm=XMLUtil.getFirstSubElement(elm);
		String txt;
		if(linkedElm!=null){
			Shape shape = PipeNode.loadConfig(linkedElm,(PipeEditor)getWorkspace());
			if(shape !=null && shape instanceof PipeNode){
				((PipeNode)shape).connectTo(port);
			}else{
				logger.warn("cannot connect shape ["+shape+"] to port "+port+" using "+linkedElm.getNodeName());
			}
		}else if((txt=XMLUtil.getTextData(elm))!=null){
			if(txt.indexOf("${")>=0){				
				ParameterNode paraNode=((PipeEditor)getWorkspace()).getParameter(txt);
				if(paraNode != null){
					paraNode.connectTo(port);
				}else{
					logger.info("Cannot directly connect parameter referenced variable in "+txt);
					txtbox.setValue(txt);
				}
			}
			else{
				txtbox.setValue(txt);
			}
		}
	}

	public void setToobar(){
		Caption caption =new Caption();
		Toolbarbutton delButton= new Toolbarbutton("","img/del-16x16.png");
		delButton.setClass("drag");
		delButton.addEventListener("onClick", new DeleteListener(this));
		Toolbarbutton debugButton= new Toolbarbutton("","img/debug.jpg");
		debugButton.setClass("drag");
		debugButton.addEventListener("onClick", new DebugListener(this));
		wnd.appendChild(caption);
		Toolbarbutton infoButton = new Toolbarbutton("","img/info-16x16.png");
		infoButton.setClass("drag");
		infoButton.setHref(this.getDocumentationLink());
		infoButton.setTarget("_blank");
		infoButton.setTooltip("Click icon to show documentation in a new tab");
		//caption.appendChild(infoButton); // disabled by Eric
		//caption.appendChild(debugButton);
		caption.appendChild(delButton);
	}


	/**
	 * @return
	 */
	public String getDocumentationLink() {
		return "doc/operators.html#"+getTagName();
	}
	
	/*
	 * Get the tag name for this node.
	 */
	public abstract String getTagName();
	
	
	/** 
	 * RDF Gears approach, only serialize just before writing to disk. Don't serialize XML and 
	 * embed in into other XML, like deri pipes
	 */
	public Node getXMLNode(boolean config){
		DocumentImpl doc =new DocumentImpl();
		

		Element workflowElement = doc.createElement("workflow");
		
		
		Node srcCode = getSrcCode(workflowElement,config);
		
		return srcCode;
	}
	
	public String getSrcCode(boolean config){
		DocumentImpl doc = new DocumentImpl();
		
		
		Element workflowElement = doc.createElement("workflow");
		
		Node srcCode = getSrcCode(workflowElement,config);
		return XMLUtil.serializeNode(doc, srcCode);
	}

	
  /* original deripipes implementation */ 
	public static PipeNode loadConfig_original_deripipes(Element elm,PipeEditor wsp){
		//logger.debug(elm.getTagName());
		String elementName = elm.getTagName();
		if(elementName.equalsIgnoreCase("pipe")){    
			List<Element>  paraElms=XMLUtil.getSubElementByName(
					XMLUtil.getFirstSubElementByName(elm, "parameters"),"parameter");
			for(int i=0;i<paraElms.size();i++){
				wsp.addParameter((ParameterNode)PipeNode.loadConfig(paraElms.get(i),wsp));
			}
			return PipeNode.loadConfig(XMLUtil.getFirstSubElementByName(elm, "code"),wsp);
		}

		return null; // was: wsp.createNodeForElement(elm);
	}
	
	/** 
	 * get a list of input names, given a <workflow> XML element 
	 * @param elm
	 * @return
	 */
	public static List<String> getInputNameList(Element elm){
		List<String> list = new ArrayList<String>();
		Element workflowInputList = XMLUtil.getFirstSubElementByName(elm, "workflowInputList");
		
		for (Element inputPortElem : XMLUtil.getSubElementByName(workflowInputList, "workflowInputPort")){
			list.add(inputPortElem.getAttribute("name"));
		}
		return list;
	}
	
	private static PipeNode loadConfig_workflow(Element rootElem, PipeEditor wsp){

		Element workflowInputElem = XMLUtil.getFirstSubElementByName(rootElem, "workflowInputList");
		int inputX=10, inputY=10, outputX=450, outputY=450;
		if (workflowInputElem!=null){
			/* get coordinates of input node */
			try {
				inputX = Integer.parseInt(workflowInputElem.getAttribute("x"));
				inputY = Integer.parseInt(workflowInputElem.getAttribute("y"));
			}
			catch (Exception e)  {
				System.out.println("coordinates for inputNode or OutputNode are not correctly specified");
				e.printStackTrace();	
			}
			
			wsp.createInput(inputX, inputY, getInputNameList(rootElem).size());
			NodeList gearInputList = workflowInputElem.getElementsByTagName("workflowInputPort");
			
			for(int i=0; i<gearInputList.getLength(); i++){
				Element gearInputElem = (Element) gearInputList.item(i);
				wsp.getInput().addInputName(gearInputElem.getAttribute("name"));
			}
		}
		else {
			wsp.createInput(inputX, inputY, 0);
		}
		
		Element outputNodeElem = XMLUtil.getFirstSubElementByName(rootElem, "network");
		outputX = Integer.parseInt(outputNodeElem.getAttribute("x"));
		outputY = Integer.parseInt(outputNodeElem.getAttribute("y"));
		wsp.createOutput(outputX, outputY);
				
		
		
		/* first construct all the processors */
		Element networkElem = XMLUtil.getFirstSubElementByName(rootElem, "network");
		if (networkElem==null)
			throw new RuntimeException("Bad XML input: no <network> element available, I need exactly 1.");
		
		NodeList procList = networkElem.getElementsByTagName("processor");
		
		/* administer the port connections */
		HashMap<Port, Element> portToSource = new HashMap<Port, Element>(); 
		
		HashMap<String, AbstractGearsNode> sourceIdProcMap = new HashMap<String, AbstractGearsNode>(); // for processor-id's to abstractGearsNode objects
		
		for (int i=0; i<procList.getLength(); i++){
			Element procElem = (Element) procList.item(i);
			/* get processor type from function type */
			Element functionElem = XMLUtil.getFirstSubElementByName(procElem, "function");
			String functionType = functionElem.getAttribute("type");
			String procId = procElem.getAttribute("id"); 
			
			AbstractGearsProcessorNode gearsNode = wsp.createNodeForElement(functionType, procElem);	
			assert(gearsNode !=null) : "The PipeNodeFactory cannot create a PipeNode for function type '"+functionType+"'. ";
//			System.out.println("sourceIdProcMap.put("+procId+", "+gearsNode+")");
			sourceIdProcMap.put(procId, gearsNode);
			
			assert(gearsNode!=null);
			VariableInputListVbox inputListVbox = gearsNode.getInputListVbox();
			assert(inputListVbox!=null);
			
			for (NamedInputIface namedInput : inputListVbox.getNamedInputList()){
				/* For the given port, find the source input in the procElement <processor> tag, and link the
				 * port to the source description so that we can later make a connection.  
				 */
				String inputName = namedInput.getInputName();
				Element sourceElem = findSourceElementForInput(inputName, procElem);
				if (sourceElem==null){
					System.out.println("Warning: Processor "+procId+" has input with name '"+inputName+"', but it has no <source> element");
				} else {
					portToSource.put(namedInput.getPort(), sourceElem );	
				}
				
				
			}
			
		}
		
		/* All nodes are created. Set port inputs */
		for (Port targetPort : portToSource.keySet()){
			Element sourceElement = portToSource.get(targetPort);
			
			Port sourcePort;
			String portName = sourceElement.getAttribute("workflowInputPort"); 
			if (portName.length()>0){
				sourcePort = wsp.getInput().getPortWithName(portName);
			} else {
				portName = sourceElement.getAttribute("processor");
				if (portName.equals(""))
					throw new RuntimeException("need an 'workflowInputPort=...' or 'processor=...' attribute for a <source> tag. ");
				sourcePort = ((AbstractGearsProcessorNode)sourceIdProcMap.get(portName)).getOutputPort();
			}
			
			
			wsp.connect(sourcePort, targetPort, false);
		}
		
		// connect output port
		AbstractGearsProcessorNode lastNode = (AbstractGearsProcessorNode) sourceIdProcMap.get(networkElem.getAttribute("output"));
		if (lastNode==null){
			throw new RuntimeException("The network output '"+networkElem.getAttribute("output")+"' was not found. ");
		}
		assert(lastNode.getOutputPort()!=null);
		assert(wsp.getOutput().getInputPort()!=null);
		wsp.connect( lastNode.getOutputPort(), wsp.getOutput().getInputPort(), false);
		
		return null; /* we don't return a pipenode anymore, we have messed up/refactored deripipes */
	}
	
	

	
	/**
	 * @param inputName
	 * @param procElem
	 * @return
	 */
	private static Element findSourceElementForInput(String inputName, Element procElem) {
		assert(procElem.getNodeName()=="processor") : "I expected a processor element ";
		for (Element inputPortElem : XMLUtil.getSubElementByName(procElem, "inputPort")){
			if (inputPortElem.getAttribute("name").equals(inputName)){
				return XMLUtil.getFirstSubElementByName(inputPortElem, "source");
			}			
		}
		LoggerFactory.getLogger(PatchGeneratorNode.class).error("The processor "+procElem.getAttribute("id")+" should have an input named '"+inputName+"', but there is no such <inputPort> tag in the XML with that 'name' attribute. ");
		
		return null;
	}

	public static PipeNode loadConfig(Element elem, PipeEditor wsp){
		
		String tagName = elem.getTagName();
		
		if ("workflow".equals(tagName)){
			return loadConfig_workflow(elem, wsp);
		} else if ("processor".equals(tagName)) {

			String funcType = XMLUtil.getFirstSubElementByName(elem, "function").getAttribute("type");
			throw new RuntimeException("ERROR: the PipeNode subclass for function "+funcType+" has no loadConfig() implementation");
		} else {
			System.out.println("WARNING: loadConfig on element with tagName "+elem.getTagName()+" is impossible");
		}
		
		return null;
		/*
		for (int i=0; i<procList.getLength(); i++){
			Element procElem = (Element) procList.item(i);
		}
		
		
		
		if(elementName.equalsIgnoreCase("pipe") || elementName.equalsIgnoreCase("gear")){    
			List<Element>  paraElms=XMLUtil.getSubElementByName(
					XMLUtil.getFirstSubElementByName(elm, "parameters"),"parameter");
			for(int i=0;i<paraElms.size();i++){
				wsp.addParameter((ParameterNode)PipeNode.loadConfig(paraElms.get(i),wsp));
			}
			// return PipeNode.loadConfig(XMLUtil.getFirstSubElementByName(elm, "code"),wsp);
			
			
		}
		return wsp.createNodeForElement(elm);
		*/
	}

	public byte getTypeIdx(){
		return PipePortType.NONE;
	}

	public void debug(){
		
		(new RuntimeException("Pipe debugging disabled by Eric")).printStackTrace();
//		((PipeEditor)getWorkspace()).hotDebug(getSrcCode(false));
	}

	protected void connectChildElement(Element elm, String childTagName, Port port) {
		List<Element> childNodes=XMLUtil.getSubElementByName(elm, childTagName);
 		for(int i=0;i<childNodes.size();i++){		
 			PipeNode nextNode=PipeNode.loadConfig(XMLUtil.getFirstSubElement(childNodes.get(i)),(PipeEditor)getWorkspace());
 			nextNode.connectTo(port);
 		}
	}

	
}
