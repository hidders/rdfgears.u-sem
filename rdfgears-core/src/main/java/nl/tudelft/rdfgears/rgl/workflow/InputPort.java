package nl.tudelft.rdfgears.rgl.workflow;

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

import nl.tudelft.rdfgears.rgl.datamodel.type.RGLType;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.exception.WorkflowCheckingException;

/**  
 * A processor input port, that can be connected to another processor to read its input from. 
 * @author Eric Feliksik
 *
 */
public class InputPort implements Comparable {
	private String portName;
	private FunctionProcessor proc;
	private WorkflowNode inputProcessor;
	private boolean iterate;

	public InputPort(String portName, FunctionProcessor owner){
		this.portName = portName;
		this.proc = owner;
	}
	
	public String getName(){
		return this.portName;
	}
	
	public FunctionProcessor getOwnerProcessor(){
		return this.proc;
	}

	protected boolean isConnected(){
		return this.inputProcessor!=null;
	}
	
	protected RGLValue readInput(){
		assert(this.isConnected());
		return this.inputProcessor.getResultValue();
	}
	
	public void resetInput(){
		this.inputProcessor.resetProcessorCache();
	}
	
	protected RGLType getInputType() throws WorkflowCheckingException{
		assert(isConnected());
		return inputProcessor.getOutputType();
	}

	public void setInputProcessor(WorkflowNode node){
		assert(node!=null): "cannot set a 'null' input";
		
		if (inputProcessor!=null){
			boolean found = inputProcessor.removeOutputReader(this); // deliberately not inside the assertion
			assert(found); 
		}
		
		inputProcessor = node;
		inputProcessor.addOutputReader(this);
	}
	
	public WorkflowNode getInputProcessor(){
		return this.inputProcessor;
	}

	public void markIteration(){
		this.iterate = true;
		this.proc.flagIteration(this); /* notify processor */
	}
	
	/** 
	 * return true iff this port is marked for iteration
	 * @return
	 */
	public boolean iterates(){
		return this.iterate;
	}

	/**
	 * Comparable only needed to be able to map ports in a TreeMap. 
	 * Maybe later we can invesigate whether this is really faster than HashMap, and whether this is significant.  
	 */
	@Override
	public int compareTo(Object o) {
		if (! (o instanceof InputPort)){
			return 1; /* not equal, other than that it doesn't matter */
		}
		return this.getName().compareTo(((InputPort) o).getName());
	}
	
	public String toString(){
		return super.toString() + "_portname="+getName();
	}
}
