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


import com.allen_sauer.gwt.log.client.Log;

import static com.google.gwt.query.client.GQuery.$;

import com.google.gwt.dom.client.Element;
import com.google.gwt.query.client.Function;
import com.google.gwt.user.client.DOM;
import com.google.gwt.xml.client.Document;

public class RGFunctionParamWorkflowTools extends RGFunctionParam{
	String openWfButtonId = "XXD9FE8G398";
	String copyWfButtonId = "XXD9FE8G399";
	Node owner;
	public RGFunctionParamWorkflowTools(String id, Node n) {
		super(id, "Tools");
		owner = n;
		Log.debug("workflow tools added for:" + id);
	}

	@Override
	void setValueFromString(String s) {}

	@Override
	void display(Element container) {
		if(elementCache == null){
			initDisplayElement();
			Element copyWf = DOM.createDiv();
			copyWf.setId(copyWfButtonId);
			copyWf.setInnerText("Copy Workflow");
			Element openWf = DOM.createDiv();
			openWf.setId(openWfButtonId);
			openWf.setInnerText("Open Workflow");
			openWf.setClassName("addFieldButton");
			copyWf.setClassName("addFieldButton");
			formContainer.appendChild(openWf);
			
			formContainer.appendChild(copyWf);
			assignHandler(pId);
			container.appendChild(elementCache);
			assignHandler(pId);
		}else {
			container.appendChild(elementCache);
		}
	}

	@Override
	void assignHandler(String id) {
//		final String wfId = id;
		$("#" + openWfButtonId).click(new Function(){
			public void f(){
				owner.canvas.openWorkflow(owner.getWorkflowId());
			}
		});
		
		$("#" + copyWfButtonId).click(new Function(){
			public void f(){
				owner.canvas.copyWorkflow(owner.getWorkflowId(), true);
			}
		});
	}

	@Override
	com.google.gwt.xml.client.Element toXml(Document doc) {
		return null;
	}

}
