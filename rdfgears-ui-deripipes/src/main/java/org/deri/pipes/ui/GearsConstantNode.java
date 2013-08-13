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
import org.w3c.dom.Element;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;

/**
 * @author Eric Feliksik
 *
 */
public class GearsConstantNode extends AbstractGearsProcessorNode {
	
	
	private static final int BWIDTH = BASE_WIDTH + 20; 
	private static final int BHEIGHT = 55; 
	private Label constantLabel = new Label();
	private String constantValue = ""; 
	public GearsConstantNode(int x, int y) {
		this(x,y, "\"apple\"@en");
	}
	public GearsConstantNode(int x, int y, String initialValue) {
		
		super(x, y, BWIDTH, BHEIGHT  );
		setToobar();
		setWindowTitle("Constant");
		//setOperationName("constant value");
		
		Vbox helpVbox = new Vbox(); // help text
		helpVbox.appendChild(new Label("Hit <tab> to complete input. Examples:"));
		helpVbox.appendChild(new Label("   <http://dbpedia.org/resource/Delft>"));
		helpVbox.appendChild(new Label("   \"John Doe\""));
		helpVbox.appendChild(new Label("\t\"pomme\"@fr"));
		helpVbox.appendChild(new Label("\t\"1.234\"^^<http://www.w3.org/2001/XMLSchema#double>"));
		
		TextBandBox inputBox = new TextBandBox("", 1, 60, helpVbox);
		inputBox.setReadonly(true);
		inputBox.setWidth("0px");
		inputBox.setTooltiptext("provide a constant value");
		
		inputBox.addEventListenerToTextbox("onChange", new org.zkoss.zk.ui.event.EventListener(){
			public void onEvent(Event event) throws Exception {
				if (event.getTarget() instanceof Textbox){
					setConstantValue(((Textbox)event.getTarget()).getRawText());
				}
				else { 
					throw new RuntimeException("Unexpected event from target "+event.getTarget());
				}
			}
		});
		
		Hbox inputLineBox = new Hbox();
		Space space = new Space();
		space.setWidth("5px"); // doesn't work
		inputLineBox.appendChild(space); // small margin on left
		inputLineBox.appendChild(inputBox);
		inputLineBox.appendChild(new Space());
		inputLineBox.appendChild(constantLabel);
		
		/** initialize textbox values */
		inputBox.setText(initialValue); 
		inputBox.setTextboxText(initialValue); 
		setConstantValue(initialValue);  
		
		vbox.appendChild(inputLineBox);
		inputLineBox.setWidth((getBaseWidth()-100)+"px");
		constantLabel.setMaxlength(30);
	}
	
	public void setConstantValue(String val){
		constantValue = val;
		/* replace html chars */
		String htmlEscaped = val;
		htmlEscaped = htmlEscaped.replaceAll(">", "&gt;");
		htmlEscaped = htmlEscaped.replaceAll("<", "&lt;");
		constantLabel.setValue(htmlEscaped);
		constantLabel.setTooltip(val);
	}
	public String getConstantValue(){
		return constantValue; 
	}

	public int getBaseWidth(){
		return BWIDTH;
	}

	public int getBaseHeight(){
		return BHEIGHT ;
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

	

	/* (non-Javadoc)
	 * @see org.deri.pipes.ui.AbstractGearsProcessorNode#getSrcCode(org.w3c.dom.Document, boolean)
	 */
	@Override
	public Element getSrcCode(Element workflowElement, boolean includePosition) {
		/* save the query as a function-config-parameter */
		Element functionElm = workflowElement.getOwnerDocument().createElement("function");
		functionElm.setAttribute("type", getTagName());
		Element paramElem = workflowElement.getOwnerDocument().createElement("config");
		paramElem.setAttribute("param", "value");
		paramElem.appendChild(workflowElement.getOwnerDocument().createCDATASection(getConstantValue()));
		functionElm.appendChild(paramElem);
		
		return createProcessorElement(functionElm, includePosition); 
	}
	
	public static PipeNode loadConfig(Element elem, PipeEditor wsp){
		GearsConstantNode node = new GearsConstantNode(
				Integer.parseInt(elem.getAttribute("x")), 
				Integer.parseInt(elem.getAttribute("y")),
				getXMLFunctionParameter(elem, "value")
			);
		wsp.addFigure(node); // calls initialize
		return node;
	}	

	/* (non-Javadoc)
	 * @see org.deri.pipes.ui.PipeNode#connectTo(org.integratedmodelling.zk.diagram.components.Port)
	 */
	@Override
	public void connectTo(Port port) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.deri.pipes.ui.PipeNode#getTagName()
	 */
	@Override
	public String getTagName() {
		return "constant";
	}

}
