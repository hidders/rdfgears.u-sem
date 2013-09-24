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

import java.util.Iterator;
import java.util.Map;

import nl.tudelft.rdfgears.engine.Engine;
import nl.tudelft.rdfgears.rgl.datamodel.type.RGLType;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.exception.FunctionTypingException;
import nl.tudelft.rdfgears.util.row.TypeRow;
import nl.tudelft.rdfgears.util.row.ValueRow;



/**
 * An abstract class for RGL Functions that only accept one particular row of input types.
 * The constants on these types may be vague (e.g. a function expects SOME bag), allowing 
 * polymorphic input, but the output type does not depend on the polymorphic elements of the input 
 * type-row. 
 *  
 * An example is a SPARQL select query. It does pose requirements on the input type row,
 * i.e. it requires some RDFValues and some Graphs, and it does yield a specific type, 
 * i.e. it gives a Bag of Records with certain keys that contain RDFValues;
 * 
 * Although the output type does depend on the SPARQL query, the output type does not 
 * depend on the inputTypes. 
 *  
 * @author Eric Feliksik
 *
 */
public abstract class SimplyTypedRGLFunction extends AtomicRGLFunction {	
	TypeRow requiredInputTypes = new TypeRow();
	
	/**
	 * Give a row over the the types of the required inputs. 
	 * @return
	 */
	private TypeRow getRequiredInputTypeRow(){
		return this.requiredInputTypes;
	}
	
	/**
	 * Initialize this method with a configuration Map. 
	 * This implementation does nothing, but subclasses may override.
	 * Note that Collections.singletonMap(key, value) may help you if you have only one thing to configure. 
	 */
	public void initialize(Map<String, String> config){	/* do nothing */ }
	
	/**
	 * Require an input name and associate the required type. Using this function is easier than
	 * requireInputName, because the user does not have to provide a complex type-checking function, 
	 * but instead immediately defines the concrete type for each input. 
	 * @param field
	 * @param type
	 */
	public void requireInputType(String field, RGLType type){
		requireInput(field);
		getRequiredInputTypeRow().put(field, type);
	}
	
	/**
	 * Get the output type that will be given if this function receives well-typed input.
	 * As there is only one input typing possible and this typing is defined on 
	 * construction of a SPARQL function with the requireInputType() function, there 
	 * is only one output type possible.    
	 * @return
	 */
	public abstract RGLType getOutputType();
	
	
	@Override
	public final RGLType getOutputType(TypeRow inputTypes) throws FunctionTypingException {
		if (inputTypes.isSubtypeOf(this.getRequiredInputTypeRow())){
			return this.getOutputType();
		}
		
		/* not ok, find an offending input... */
		Iterator<String> rangeIter = this.getRequiredInputTypeRow().getRange().iterator();
		while (rangeIter.hasNext()){
			String field = rangeIter.next();
			RGLType expectedType = this.getRequiredInputTypeRow().get(field);
			RGLType actualType = inputTypes.get(field);
			if (! actualType.isSubtypeOf(expectedType)){
				throw new FunctionTypingException(field,expectedType,actualType);
			}
			
		}
		
		assert(false): "we should have found a typing error";
		return null;
	}

	/**
	 * Perform the function of this class on the input row, and return the result.
	 * 
	 * This method will be called only if all values in inputRow are non-Error values.  
	 * However, implementation should be aware that the elements in bags/records may still be Errors. 
	 * 
	 * @param inputRow
	 * @return the execution result value. 
	 */
	public abstract RGLValue simpleExecute(ValueRow inputRow);
	

	/**
	 * Perform the function of this class on the input row, and return the result.
	 * @param inputRow
	 * @return the execution result value. 
	 */
	public final RGLValue execute(ValueRow inputRow){
		for (String field : inputRow.getRange()){
			if (inputRow.get(field).isNull())
				return inputRow.get(field);
			
			/* TODO 
			 * Assess whether this is a performance issue (I don't think so).
			 * Optionally, create method ValueRow.containsError() that returns an error if present, instead
			 * of walking through all elements in the range and checking them.  
			 * Implementation may be faster than indexing all these things (because it can iterate an array) 
			 */
		}
		
		/* they are all ok */
		RGLValue result = simpleExecute(inputRow);
		if (result==null){
			Engine.getLogger().error("the function "+getFullName()+" returned <null>, this is not according to the contract!  ");
		}	
		return result; 
	}
	
	
}
