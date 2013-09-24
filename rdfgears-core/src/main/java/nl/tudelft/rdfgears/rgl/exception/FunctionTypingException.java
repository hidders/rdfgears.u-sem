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

import nl.tudelft.rdfgears.rgl.datamodel.type.BagType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RGLType;


public class FunctionTypingException extends WorkflowCheckingException {

	private static final long serialVersionUID = 1L;

	private RGLType requiredType;
	private RGLType actualType;
	
	/**
	 * 
	 * Do not use this constructor, but use the one with required and actual type instead. 
	 * 
	 * It allows a more uniform error message system. 
	 *  
	 * @deprecated
	 * @param message
	 */
	@Deprecated
	public FunctionTypingException(String message) {
		super(message);
	}
	
	public FunctionTypingException(String portName, RGLType requiredType, RGLType actualType) {
		this("Port '"+portName+"' received input of type "+actualType +", but I require "+requiredType);
		this.requiredType = requiredType;
		this.actualType = actualType;
	}
	
	public String getProblemDescription(){
		return "Typing problem: "+getMessage();
	}

	public RGLType getRequiredType() {
		return requiredType;
	}

	public RGLType getActualType() {
		return actualType;
	}
	
	/**
	 *  If the actualType is a bag of the required type, this is an iteration problem. 
	 *  
	 *  Return true if this is an iteration problem 
	 */
	public boolean isIterationProblem(){
		if (actualType instanceof BagType){
			BagType bagType = (BagType) actualType;
			RGLType elemType = bagType.getElemType();
			return (elemType.isSubtypeOf(requiredType));
		}
		return false;
	}

}
