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
import java.util.HashMap;
import java.util.HashSet;

import org.deri.pipes.utils.XMLUtil;
import org.integratedmodelling.zk.diagram.components.Port;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Space;
import org.zkoss.zul.Vbox;
public class OutPipeNode extends PipeNode {
	final Logger logger = LoggerFactory.getLogger(OutPipeNode.class);
		
	    protected Port input =null;
		public OutPipeNode(int x,int y){
			super(x,y,200,25);

			Vbox vbox;
			Hbox functionHeader = new Hbox(); // first element, can be referenced by subclass
			Label headerLabel = new Label();
			wnd.appendChild(vbox = new Vbox());
			vbox.appendChild(new Space());
			vbox.appendChild(functionHeader);
			functionHeader.appendChild(new Space());
			functionHeader.appendChild(headerLabel);
			headerLabel.setStyle("font-weight: bold");
			headerLabel.setValue("Workflow output");
			
//			this.insertBefore(new Space(), functionHeader);
//			setOperationName("Workflow output");
//			//wnd.setTitle("Output");
//			
			
		}

		protected void initialize(){
			input =createPort(PipePortType.ANYIN,"top");
			input.setMaxConnections(1);
		}
		
		public Port getInputPort(){
			return input;
		}
		
		public Element getSrcCode(Element workflowElement,boolean includePosition){
			((PipeEditor)getWorkspace()).removeParameters();
			Document doc = workflowElement.getOwnerDocument();
			
			Element networkElm = doc.createElement("network");
			workflowElement.appendChild(networkElm);
			
			if (includePosition)
				setPosition(networkElm);
			
			/* set to collect all nodes in; this prevents duplication */
			HashSet<AbstractGearsNode> doneSet = new HashSet<AbstractGearsNode>();
						
			/***
			 * Hack to remove duplicate processor id's
			 */
			HashMap<String, AbstractGearsNode> idSet = new HashMap<String, AbstractGearsNode>();
			for (Object obj : getWorkspace().getChildren()){
				if (obj instanceof AbstractGearsProcessorNode){
					AbstractGearsProcessorNode node = (AbstractGearsProcessorNode) obj;
					while (idSet.containsKey(node.getProcessorId())){
						/* hmm, a duplicate came into existence. This is due to the fact id's are generated in 
						 * AbstractGearsProcessorNode with a counter.
						 */
						node.setProcessorId(node.getProcessorId()+"_"); // add _ until it is unique
					}
					idSet.put(node.getProcessorId(), node);
				}
			}
			
			/* find the output processor connected to this OutPipeNode, serialize it*/
			for(Port p : getWorkspace().getIncomingConnections(input.getUuid())){ // this *should* be only one element 
				if(p.getParent() instanceof AbstractGearsProcessorNode){
					AbstractGearsProcessorNode node = (AbstractGearsProcessorNode) p.getParent();
					networkElm.setAttribute("output", node.getProcessorId());
					
					// recursively call addSrcCode()
					node.addSrcCode(workflowElement, doneSet, includePosition);
				}
			}
//			
			/**
			 * The first approach was to recursively add child nodes (directly above). 
			 * But sometimes you want to draw unconnected nodes and save them (if you do drawing-work 
			 * in progress, and particularly as a workaround to fix the corrupted connected-lines state 
			 * of the buggy UI). 
			 *  
			 * So we should actually serialize all nodes on the canvas. Unconnected nodes won't affect execution.
			 *  
			 */
			for (Object obj :  getWorkspace().getChildren()){
				if (obj instanceof AbstractGearsProcessorNode){
					AbstractGearsProcessorNode node = (AbstractGearsProcessorNode) obj;
					
					node.addSrcCode(workflowElement, doneSet, includePosition);
				}
			}
			
//			Element paraElm =doc.createElement("parameters");
//			ArrayList<ParameterNode> paraList=((PipeEditor)getWorkspace()).getParameters();
//			for(int i=0;i<paraList.size();i++){
//				paraElm.appendChild(paraList.get(i).getSrcCode(workflowElement, includePosition));
//			}
//			workflowElement .appendChild(paraElm);
			return workflowElement ;
		}
		
		public static PipeNode loadConfig(Element elm,PipeEditor wsp){
			OutPipeNode node=new OutPipeNode(Integer.parseInt(elm.getAttribute("x")),Integer.parseInt(elm.getAttribute("y")));
			wsp.addFigure(node);
			wsp.setOutput(node);
		    PipeNode nextNode=PipeNode.loadConfig(XMLUtil.getFirstSubElement(elm),wsp);
		    nextNode.connectTo(node.getInputPort());
			return node;
		}
		
			
		public void debug(){	   
			   ((PipeEditor)getWorkspace()).debug(getSrcCode(false));
		}

		/* (non-Javadoc)
		 * @see org.deri.pipes.ui.PipeNode#connectTo(org.integratedmodelling.zk.diagram.components.Port)
		 */
		@Override
		public void connectTo(Port port) {
			logger.warn("The output pipe node should not be invoked to connect()");
		}

		/* (non-Javadoc)
		 * @see org.deri.pipes.ui.PipeNode#getTagName()
		 */
		@Override
		public String getTagName() {
			return "output";
		}
	}