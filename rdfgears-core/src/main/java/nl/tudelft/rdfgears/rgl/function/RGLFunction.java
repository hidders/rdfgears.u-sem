package nl.tudelft.rdfgears.rgl.function;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nl.tudelft.rdfgears.rgl.datamodel.type.RGLType;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.exception.WorkflowCheckingException;
import nl.tudelft.rdfgears.rgl.exception.WorkflowLoadingException;
import nl.tudelft.rdfgears.util.row.FieldIndexMap;
import nl.tudelft.rdfgears.util.row.FieldIndexMapFactory;
import nl.tudelft.rdfgears.util.row.TypeRow;
import nl.tudelft.rdfgears.util.row.ValueRow;


/**
 * An RGLFunction can convert a Row<RGLValue> to an output RGLValue
 * with the execute function. An instance of RGLFunction can be reused
 * for many ReadingProcessors. 
 * 
 * @author Eric Feliksik
 *
 */
public abstract class RGLFunction {
	
	private FieldIndexMap fieldIndexMap = null;
	private ArrayList<String> requiredInputList = new ArrayList<String>(); // contains same variables as fieldIndexMap, but ordered
	
	public boolean isLazy = true;
	
	/** 
	 * Initialize the function with a configuration map. 
	 * 
	 * Reconsider whether this function is necessary;;; very few functions need it (standard functions, 
	 * e.g. record-project). Maybe it should be a separate class extending RGLFunction.   
	 */
	public abstract void initialize(Map<String, String> config) throws WorkflowLoadingException;
	
	/**
	 * Perform the function of this class on the input row, and return the result.
	 * @param inputRow
	 * @return the execution result value. 
	 */
	public abstract RGLValue execute(ValueRow inputRow);
	
	/** 
	 * The typechecking mechanism for this function.
	 * given a row over types, return the output type. 
	 * If the processor is not defined for the input row, it must throw a TypingException.
	 * This function should never return null.  
	 * @param inputTypes a mapping of names to input types.
	 * @return the output type
	 * @throws WorkflowCheckingException 
	 */
	public abstract RGLType getOutputType(TypeRow inputTypes) throws WorkflowCheckingException;
	
	public final List<String> getRequiredInputNames(){
		return requiredInputList;
	}

	/**
	 * 
	 * @return
	 */
	public final FieldIndexMap getFieldIndexMap(){
		if (fieldIndexMap==null)
			fieldIndexMap = FieldIndexMapFactory.create(requiredInputList);
		return this.fieldIndexMap;
	}
	
	/**
	 * register an input name in this function.
	 * Should first use this function to set all names, and THEN you can call other functions, like 
	 * getNumberOfArguments(), getArgumentNumber(), getRequiredInputNames(), etcetera. 
	 * @param field
	 */
	public final void requireInput(String field){
		if (! requiredInputList.contains(field)){
			requiredInputList.add(field);
		}
	}

	public boolean isLazy() {
		return this.isLazy ;
	}
	
	public String getShortName() {
		String packageName = getClass().getPackage().getName();
		return getFullName().substring(packageName.length()+1); // remove package name and the dot.
	}
	
	public String getFullName() {
		return getClass().getCanonicalName();
	}
	
	public String getRole(){
		return "java-function";
	}
}
