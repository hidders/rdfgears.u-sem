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

import org.w3c.dom.Element;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Vbox;


/**
 * @author Eric Feliksik
 *
 */
public class GearsComparatorNode extends GearsFunctionNode {
	
	private static final long serialVersionUID = 1L;
	private Vbox functionChoosing = new Vbox();
	private Listbox operatorListBox = new Listbox();
	public static int OUTPORT_HEIGHT = 40;
	
	private static final int CHOOSEBOX_WIDTH = 90;
	
	/*
	 * the possible number-comparison operators. 
	 * 
	 * Will they also apply for other values, e.g. strings? 
	 */
	public enum NumberOp {
		OP_LESS          ("<"),
		OP_LESS_EQUAL    ("<="),
		OP_GREATER       (">"),
		OP_GREATER_EQUAL (">="),
		OP_EQUAL         ("=="),
		OP_NOT_EQUAL     ("!=");
		
		private final String code;
		private NumberOp(String code){
			this.code = code;
		}
		public String getCode(){
			return code;
		}
	}
	
	/**
	 * @param x
	 * @param y
	 * @param nParams
	 */
	public GearsComparatorNode(int x, int y) {
		super(x, y);
		functionChoosing.appendChild(operatorListBox);
		wnd.setTitle("Cmp");

		//vbox.appendChild(functionChoosing);
		vbox.insertBefore(functionChoosing, functionHeader);
		
		/**
		 * load the function list 
		 */
    	operatorListBox.setWidth(CHOOSEBOX_WIDTH+"px");
    	operatorListBox.setMold("select");
    	for (NumberOp op : NumberOp.values()){
    		String label = "a "+op.getCode()+ " b";
    		operatorListBox.appendItem(label, op.name());
    	}
    	
    	if (operatorListBox.getSelectedItem()==null)
			operatorListBox.selectItem(operatorListBox.getItemAtIndex(1));
    	
//    	operatorListBox.addEventListener("onSelect", new EventListener(){
//			@Override
//			public void onEvent(Event arg0) throws Exception {
//				String opName = (String) operatorListBox.getSelectedItem().getValue();
//				setOperator(opName);
//			}
//
//    	});
	}
	

	
	private String getOperator(){
		return (String) operatorListBox.getSelectedItem().getValue();
	}
	
	/* select the operator in the listbox */
	private void setOperator(String opName){
		List<Listitem> items = operatorListBox.getItems();
		for (Listitem listItem : items){
			String operatorName = (String) listItem.getValue();
			if (operatorName.equals(opName)){
				operatorListBox.selectItem(listItem);
				return;
			}
		}
	}
	
	public void initialize(){
		addInputName("a");
		addInputName("b");
		super.initialize();
	}
	
	/**
	 * get standard height of this window
	 */
	protected int getBaseHeight() {
		return BASE_HEIGHT + 10; // higher because of function chooser
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
		return super.getYPosForPort(portNr) + 41;	
	}

	/* (non-Javadoc)
	 * @see org.deri.pipes.ui.AbstractGearsNode#getSrcCode(org.w3c.dom.Document, boolean)
	 */
	@Override
	public Element getSrcCode(Element workflowElement, boolean includePosition) {
			/* save the query as a function-config-parameter */
			Element functionElm = workflowElement.getOwnerDocument().createElement("function");
			functionElm.setAttribute("type", getTagName()); // is associated with gearsfunctionchoosernode in PipeNode.java
			Element configElem = workflowElement.getOwnerDocument().createElement("config");
			configElem.setAttribute("param", "operator");
			configElem.setTextContent(getOperator());
			functionElm.appendChild(configElem);
			return createProcessorElement(functionElm, includePosition);
	}


	public static PipeNode loadConfig(Element elm, PipeEditor wsp) {
		
		GearsComparatorNode node = new GearsComparatorNode(
				Integer.parseInt(elm.getAttribute("x")), Integer.parseInt(elm.getAttribute("y"))
			);
		
		wsp.addFigure(node); // calls node.initialize(). But I must do addFigure before I can do other stuff
		
		String operatorName = getXMLFunctionParameter(elm, "operator");
		
		node.setOperator(operatorName);
		
		node.configureIterationMarkers(elm);
		
		return node;
	}
	/* (non-Javadoc)
	 * @see org.deri.pipes.ui.PipeNode#getTagName()
	 */
	@Override
	public String getTagName() {
		return "comparator";
	}

}
