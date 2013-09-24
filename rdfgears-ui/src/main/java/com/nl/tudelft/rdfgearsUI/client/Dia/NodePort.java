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
import com.google.gwt.query.client.GQuery.Offset;
import com.nl.tudelft.rdfgearsUI.client.RGType;

public abstract class NodePort {
	protected String id;
	protected Element e;
	protected RGType type;
	protected Node parentNode;
	protected boolean isInputPort = true;
	protected boolean isActive = false;
	public NodePort(String pId, Node pNode, RGType t){
		this.id = pId;
		this.parentNode = pNode;
		this.type = t;
	}
	
	public String getId(){
		return id;
	}
	
	public Element getElement(){
		return e;
	}
	
	public RGType getType(){
		return type;
	}
	public Node getParentNode(){
		return parentNode;
	}

	
	public Offset getCenterCoordinate(){
		Offset pos = $("#" + id).offset();
		if(isInputPort){
			pos.top += (parentNode.canvas.getTopMargin() + 3); //place it in the middle of the port
			pos.left += parentNode.canvas.getLeftMargin();
		}else{
			pos.top += (parentNode.canvas.getTopMargin() + 3); //place it in the middle of the port
			pos.left += (4 + parentNode.canvas.getLeftMargin());
		}
		return pos;
	}
	public NodePort getInstance(){
		return this;
	}
	public abstract void setActive(boolean s);
	public abstract void enableEventHandler();
	public abstract boolean isConnected();
	public abstract NodePort getConnectedPort();
	public abstract Path getPath();
	
}
