package nl.tudelft.rdfgears.rgl.datamodel.value.serialization.rglxml;

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
import nl.tudelft.rdfgears.rgl.datamodel.type.TypedValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;

/**
 * This class is an implementation of the TypedValue. It is assumed that any constructor for a 
 * subclass is built as follows: 
 * - constructor accepts an InputStream (or some other object that provides input, such as an 
 * 		XMLStreamReader)
 * - constructor (calls some functions that) parses the input and internally sets the parsedValue 
 * 		and parsedType fields using the setParsedValue() and setParsedType() fields.   
 * - constructor should throw an exception if parsing fails. 
 * 
 * The value and type can be fetched after constructing the instance, using the functions defined 
 * by the TypedValue interface. 
 * 
 * @author Eric Feliksik
 *
 */
public class AbstractRGLValueParser implements TypedValue {
	private RGLValue parsedValue;
	private RGLType parsedType;
	

	/**
	 * Get the value that was parsed. 
	 */
	@Override
	public RGLValue getValue() {
		return parsedValue;
	}
	/**
	 * Get the type of the value that was parsed. 
	 */
	@Override
	public RGLType getType() {
		return parsedType;
	}
	
	
	protected void setParsedValue(RGLValue value) {
		this.parsedValue = value;
	}
	
	protected void setParsedType(RGLType type) {
		this.parsedType = type;
	}


}
