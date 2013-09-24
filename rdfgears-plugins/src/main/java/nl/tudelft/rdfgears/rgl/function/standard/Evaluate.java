package nl.tudelft.rdfgears.rgl.function.standard;

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

import nl.tudelft.rdfgears.rgl.datamodel.type.RGLType;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.visitors.ValueEvaluator;
import nl.tudelft.rdfgears.rgl.exception.FunctionTypingException;
import nl.tudelft.rdfgears.rgl.function.AtomicRGLFunction;
import nl.tudelft.rdfgears.util.row.TypeRow;
import nl.tudelft.rdfgears.util.row.ValueRow;


/**
 * This is the identity function, but it does evaluate the input recursively. 
 * 
 * Thus, it will generate possible side-effects, if they are specified in the Function. 
 * 	
 */
public class Evaluate extends AtomicRGLFunction  {
	public static String valueField = "value";
	
	public Evaluate(){
		requireInput(valueField); // any value is ok 
	}
	
	@Override
	public void initialize(Map<String, String> config) {
		// TODO Auto-generated method stub
	}

	@Override
	public RGLType getOutputType(TypeRow inputTypes)
			throws FunctionTypingException {
		return inputTypes.get(valueField);
	}

	@Override
	public RGLValue execute(ValueRow inputRow) {
		ValueEvaluator visitor = new ValueEvaluator();
		RGLValue val = inputRow.get(valueField);
		val.accept(visitor);
		return val;
	}

}
