package nl.tudelft.rdfgears.rgl.function.core;

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

import java.util.Map;

import nl.tudelft.rdfgears.rgl.datamodel.type.BooleanType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RGLType;
import nl.tudelft.rdfgears.rgl.datamodel.type.SuperTypePattern;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.exception.FunctionTypingException;
import nl.tudelft.rdfgears.rgl.function.AtomicRGLFunction;
import nl.tudelft.rdfgears.util.row.TypeRow;
import nl.tudelft.rdfgears.util.row.ValueRow;

/**
 * The if/then/else construct. 
 * 
 *  If the input for ifTrueInput is True, then the element in 'thenResult' is returned. 
 *  If the input if False, the element in 'elseResult' is returned. 
 *  If the input is Null, then Null is returned.  
 * @author Eric Feliksik
 *
 */
public class IfThenElseFunction extends AtomicRGLFunction  {
	public static String ifTrueInput = "if_true";
	public static String thenResult = "then";
	public static String elseResult = "else";
	
	
	@Override
	public RGLValue execute(ValueRow inputRow) {		
		RGLValue trueOrFalse = inputRow.get(ifTrueInput);
		if (trueOrFalse.isBoolean()){
			if (trueOrFalse.asBoolean().isTrue()){
				return inputRow.get(thenResult);
			} else {
				return inputRow.get(elseResult);
			}
		}
		
		/* not a boolean */
		assert (trueOrFalse.isNull());
		return trueOrFalse; // return the non-value
	}

	@Override
	public void initialize(Map<String, String> config) {
		requireInput(ifTrueInput);
		requireInput(thenResult);
		requireInput(elseResult);	}

	@Override
	public RGLType getOutputType(TypeRow inputTypes) throws FunctionTypingException {
		if (! inputTypes.get(ifTrueInput).isSubtypeOf(BooleanType.getInstance())){
			throw new FunctionTypingException("Must input a Boolean in the field '"+ifTrueInput+"'."); 
		}
		
		RGLType thenType = inputTypes.get(thenResult);
		RGLType elseType = inputTypes.get(elseResult);
		
		/**
		 * The If and Else clause need not necessarily return the same type. 
		 * if we receive types A and B and A is supertype of B, we return A. 
		 * 
		 * 
		 * Note that: 
		 * - If NULL is input as a type (because it was fetched from a ConstantProcessor), we return the *other* type. 
		 * - We assume that constant NULL values in the workflow have the generic SuperType. This seems ok
		 *    but it is not formally proven and should be carefully rethought. 
		 */
		
		
		RGLType returnType; 
		if (thenType.acceptsAsSubtype(elseType)){
			/* return thenType, unless it is a NULL type */
			if (thenType instanceof SuperTypePattern)
				returnType = elseType;
			else 
				returnType = thenType;
		} else if (elseType.acceptsAsSubtype(thenType)){
			/* return elseType, unless it is a NULL type */
			if (elseType instanceof SuperTypePattern)
				returnType = thenType;
			else 
				returnType = elseType; 
		} else {
			throw new FunctionTypingException("The input types for '"+thenResult+"' and '"+elseResult+"' are not interchangable (should be subtypes).");
		}
		
		return returnType;
	}

}