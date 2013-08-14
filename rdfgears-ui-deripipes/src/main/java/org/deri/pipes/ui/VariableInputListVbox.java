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
import java.util.List;

import org.integratedmodelling.zk.diagram.components.Port;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Space;
import org.zkoss.zul.Vbox;

/**
 * @author Eric Feliksik
 *
 */
public class VariableInputListVbox extends Vbox {
	
	private AbstractGearsProcessorNode node;
	private int minimumNrOfInputs = 1;
	private boolean editableInputs = false;
	private boolean checkboxes = true;
	private boolean variableInputs = false;
	private Hbox inputHeader;
	private Vbox inputFieldList = new Vbox();
	private int varCount = 0;
	private GearPort.PortType portType = GearPort.PortType.TARGET_PORT;
	
	private int namedInputLoc; /* location of the NamedGraphInput element in a Hbox; set when adding the namedGraphInput */

	public static final String ADD_ICON = "img/edit_add-48x48.png";
	public static final String REMOVE_ICON = "img/edit_remove-48x48.png";

	/* a list of all the named inputs for this node (actually redundant, they're also 
	 * hidden in the hboxes in inputFieldList */
	private List<NamedInputIface> namedInputList = new ArrayList<NamedInputIface>();
	
	public VariableInputListVbox(AbstractGearsProcessorNode node){
		this.node = node;
		appendChild(inputFieldList);
		manageButtonVisibility();
	}

	/**
	 * get a list with all the named inputs for this node. 
	 * @see org.deri.pipes.ui.AbstractGearsNode#getNamedInputList()
	 */
	protected List<NamedInputIface> getNamedInputList() {
		return namedInputList;
	}
	
	public String getUnusedInputName(String prefix){
		boolean used = true;
		String name = prefix + varCount;
		while (used){
			name = prefix + varCount++;
			used = false; // not found yet 
			for (NamedInputIface namedInput: getNamedInputList()){
				if (namedInput.getInputName().equals(name)){
					used = true;
					break;
				}
			}
		}
		return name;
	}
	
	public NamedInputIface addInputName(String portName) {
		/* add an input element and iteration-marker for input with the given name */
		Hbox hbox = new Hbox();
		NamedInputIface namedInput;
		if (editableInputs) {
			namedInput = new NamedInputTextBox(portName, node.getWorkspace(), getPortType());
			namedInput.setWidth(getTextboxWidth()+"px");
		} else {
			namedInput = new NamedInputLabel(portName, node.getWorkspace(), getPortType());
		}
		namedInput.setTooltiptext("Name of input variable");
		//hbox.setPack("center");
		hbox.setAlign("center");
		if (hasCheckboxes()){
			hbox.appendChild(namedInput.getCheckbox());	
		} else { 
			hbox.appendChild(new Space());
		}
		
		namedInputLoc = hbox.getChildren().size(); // remember the position of the namedInput (actually only necessary the first time )
		hbox.appendChild(namedInput);
		
//		System.out.println("Adding input port ");
//		System.out.println(namedInput.getPort() +"   "+  10 /* node.getXPosForPort() */ +"   "+  node.getYPosForPort(getNamedInputList().size()));
		
		node.addPort(namedInput.getPort()); // position will be set by relayoutInputPort(); 
		
		getNamedInputList().add(namedInput);
		
		inputFieldList.appendChild(hbox);
		
		if (variableInputs){
			/* make the first remove icon visible/invisible */
			hbox.appendChild(addImage(REMOVE_ICON));
			manageButtonVisibility();
		}
		
		assert(getNamedInputList().size()>0);
		node.relayoutInputPorts(getNamedInputList().size() -1 ); // only the newly added ports needs to be updated  
		node.relayout();
		return namedInput;
	}	
	/**
	 * Show the remove buttons for the inputs only if there's more than the minimum nr of inputs 
	 */
	private void manageButtonVisibility() {
		boolean visible = inputFieldList.getChildren().size() > getMinimumNrOfInputs();
		for (Object hbox : inputFieldList.getChildren()){
			((Hbox) hbox).getLastChild().setVisible(visible);
		}
	}
		
	/**
	 * @param namedInput
	 */
	public void removeInput(NamedInputIface namedInput) {

		getNamedInputList().remove(namedInput);
		namedInput.getPort().detach();
		
		Hbox hbox = (Hbox) namedInput.getParent();
		hbox.detach();
		//List<Component> children = hbox.getChildren();
		if (editableInputs){
			manageButtonVisibility();	
		}
		node.relayoutInputPorts(0);
		node.relayout();
		
	}
	
	/**
	 * Remove all inputs and their ports from the screen.  
	 *  
	 */
	public void removeAllInputs() {
		// Copy the list to array to prevent ConcurrentModificationException
		Object deletable[] = inputFieldList.getChildren().toArray();
		for (int i=0; i<deletable.length;i++){
			Hbox hbox = (Hbox) deletable[i];
			NamedInputIface namedInput = (NamedInputIface) hbox.getChildren().get(1);
			removeInput(namedInput);
		}
	}
	
	/**
	 * @return
	 */
	private int getTextboxWidth() {
		int width = node.getBaseWidth() - 18; // slightly smaller than window
		
		if (hasCheckboxes())
			width -= 12; // substract textbox width
		if (hasVariableInputs())
			width -= 15; // substract remove-button icon width 
		return width; 
	}


	public Image addImage(String src) {
		Image img = new Image(src);
		img.setWidth("14px");
		img.setHeight("14px");
		img.addEventListener("onClick", new AddRemoveListener());
		return img;
	}


	class AddRemoveListener implements org.zkoss.zk.ui.event.EventListener {
		
		public void onEvent(Event event) throws org.zkoss.zk.ui.UiException {
			if (event==null){
				// just relayout
				node.relayoutInputPorts(0);
				node.relayout();
				return;
			}
			
			Component hbox = event.getTarget().getParent();
			
			if (((Image) event.getTarget()).getSrc().equals(ADD_ICON)) {
				if (hbox == inputHeader){
					addInputName(getUnusedInputName("input"));
				}
				else
					throw new RuntimeException(
							"Where did this event come from? parent="
									+ hbox.getParent().toString());
			}	 
			else if (((Image) event.getTarget()).getSrc().equals(REMOVE_ICON)) {
				if (hbox.getParent() == inputFieldList) {
					/* only remove an input if it isn't the last one */
					if (inputFieldList.getChildren().size()>getMinimumNrOfInputs()){
						NamedInputIface namedInput = (NamedInputIface) hbox.getChildren().get(namedInputLoc);
						removeInput(namedInput);
					}
					
				} else
					System.out.println("Where did this REMOVE_ICON event come from? "+event.toString()+". Ignoring, sometimes the button is removed but still generates an event");
			}
		}

	}
	
	/**
	 * @param portName
	 * @return
	 */
	public Port getPortWithName(String portName) {
		for (Object component : inputFieldList.getChildren() ){
			NamedInputIface namedInput = (NamedInputIface) ((AbstractComponent)component).getChildren().get(namedInputLoc);
			if (namedInput.getInputName().equals(portName)){
				return namedInput.getPort();
			}
		}
		return null;
	}
	
	

	/**
	 * @return the editableInputs
	 */
	public boolean hasEditableInputs() {
		return editableInputs;
	}


	/**
	 * @param editableInputs the editableInputs to set
	 */
	public void setEditableInputs(boolean editableInputs) {
		this.editableInputs = editableInputs;
	}


	/**
	 * @return the variableInputs
	 */
	public boolean hasVariableInputs() {
		return variableInputs;
	}


	/**
	 * @param variableInputs the variableInputs to set
	 */
	public void setVariableInputs(boolean variableInputs) {
		this.variableInputs = variableInputs;
		inputHeader = new Hbox();
		inputHeader.appendChild(addImage(ADD_ICON));
		inputHeader.appendChild(new Label("add input"));
		insertBefore(inputHeader, inputFieldList);
	}

	/**
	 * @param checkboxes the checkboxes to set
	 */
	public void setCheckboxes(boolean checkboxes) {
		this.checkboxes = checkboxes;
	}

	/**
	 * @return the checkboxes
	 */
	public boolean hasCheckboxes() {
		return checkboxes;
	}

	/**
	 * @param minimumNrOfInputs the minimumNrOfInputs to set
	 */
	public void setMinimumNrOfInputs(int minimumNrOfInputs) {
		this.minimumNrOfInputs = minimumNrOfInputs;
	}

	/**
	 * @return the minimumNrOfInputs
	 */
	public int getMinimumNrOfInputs() {
		return minimumNrOfInputs;
	}


	/**
	 * @return the porttype
	 */
	public GearPort.PortType getPortType() {
		return portType;
	}

	/**
	 * @param porttype the porttype to set
	 */
	public void setPorttype(GearPort.PortType porttype) {
		this.portType = porttype;
	}

	
}
