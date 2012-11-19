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
package org.deri.pipes.endpoints;


import java.io.IOException;
import java.io.InputStream;

import org.apache.xerces.parsers.DOMParser;
import org.deri.pipes.utils.XMLUtil;
import org.deri.pipes.utils.XSLTUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.thoughtworks.xstream.annotations.XStreamAlias;
@XStreamAlias("pipeConfig")
public class PipeConfig {
	final transient static Logger logger = LoggerFactory.getLogger(PipeConfig.class);
	private String id = null;
	private String name = null;
	private String syntax = null; // obsoleted by RDF Gears
	private String config=null;
	private String password = null;
	private Node workflowXMLNode;
	
	public PipeConfig(){
		
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id==null?null:id.trim();
	}
	
	/**
	 * 'Name' is a bad name, as it is actually used as description....
	 * @return
	 */

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	// obsoleted by RDF Gears
	public void setSyntax(String syntax) {
		this.syntax = XSLTUtil.toPrettyXml(syntax);
	}
	
	/* requested by index.zul */
	public String getUISyntax(){
		return serialize();
	}
	
	
	
	/**
	 * 
	 * Silly but effective: 
	 * 
	 * This call is obsolete; DERI pipes stored 'config' and 'syntax' as a nested XML file (CDATA in XML). 
	 * But for RDF Gears we don't like this. However many places in the code use this, so just serialize the 
	 * relevant part. 
	 * @return
	 */
	public String getSyntax() {
		return getWorkflowElementSerialization() + "<!-- this was the syntax -->";
	}
	
	/**
	 * See getSyntax()
	 * @return
	 */
	public String getConfig() {
		return getWorkflowElementSerialization()+" <!-- this was the config -->";
	}
	
	/**
	 * Get a human readable serialization of the workflow XML
	 * @return
	 */
	public String getWorkflowElementSerialization() {
		return XSLTUtil.toPrettyXml(XMLUtil.serializeNode(getWorkflowXML().getOwnerDocument(), getWorkflowXML()));
	}

	public void setConfig(String config) {
		this.config = XSLTUtil.toPrettyXml(config);
	}
	
	
	/**
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * This method could be removed and use instead
	 * isPasswordCorrect(String password)
	 * @return
	 */
	public String getPassword(){
		return this.password;
	}
	/**
	 * Whether another password is the same as the 
	 * one for this PipeConfig.
	 */
	public boolean isPasswordCorrect(String password){
		if(isEmptyOrNull(this.password) && isEmptyOrNull(password)){
			return true;
		}
		if(this.password == null){
			return false;
		}
		return this.password.equals(password);
	}

	/**
	 * @param password2
	 * @return
	 */
	private boolean isEmptyOrNull(String s) {
		return s==null || s.trim().length() ==0;
	}
	

	/**
	 * @param in
	 * @return
	 */
	public static PipeConfig deserialize(InputStream in) {
		PipeConfig pipeConfig = null;
		
		InputSource input=new InputSource(in);
		DOMParser parser = new DOMParser();
		try {
			parser.parse(input);
		} catch (SAXException e) {
			throw new RuntimeException("Cannot parse Workflow XML ");
		} catch (IOException e) {
			throw new RuntimeException("Cannot read Workflow XML Stream");
		}
		Element documentElement = parser.getDocument().getDocumentElement();
		
		pipeConfig = new PipeConfig();
		Element workflowElem = XMLUtil.getFirstSubElementByName(documentElement, "workflow");
		pipeConfig.setWorkflowXML(workflowElem);
		
		Element metadata = XMLUtil.getFirstSubElementByName(documentElement, "metadata");
		
		pipeConfig.setName(XMLUtil.getFirstSubElementByName(metadata, "description").getTextContent());
		pipeConfig.setId(XMLUtil.getFirstSubElementByName(metadata, "id").getTextContent());
		pipeConfig.setPassword(XMLUtil.getFirstSubElementByName(metadata, "password").getTextContent());
		
	
		return pipeConfig;
	}

	/**
	 * Serialize the pipeConfig so that it can be saved to disk. 
	 * RDF Gears modification to DeriPipes, goodbye to the XStream approach and the XML-in-XML/CData-nested documents
	 * @return
	 */
	public String serialize() {
		Node rootElem = createFullXML();
		String xmlStr = XMLUtil.serializeNode(rootElem.getOwnerDocument(), rootElem);
		return XSLTUtil.toPrettyXml(xmlStr); 
	}
	
	public Node createFullXML() {
		// TODO Auto-generated method stub
		// doc.appendChild(rootElem );
		Document ownerDoc = getWorkflowXML().getOwnerDocument();
		Element root_rdfgears = ownerDoc.createElement("rdfgears");
		ownerDoc.appendChild(root_rdfgears);
		
		/* create metadata tag */
		Element metadata = ownerDoc.createElement("metadata");
		

		/* create id, name, password fields */ 
		Element name = ownerDoc.createElement("description");
		Element id = ownerDoc.createElement("id");
		Element password = ownerDoc.createElement("password");
		name.appendChild(ownerDoc.createCDATASection(getName()));
		id.appendChild(ownerDoc.createTextNode(getId()));
		password.appendChild(ownerDoc.createTextNode(getPassword()));
		metadata.appendChild(id);
		metadata.appendChild(name);
		metadata.appendChild(password);
		
		/** append two elements to root doc */
		root_rdfgears.appendChild(metadata);
		root_rdfgears.appendChild(getWorkflowXML());
		
		return root_rdfgears;
		
	}

	/**
	 * set the Basic XML node for this pipe. It is the thing generated by the outputNode; 
	 * 
	 * It is not yet added to its OwnerDocument. 
	 * 
	 * Note that it does not yet contain password, name and id. 
	 * @param xmlNode
	 */
	public void setWorkflowXML(Node xmlNode) {
		assert(xmlNode.getNodeName()=="workflow"); // the xmlNode has tag "workflow"
		this.workflowXMLNode = xmlNode;
	}
	
	public Node getWorkflowXML(){
		assert(this.workflowXMLNode!=null);
		return this.workflowXMLNode;
	}

	
}
