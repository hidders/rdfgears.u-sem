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
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;

/**
 * 
 * A fork of the VariableInputListVbox
 * 
 * @author Eric Feliksik
 *
 */
public class VariableTextboxVbox extends Vbox {
	
	private AbstractGearsProcessorNode node;
	private final int minimumNrOfElements = 1;
	private Hbox inputHeader;
	private Vbox hboxList = new Vbox();
//	
//	private int namedInputLoc; /* location of the NamedGraphInput element in a Hbox; set when adding the namedGraphInput */

	public static final String ADD_ICON = "img/edit_add-48x48.png";
	public static final String REMOVE_ICON = "img/edit_remove-48x48.png";
	
	public Label label;  
	
	/* a list of all the named inputs for this node (actually redundant, their also 
	 * hidden in the hboxes in inputFieldList */
	
	public VariableTextboxVbox(AbstractGearsProcessorNode node){
		this.node = node;
		
		inputHeader = new Hbox();
		inputHeader.appendChild(addImage(ADD_ICON));
		this.appendChild(inputHeader);
		this.appendChild(hboxList);
		
	}

	public int getNumberOfTextboxes(){
		return hboxList.getChildren().size();
	}

	/**
	 * Get the list of strings set by the input boxes. 
	 * @return
	 */
	public List<String> getStringList(){
		List<String> list = new ArrayList<String>();
		for (Object obj : hboxList.getChildren()){
			Hbox hbox = (Hbox) obj;
			Textbox textbox = (Textbox) hbox.getChildren().get(0);
			list.add(textbox.getValue());
		}
		return list; 
	}
	
	public void setInputHeaderText(String txt){
		if (label!=null)
			inputHeader.removeChild(label);
		inputHeader.appendChild(label = new Label(txt));
		
	}
	
	public void addTextBox() {
		addTextBox("");
	}
	public void addTextBox(String contents) {
		/* add an input element and iteration-marker for input with the given name */
		Hbox hbox = new Hbox();
		Textbox textbox = new Textbox(contents);
		textbox.setWidth("80px");
		hbox.setAlign("center");
		
		hbox.appendChild(textbox);
		
		/* make the first remove icon visible/invisible */
		hbox.appendChild(addImage(REMOVE_ICON));
		
		hboxList.appendChild(hbox);
		manageButtonVisibility();
		
		node.relayout();
	}	
	/**
	 * Show the remove buttons for the inputs only if there's more than the minimum nr of inputs 
	 */
	private void manageButtonVisibility() {
		boolean visible = hboxList.getChildren().size() > minimumNrOfElements ;
		for (Object hbox : hboxList.getChildren()){
			((Hbox) hbox).getLastChild().setVisible(visible);
		}
	}
		
	/**
	 * @param namedInput
	 */
	public void removeInput(int index) {
		Hbox hbox = (Hbox) hboxList.getChildren().get(index);
		Textbox textbox = (Textbox) hbox.getChildren().get(0);
		textbox.detach();
		hbox.detach();
		manageButtonVisibility();	
		node.relayout();
	}
	
	/**
	 * Remove all boxes  
	 *  
	 */
	public void removeAllBoxes() {
		// Copy the list to array to prevent ConcurrentModificationException
		Object deletable[] = hboxList.getChildren().toArray();
		for (int i=0; i<deletable.length;i++){
			removeInput(i);
		}
	}
	
	/**
	 * @return
	 */
	private int getTextboxWidth() {
		int width = node.getBaseWidth() - 18; // slightly smaller than window
		
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
					addTextBox();
				}
				else
					throw new RuntimeException(
							"Where did this event come from? parent="
									+ hbox.getParent().toString());
			}	 
			
			if (((Image) event.getTarget()).getSrc().equals(REMOVE_ICON)) {
				if (hbox.getParent() == hboxList) {
					/* only remove an input if it isn't the last one */
					List children = hboxList.getChildren();
					if (children.size()> minimumNrOfElements ){
						
						/* find the inputbox in the list  */
						for (int i=0; i<children.size(); i++){
							Hbox listElem = (Hbox) children.get(i);
							if (hbox == listElem){
								/* remove that one! */
								removeInput(i);
								break;
							}
						}
					}
					
				} else
					System.out.println("Where did this REMOVE_ICON event come from? "+event.toString()+". Ignoring, sometimes the button is removed but still generates an event");
			}
		}

	}


	
}
