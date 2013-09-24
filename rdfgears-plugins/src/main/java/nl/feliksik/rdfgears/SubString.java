package nl.feliksik.rdfgears;

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

import nl.tudelft.rdfgears.engine.ValueFactory;
import nl.tudelft.rdfgears.rgl.datamodel.type.BagType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RDFType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RGLType;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.function.SimplyTypedRGLFunction;
import nl.tudelft.rdfgears.util.row.ValueRow;


/**
 * Get a substring from a given string.  
 * @author Eric Feliksik
 *
 */
public class SubString extends SimplyTypedRGLFunction {
	public static String stringName = "string";
	public static String beginIndexName = "beginIndex";
	public static String endIndexName = "endIndex";
	
	public SubString(){
		requireInputType(stringName, RDFType.getInstance());
		requireInputType(beginIndexName, RDFType.getInstance());
		requireInputType(endIndexName, RDFType.getInstance());
		
	}
	
	@Override
	public RGLValue simpleExecute(ValueRow inputRow) {
		String stringVal = inputRow.get(stringName).asLiteral().getValueString();
		int start_index = (int) inputRow.get(beginIndexName).asLiteral().getValueDouble();
		int end_index = (int) inputRow.get(endIndexName).asLiteral().getValueDouble();
		
		// return untyped string 
		return ValueFactory.createLiteralTyped(stringVal.substring(start_index, end_index), null); 
	}

	@Override
	public RGLType getOutputType() {
		return BagType.getInstance(RDFType.getInstance());
	}

}
