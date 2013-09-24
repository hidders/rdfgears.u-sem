package nl.tudelft.rdfgears.rgl.exception;

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

import nl.tudelft.rdfgears.rgl.function.RGLFunction;
import nl.tudelft.rdfgears.rgl.workflow.FunctionProcessor;

public abstract class WorkflowCheckingException extends Exception {

	private static final long serialVersionUID = 1L;


	private static final String WorkflowCheckingException = null;
	

	private FunctionProcessor complainingProcessor;
	private RGLFunction complainingFunction; 
	
	
	
	public WorkflowCheckingException(String message){
		super(message);
	}

	public WorkflowCheckingException(WorkflowCheckingException cause){
		super(cause);
	}

	public FunctionProcessor getProcessor() {
		return complainingProcessor;
	}
	

	public RGLFunction getFunction() {
		return complainingFunction;
	}
	
	
	/**
	 * Register processor and function as complainers that caused the thrown exception. 
	 * Function may be null if the processor was complaining (e.g. iterate-over-nonbag-value or unconnected input). 
	 * Processor may be null if function is not executed in processor-context (root of workflow).  
	 * @param p
	 * @param f
	 */
	public void setProcessorAndFunction(FunctionProcessor p, RGLFunction f){
		this.complainingFunction = f;
		this.complainingProcessor = p;
	}
	
	/**
	 * Get the original message that describes the problem of the complaining Function/Processor
	 * @return
	 */
	public String getOriginalMessage(){
		return getMessage();
	}
	
	

	@Override
	public WorkflowCheckingException getCause(){
		return (WorkflowCheckingException) super.getCause();
	}

	
	public WorkflowCheckingException getRootCause(){
		WorkflowCheckingException wce = (WorkflowCheckingException) super.getCause();
		if (wce==null)
			return this;
		else
			return wce.getRootCause();
	}
	
	public String getProblemLocation() {
		String msg = "";
		if (getCause()!=null){
			msg += getCause().getProblemLocation() ;
		}
		
		msg += "\n\t in ";
		
		if (getFunction()!=null){
			msg += getFunction().getRole()+" "+getFunction().getFullName();
			if (getProcessor()!=null){
				msg += " used by "; 
			}
		}
		
		if (getProcessor()!=null){
			msg += "processor "+getProcessor().getId();
		}
		
		return msg;
	}
	
	
	public String getProblemDescription() {
		return 
			getOriginalMessage() + 
			getProblemLocation();
	}
	
	
}
