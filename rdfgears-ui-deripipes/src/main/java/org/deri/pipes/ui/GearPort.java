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

import org.integratedmodelling.zk.diagram.components.CustomPort;
import org.integratedmodelling.zk.diagram.components.PortTypeManager;
import org.integratedmodelling.zk.diagram.components.Workspace;

/**
 * @author eric
 * modified version of GraphInputPort
 *
 */
public class GearPort extends CustomPort{
	final NamedInputIface namedInput;
	final String[] originalText = {""};
	private GearPort.PortType gearPortType; 
	
	public enum PortType {
		SOURCE_PORT,
		TARGET_PORT,
	}
	
	public GearPort(Workspace workspace, GearPort.PortType type) {
		this(workspace,type, null);
	}

	public GearPort(Workspace workspace, GearPort.PortType type, NamedInputIface namedInput) {
		this(((PipeEditor) workspace).getPTManager(), type, namedInput);
	}
	/**
	 * @param manager
	 * @param type
	 * @param position
	 * @param gearPortType TODO
	 * @param textbox2
	 */
	public GearPort(PortTypeManager manager, GearPort.PortType type, NamedInputIface namedInput) {
		super(manager, GearPort.getPipePortType(type));
		setPortType("custom");
		this.namedInput = namedInput;
		this.gearPortType = type;
	}
	
	public GearPort.PortType getGearPortType(){
		return this.gearPortType;
	}
	
	@Override
	public void detach() {
		super.detach();
		onDisconnect();
	}
	
	/** 
	 * simply translate to something known in the upper classes. 
	 * @param t
	 * @return
	 */
	private static PipePortType getPipePortType(GearPort.PortType t){
		switch(t){
			case SOURCE_PORT: 
				return PipePortType.getPType(PipePortType.RDFOUT);
			case TARGET_PORT:
				return PipePortType.getPType(PipePortType.RDFIN);
			default: return null;
		}

	}
	
	/**
	 * 
	 */
	public void onDisconnect() {
//		if (textbox!=null){
//			if(textbox.isReadonly()){
//				textbox.setText(originalText[0]);
//				textbox.setReadonly(false);
//			}	
//		}
	}
	
	/**
	 * 
	 */
	public void onConnect() {
		/*
		if(!textbox.isReadonly()){
			originalText[0] = textbox.getValue();
			textbox.setText("Text [wired]");
			textbox.setReadonly(true);
		}
		*/
		System.out.println("fixme: textbox connected");
	}
	
	/**
	 * @return
	 */
	public String getPortName(){
		if (namedInput!=null)
			return namedInput.getInputName();
		
		return "";
		/*
		switch(gearPortType){
			case NAMED_INPUT:
				assert(textbox!=null);
				return "NAMED_INPUT:"+textbox.getText().trim();
			case NAMED_OUTPUT:
				assert(textbox!=null);
				return "NAMED_OUTPUT:"+this.getParent().getId()+":"+textbox.getText().trim();
			case UNNAMED_OUTPUT:
				return this.getParent().getId()+"UNNAMED_OUTPUT:out";
			default: 
				return null;
		}
		*/
	}

	/**
	 * @return
	 */
	public String getProcessorId(){
		if (this.getParent()==null){
			throw new RuntimeException("Sorry, due to a bug in the ZKDiagram library we lost your port connection.");
		}
		return ((AbstractGearsProcessorNode)this.getParent()).getProcessorId();
	}
	

}
