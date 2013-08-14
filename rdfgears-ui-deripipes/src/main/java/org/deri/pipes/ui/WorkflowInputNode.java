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

import java.util.Set;

import org.integratedmodelling.zk.diagram.components.Port;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Eric Feliksik
 *
 */
public class WorkflowInputNode extends AbstractGearsProcessorNode {
	private int someInputNr = 1;
	
	public WorkflowInputNode(int x, int y, int nParams) {
		super(x, y, BASE_WIDTH, (int) (BASE_HEIGHT + nParams *  HBOX_HEIGHT));
		setOperationName("workflow input:");
//		setToobar();
//		setWindowTitle("Workflow input");
		
		vbox.appendChild(getInputListVbox());
		getInputListVbox().setVariableInputs(true);
		getInputListVbox().setEditableInputs(true);
		getInputListVbox().setMinimumNrOfInputs(0); // could also be 0, but then the ports don't re-appear when adding... :-s ?
		
		getInputListVbox().setCheckboxes(false);
		getInputListVbox().setPorttype(GearPort.PortType.SOURCE_PORT);
		
		
	}
	
	public WorkflowInputNode(int x, int y) {
		this(x,y,1);
	}
	public int getYPosForPort(int i){
		return super.getYPosForPort(i) - 24; // because we have no window-manager buttons, slightly smaller
	}
	
	protected void initialize() {
		super.initialize();
		if (this.createDefaultPorts){
			while (getInputListVbox().getNamedInputList().size()<getInputListVbox().getMinimumNrOfInputs()){
				/* there are no input ports yet; add default input port */
				//addInputName("input"+(someInputNr++));
				getInputListVbox().addInputName("input"+(someInputNr++));
			}	
		}
		
		/* We don't need this output port, but it's a workaround: 
		 * If it is not there, the processor will be portless when all inputs are removed -- 
		 * and if a Node has ports but these are removed, then newly added ports will not be visible
		 * for some mysterious reason. Seems like a bug in ZK or ZKDiagram. */ 
		output = new Port("input", 5, 5, "img/invisible-port.png");
		addPort(output);
		
		relayout();
	}
	
	public int getXPosForPort(){
		return getBaseWidth();
	}
	protected int getBaseHeight() {
		return BASE_HEIGHT + 10;
	}
	protected int getBaseWidth() {
		return BASE_WIDTH + 10;
	}

	
	/* (non-Javadoc)
	 * @see org.deri.pipes.ui.PipeNode#getSrcCode(org.w3c.dom.Document, boolean)
	 */
	@Override
	public Element getSrcCode(Element workflowElement, boolean includePosition) {
		Document doc = workflowElement.getOwnerDocument();
		Element elem = doc.createElement("workflowInputList"); /* this will be stripped; it is just a container for the children */
		
		for (NamedInputIface namedInput : getInputListVbox().getNamedInputList()){
			Element inputElem = doc.createElement("workflowInputPort");
			inputElem.setAttribute("name", namedInput.getInputName());
			elem.appendChild(inputElem);
		}
		
		if (includePosition)
			setPosition(elem);
		
		return elem;
	}

	/* (non-Javadoc)
	 * @see org.deri.pipes.ui.AbstractGearsNode#addSrcCode(org.w3c.dom.Document, java.util.HashMap, boolean)
	 */
	public void addSrcCode(Element workflowElement , Set<AbstractGearsNode> done, boolean includePosition) {
		Document doc = workflowElement.getOwnerDocument();
		if (done.contains(this))
			return; /* already serialized */
		
		done.add(this); /* administer that this object is serialized */
		
		/* insert it right before the <network> element */ 
		workflowElement.insertBefore(
				getSrcCode(workflowElement, includePosition), 
				workflowElement.getElementsByTagName("network").item(0)
		);
		
	}


	/* (non-Javadoc)
	 * @see org.deri.pipes.ui.PipeNode#connectTo(org.integratedmodelling.zk.diagram.components.Port)
	 */
	@Override
	public void connectTo(Port port) {
		// TODO Auto-generated method stub
		System.out.println(this+".connectTo("+port+")");
		
	}

	/* (non-Javadoc)
	 * @see org.deri.pipes.ui.PipeNode#getTagName()
	 */
	@Override
	public String getTagName() {
		return "gear-input-UNUSED?"; // i believe it is unused in this class
	}

}
