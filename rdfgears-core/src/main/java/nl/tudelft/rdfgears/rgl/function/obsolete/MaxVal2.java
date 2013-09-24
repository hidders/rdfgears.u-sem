package nl.tudelft.rdfgears.rgl.function.obsolete;

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

import nl.tudelft.rdfgears.rgl.datamodel.type.RDFType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RGLType;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.exception.FunctionTypingException;
import nl.tudelft.rdfgears.rgl.function.AtomicRGLFunction;
import nl.tudelft.rdfgears.util.row.TypeRow;
import nl.tudelft.rdfgears.util.row.ValueRow;

/**
 * A simple function that takes the max value of value1 and value2 . 
 * @author Eric Feliksik
 *
 */
public class MaxVal2 extends AtomicRGLFunction  {
	public static String value1 = "value1";
	public static String value2 = "value2";
	
	
	public MaxVal2(){
		requireInput(value1);
		requireInput(value2);
	}
	

	public RGLType getOutputType(TypeRow inputTypes) throws FunctionTypingException {
		RDFType rdfType = RDFType.getInstance();
		if(! inputTypes.get(value1).isSubtypeOf(rdfType) )
			throw new FunctionTypingException(value1, rdfType, inputTypes.get(value1));
		
		if(! inputTypes.get(value2).isSubtypeOf(rdfType) )
			throw new FunctionTypingException(value2, rdfType, inputTypes.get(value2));
		
		return rdfType;
	}
	
	
	
	@Override
	public RGLValue execute(ValueRow inputRow) {
		
		RGLValue v1 = inputRow.get(value1);
		RGLValue v2 = inputRow.get(value2);
		
		double d1 = -Double.MAX_VALUE; 
		double d2 = -Double.MAX_VALUE; 
		boolean haveLiteral = false;
		
		if (v1.isLiteral()){
			d1 = v1.asLiteral().getValueDouble();
			haveLiteral = true;
		}
		if (v2.isLiteral()){
			d2 = v2.asLiteral().getValueDouble();
			haveLiteral = true;
		}
		if (haveLiteral){
			if (d1>d2){
				return v1;
			} else {
				return v2;
			}
		} else {
			return v1; // return error 
		}
		
	}


	@Override
	public void initialize(Map<String, String> config) {
		// TODO Auto-generated method stub
	}

}
