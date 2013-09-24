package com.nl.tudelft.rdfgearsUI.client.Dia;

/*
 * #%L
 * RDFGears
 * %%
 * Copyright (C) 2013 WIS group at the TU Delft (http://www.wis.ewi.tudelft.nl/)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */


import static com.google.gwt.query.client.GQuery.$;

import com.google.gwt.dom.client.Element;
import com.google.gwt.query.client.Function;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTMLPanel;

public class RGTab {
	private Element root, label, closeButton;
	private RGWorkflow myWorkflow;
	private RGCanvas canvas;
	private String labelId, closeButtonId;
	public RGTab(RGWorkflow wf, RGCanvas c){
		myWorkflow = wf;
		canvas = c;
		root = DOM.createDiv();
		root.setId(canvas.createUniqueId());
		root.setClassName("workflowTab");
		root.setAttribute("style", "position:relative;" +
								   "display: inline-block;" +
								   "width:100px;" +
								   "height:20px;" +
								   "margin-right:2px;");
		labelId = HTMLPanel.createUniqueId();
		label = DOM.createDiv();
		label.setId(labelId);
		label.setInnerText(myWorkflow.getName());
		label.setAttribute("style", "position:absolute;" +
									"left:3px;" +
									"bottom:1px;" +
									"width:77px;" +
									"height:17px;" +
									"overflow:hidden;" +
									"white-space: nowrap;" +
									"text-overflow:ellipsis;");
		root.appendChild(label);
		
		closeButtonId = HTMLPanel.createUniqueId();
		closeButton = DOM.createDiv();
		closeButton.setId(closeButtonId);
		closeButton.addClassName("tabCloseButton");
		closeButton.setAttribute("style", "position:absolute;" +
									"width:19px;" +
									"height:19px;" +
									"top:1px;" +
									"right:2px;");
		
		root.appendChild(closeButton);
		
	}
	
	public Element getElement(){
		return root;
	}
	
	public void refresh(){
		label.setInnerText(myWorkflow.getName());
	}
	public void remove(){
		root.removeFromParent();
	}
	public void setActive(boolean v){
		if(v)
			root.addClassName("activeWorkflowTab");
		else
			root.removeClassName("activeWorkflowTab");
	}
	
	public void enableEventHandler(){
		$("#" + labelId).click(new Function(){
			public void f(){
				canvas.setActiveWorkflow(myWorkflow);
			}
		});
		
		$("#" + closeButtonId).click(new Function(){
			public void f(){
				canvas.closeWorkflow(myWorkflow);
			}
		});
		
		$("#" + closeButtonId).mouseover(new Function(){
			public void f(){
			}
		});
		
		$("#" + closeButtonId).mouseout(new Function(){
			public void f(){
			}
		});
	}

}
